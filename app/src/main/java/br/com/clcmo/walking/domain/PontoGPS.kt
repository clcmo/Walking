package br.com.clcmo.walking.domain

/** Uma leitura de posição. Independe do Android para poder ser testada na JVM. */
data class PontoGPS(
    val latitude: Double,
    val longitude: Double,
    val tempoMs: Long,
    val precisaoM: Float,
)
