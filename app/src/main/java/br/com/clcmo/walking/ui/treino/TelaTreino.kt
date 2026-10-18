package br.com.clcmo.walking.ui.treino

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.clcmo.walking.R
import br.com.clcmo.walking.domain.FaseTreino
import br.com.clcmo.walking.ui.formatarDistanciaKm
import br.com.clcmo.walking.ui.formatarDuracao
import br.com.clcmo.walking.ui.formatarRitmo
import br.com.clcmo.walking.ui.theme.WalkingTheme

@Composable
fun TelaTreino(
    ui: TreinoUi,
    onPausar: () -> Unit,
    onRetomar: () -> Unit,
    onFinalizar: () -> Unit,
    onConcluir: () -> Unit,
) {
    val titulo = when (ui.fase) {
        FaseTreino.Pausado -> R.string.phase_paused
        FaseTreino.Finalizado -> R.string.phase_finished
        else -> R.string.phase_running
    }

    Scaffold { margens ->
        Column(
            modifier = Modifier.padding(margens).fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(titulo), style = MaterialTheme.typography.titleLarge)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                Metrica(R.string.label_time, formatarDuracao(ui.duracaoMs), destaque = true)
                Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                    Metrica(
                        R.string.label_distance,
                        stringResource(R.string.distance_value, formatarDistanciaKm(ui.distanciaM)),
                    )
                    Metrica(
                        R.string.label_pace,
                        ui.ritmoMinKm?.let { stringResource(R.string.pace_value, formatarRitmo(it)) } ?: "--",
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                when (ui.fase) {
                    FaseTreino.Rodando -> {
                        Button(onClick = onPausar, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.pause))
                        }
                        OutlinedButton(onClick = onFinalizar, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.finish))
                        }
                    }
                    FaseTreino.Pausado -> {
                        Button(onClick = onRetomar, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.resume))
                        }
                        OutlinedButton(onClick = onFinalizar, modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.finish))
                        }
                    }
                    else -> Button(onClick = onConcluir, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.done))
                    }
                }
            }
        }
    }
}

@Composable
private fun Metrica(@StringRes rotulo: Int, valor: String, destaque: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(rotulo),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = valor,
            style = if (destaque) MaterialTheme.typography.displayMedium else MaterialTheme.typography.headlineSmall,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTelaTreino() {
    WalkingTheme {
        TelaTreino(
            ui = TreinoUi(FaseTreino.Rodando, distanciaM = 1520.0, duracaoMs = 905_000L, ritmoMinKm = 9.9),
            onPausar = {}, onRetomar = {}, onFinalizar = {}, onConcluir = {},
        )
    }
}
