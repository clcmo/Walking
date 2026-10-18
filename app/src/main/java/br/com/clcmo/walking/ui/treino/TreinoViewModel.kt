package br.com.clcmo.walking.ui.treino

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.clcmo.walking.data.TreinoRepository
import br.com.clcmo.walking.domain.EstadoTreino
import br.com.clcmo.walking.domain.FaseTreino
import br.com.clcmo.walking.domain.ritmoMinPorKm
import br.com.clcmo.walking.service.TreinoService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class TreinoUi(
    val fase: FaseTreino = FaseTreino.Parado,
    val distanciaM: Double = 0.0,
    val duracaoMs: Long = 0L,
    val ritmoMinKm: Double? = null,
)

private fun EstadoTreino.paraUi(agoraMs: Long): TreinoUi {
    val duracao = duracaoMs(agoraMs)
    return TreinoUi(fase, distanciaM, duracao, ritmoMinPorKm(distanciaM, duracao))
}

@HiltViewModel
class TreinoViewModel @Inject constructor(
    @ApplicationContext private val contexto: Context,
    private val repositorio: TreinoRepository,
) : ViewModel() {

    /** Reemite a cada segundo enquanto o treino roda, para o cronômetro andar. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val ui: StateFlow<TreinoUi> = repositorio.estado
        .flatMapLatest { estado ->
            if (estado.fase == FaseTreino.Rodando) {
                flow {
                    while (true) {
                        emit(estado.paraUi(SystemClock.elapsedRealtime()))
                        delay(1_000L)
                    }
                }
            } else {
                flowOf(estado.paraUi(SystemClock.elapsedRealtime()))
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = repositorio.estado.value.paraUi(SystemClock.elapsedRealtime()),
        )

    fun iniciar() = enviar(TreinoService.ACAO_INICIAR)
    fun pausar() = enviar(TreinoService.ACAO_PAUSAR)
    fun retomar() = enviar(TreinoService.ACAO_RETOMAR)
    fun finalizar() = enviar(TreinoService.ACAO_FINALIZAR)
    fun concluir() = repositorio.concluir()

    private fun enviar(acao: String) {
        val intent = Intent(contexto, TreinoService::class.java).setAction(acao)
        ContextCompat.startForegroundService(contexto, intent)
    }
}
