package br.com.clcmo.walking.domain

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/** Distância entre dois pontos pela fórmula de Haversine, em metros. */
fun distanciaEntreM(a: PontoGPS, b: PontoGPS): Double {
    val raioTerraM = 6_371_000.0
    val dLat = Math.toRadians(b.latitude - a.latitude)
    val dLon = Math.toRadians(b.longitude - a.longitude)
    val h = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(a.latitude)) * cos(Math.toRadians(b.latitude)) * sin(dLon / 2).pow(2)
    return 2 * raioTerraM * asin(min(1.0, sqrt(h)))
}

/** Ritmo em min/km, ou null enquanto a distância for curta demais para fazer sentido. */
fun ritmoMinPorKm(distanciaM: Double, duracaoMs: Long): Double? {
    if (distanciaM < 10.0 || duracaoMs <= 0) return null
    return (duracaoMs / 60_000.0) / (distanciaM / 1_000.0)
}

/**
 * Acumula a distância do percurso ponto a ponto.
 *
 * No app de 2018 a distância era medida sempre a partir do primeiro ponto
 * (linha reta). Aqui cada ponto aceito soma o trecho desde o ponto aceito
 * anterior, então uma volta completa conta o caminho todo.
 */
class CalculadoraPercurso(
    private val precisaoMaximaM: Float = 20f,
    private val deslocamentoMinimoM: Double = 3.0,
) {
    private var ultimo: PontoGPS? = null

    var distanciaM: Double = 0.0
        private set

    /** Retorna true se o ponto foi aceito. Pontos imprecisos ou parados são descartados. */
    fun adicionar(ponto: PontoGPS): Boolean {
        if (ponto.precisaoM > precisaoMaximaM) return false
        val anterior = ultimo
        if (anterior == null) {
            ultimo = ponto
            return true
        }
        val trecho = distanciaEntreM(anterior, ponto)
        if (trecho < deslocamentoMinimoM) return false
        distanciaM += trecho
        ultimo = ponto
        return true
    }

    /**
     * Esquece o último ponto sem zerar a distância. Usado ao retomar uma pausa:
     * o trecho andado durante a pausa não deve ser somado.
     */
    fun reancorar() {
        ultimo = null
    }

    fun reiniciar() {
        ultimo = null
        distanciaM = 0.0
    }
}
