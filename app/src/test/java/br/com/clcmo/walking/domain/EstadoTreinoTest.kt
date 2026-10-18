package br.com.clcmo.walking.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class EstadoTreinoTest {

    @Test
    fun duracaoSomaOTempoDesdeARetomadaQuandoRodando() {
        val estado = EstadoTreino(FaseTreino.Rodando, acumuladoMs = 60_000L, retomadoEmMs = 100_000L)
        assertEquals(90_000L, estado.duracaoMs(agoraMs = 130_000L))
    }

    @Test
    fun duracaoFicaParadaQuandoPausado() {
        val estado = EstadoTreino(FaseTreino.Pausado, acumuladoMs = 60_000L, retomadoEmMs = 100_000L)
        assertEquals(60_000L, estado.duracaoMs(agoraMs = 999_000L))
    }

    @Test
    fun relogioParaTrasNaoDaDuracaoNegativa() {
        val estado = EstadoTreino(FaseTreino.Rodando, acumuladoMs = 10_000L, retomadoEmMs = 50_000L)
        assertEquals(10_000L, estado.duracaoMs(agoraMs = 40_000L))
    }
}
