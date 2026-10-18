package br.com.clcmo.walking.domain

enum class FaseTreino { Parado, Rodando, Pausado, Finalizado }

/**
 * Estado do treino. O tempo é guardado como "acumulado até a última retomada"
 * mais o relógio da retomada, para a duração poder ser calculada a qualquer
 * momento sem precisar de um timer rodando (e sobreviver à rotação da tela).
 */
data class EstadoTreino(
    val fase: FaseTreino = FaseTreino.Parado,
    val distanciaM: Double = 0.0,
    val acumuladoMs: Long = 0L,
    val retomadoEmMs: Long = 0L,
) {
    fun duracaoMs(agoraMs: Long): Long =
        if (fase == FaseTreino.Rodando) {
            acumuladoMs + (agoraMs - retomadoEmMs).coerceAtLeast(0L)
        } else {
            acumuladoMs
        }
}
