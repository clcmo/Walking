package br.com.clcmo.walking.data

import android.os.SystemClock
import br.com.clcmo.walking.domain.CalculadoraPercurso
import br.com.clcmo.walking.domain.EstadoTreino
import br.com.clcmo.walking.domain.FaseTreino
import br.com.clcmo.walking.domain.PontoGPS
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fonte única do treino em andamento. O serviço escreve aqui; a tela só observa.
 * Todas as chamadas acontecem na thread principal (callbacks de localização
 * usam o Looper principal), por isso não há sincronização extra.
 */
@Singleton
class TreinoRepository @Inject constructor() {

    private val calculadora = CalculadoraPercurso()
    private val _estado = MutableStateFlow(EstadoTreino())
    val estado: StateFlow<EstadoTreino> = _estado.asStateFlow()

    fun iniciar(agoraMs: Long = SystemClock.elapsedRealtime()) {
        if (_estado.value.fase == FaseTreino.Rodando) return
        calculadora.reiniciar()
        _estado.value = EstadoTreino(fase = FaseTreino.Rodando, retomadoEmMs = agoraMs)
    }

    fun pausar(agoraMs: Long = SystemClock.elapsedRealtime()) {
        val atual = _estado.value
        if (atual.fase != FaseTreino.Rodando) return
        _estado.value = atual.copy(
            fase = FaseTreino.Pausado,
            acumuladoMs = atual.duracaoMs(agoraMs),
        )
    }

    fun retomar(agoraMs: Long = SystemClock.elapsedRealtime()) {
        val atual = _estado.value
        if (atual.fase != FaseTreino.Pausado) return
        calculadora.reancorar()
        _estado.value = atual.copy(fase = FaseTreino.Rodando, retomadoEmMs = agoraMs)
    }

    fun finalizar(agoraMs: Long = SystemClock.elapsedRealtime()) {
        val atual = _estado.value
        if (atual.fase != FaseTreino.Rodando && atual.fase != FaseTreino.Pausado) return
        _estado.value = atual.copy(
            fase = FaseTreino.Finalizado,
            acumuladoMs = atual.duracaoMs(agoraMs),
        )
    }

    /** Fecha o resumo e volta ao estado inicial. */
    fun concluir() {
        calculadora.reiniciar()
        _estado.value = EstadoTreino()
    }

    fun registrar(ponto: PontoGPS) {
        if (_estado.value.fase != FaseTreino.Rodando) return
        if (calculadora.adicionar(ponto)) {
            _estado.value = _estado.value.copy(distanciaM = calculadora.distanciaM)
        }
    }
}
