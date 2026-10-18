package br.com.clcmo.walking.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatadoresTest {

    @Test
    fun duracaoCurtaUsaMinutosESegundos() {
        assertEquals("02:05", formatarDuracao(125_000L))
        assertEquals("00:00", formatarDuracao(0L))
    }

    @Test
    fun duracaoLongaIncluiHoras() {
        assertEquals("1:02:05", formatarDuracao(3_725_000L))
    }

    @Test
    fun ritmoEmMinutosESegundos() {
        assertEquals("6:30", formatarRitmo(6.5))
        assertEquals("6:00", formatarRitmo(5.999))
    }
}
