package br.com.clcmo.walking.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculadoraPercursoTest {

    private fun ponto(lat: Double, lon: Double = 0.0, precisao: Float = 5f) =
        PontoGPS(lat, lon, tempoMs = 0L, precisaoM = precisao)

    @Test
    fun primeiroPontoNaoSomaDistancia() {
        val calc = CalculadoraPercurso()
        assertTrue(calc.adicionar(ponto(0.0)))
        assertEquals(0.0, calc.distanciaM, 0.0)
    }

    @Test
    fun idaEVoltaContaOCaminhoTodo() {
        // O app de 2018 mostraria ~0 m aqui, pois comparava sempre com o ponto inicial.
        val calc = CalculadoraPercurso()
        calc.adicionar(ponto(0.0))
        calc.adicionar(ponto(0.001))
        calc.adicionar(ponto(0.0))
        assertEquals(222.39, calc.distanciaM, 0.5)
    }

    @Test
    fun descartaPontoComPrecisaoRuim() {
        val calc = CalculadoraPercurso(precisaoMaximaM = 20f)
        calc.adicionar(ponto(0.0))
        assertFalse(calc.adicionar(ponto(0.001, precisao = 50f)))
        assertEquals(0.0, calc.distanciaM, 0.0)
    }

    @Test
    fun ignoraRuidoMenorQueODeslocamentoMinimo() {
        val calc = CalculadoraPercurso(deslocamentoMinimoM = 3.0)
        calc.adicionar(ponto(0.0))
        assertFalse(calc.adicionar(ponto(0.00001))) // ~1,1 m
        assertEquals(0.0, calc.distanciaM, 0.0)
    }

    @Test
    fun reiniciarZeraTudo() {
        val calc = CalculadoraPercurso()
        calc.adicionar(ponto(0.0))
        calc.adicionar(ponto(0.001))
        calc.reiniciar()
        assertEquals(0.0, calc.distanciaM, 0.0)
        assertTrue(calc.adicionar(ponto(0.002)))
        assertEquals(0.0, calc.distanciaM, 0.0)
    }

    @Test
    fun ritmoEmMinutosPorKm() {
        assertEquals(6.0, ritmoMinPorKm(1_000.0, 360_000L)!!, 1e-9)
    }

    @Test
    fun ritmoIndefinidoParaDistanciaCurta() {
        assertNull(ritmoMinPorKm(5.0, 60_000L))
        assertNotNull(ritmoMinPorKm(50.0, 60_000L))
    }

    @Test
    fun reancorarNaoSomaOTrechoAndadoDuranteAPausa() {
        val calc = CalculadoraPercurso()
        calc.adicionar(ponto(0.0))
        calc.adicionar(ponto(0.001)) // ~111 m
        val antes = calc.distanciaM
        calc.reancorar()
        calc.adicionar(ponto(0.010)) // chegou longe durante a pausa: vira o novo ponto de partida
        assertEquals(antes, calc.distanciaM, 0.0)
        calc.adicionar(ponto(0.011)) // ~111 m a mais
        assertEquals(antes + 111.19, calc.distanciaM, 0.5)
    }
}
