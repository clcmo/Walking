package br.com.clcmo.walking.ui.hoje

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.clcmo.walking.R
import br.com.clcmo.walking.ui.theme.AzulCeu
import br.com.clcmo.walking.ui.theme.AzulMedio
import br.com.clcmo.walking.ui.theme.Marinho
import br.com.clcmo.walking.ui.theme.WalkingTheme

@Composable
fun TelaHoje(onIniciar: () -> Unit, modifier: Modifier = Modifier) {
    // Valores fixos por enquanto. Passam a vir do histórico (Room) no marco 4.
    val metaMin = 30
    val feitoMin = 0

    var faltaPermissao by remember { mutableStateOf(false) }
    val pedirPermissoes = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { resultado ->
        // Localização aproximada não basta: o filtro de precisão descartaria os pontos.
        val precisaConcedida = resultado[Manifest.permission.ACCESS_FINE_LOCATION] == true
        faltaPermissao = !precisaConcedida
        if (precisaConcedida) onIniciar()
    }
    val permissoes = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
    }.toTypedArray()

    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.goal_label),
            style = MaterialTheme.typography.titleLarge,
        )

        AnelDeProgresso(
            progresso = feitoMin / metaMin.toFloat(),
            valor = feitoMin.toString(),
            legenda = stringResource(R.string.progress_of_goal, metaMin),
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = { pedirPermissoes.launch(permissoes) },
                shape = CircleShape,
                modifier = Modifier.size(104.dp),
            ) {
                Text(
                    text = stringResource(R.string.start),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(Modifier.height(8.dp))
            if (faltaPermissao) {
                Text(
                    text = stringResource(R.string.permission_needed),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

/**
 * Dois círculos sobrepostos, como na logo, dentro de um anel de progresso.
 * O texto fica sobre o círculo claro, então usa sempre Marinho (nos dois temas).
 */
@Composable
fun AnelDeProgresso(
    progresso: Float,
    valor: String,
    legenda: String,
    modifier: Modifier = Modifier,
) {
    val trilha = MaterialTheme.colorScheme.surfaceVariant
    val arco = MaterialTheme.colorScheme.primary

    Box(modifier.size(280.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val lado = size.minDimension
            val centro = Offset(lado / 2, lado / 2)

            drawCircle(AzulMedio, radius = lado * 0.27f, center = Offset(lado * 0.40f, lado * 0.40f))
            drawCircle(AzulCeu, radius = lado * 0.30f, center = centro)

            val espessura = 10.dp.toPx()
            val raio = lado * 0.46f
            val topoEsquerdo = Offset(centro.x - raio, centro.y - raio)
            val tamanho = Size(raio * 2, raio * 2)
            drawArc(
                color = trilha,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topoEsquerdo,
                size = tamanho,
                style = Stroke(width = espessura),
            )
            if (progresso > 0f) {
                drawArc(
                    color = arco,
                    startAngle = -90f,
                    sweepAngle = 360f * progresso.coerceAtMost(1f),
                    useCenter = false,
                    topLeft = topoEsquerdo,
                    size = tamanho,
                    style = Stroke(width = espessura, cap = StrokeCap.Round),
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, style = MaterialTheme.typography.displayMedium, color = Marinho)
            Text(legenda, style = MaterialTheme.typography.bodyMedium, color = Marinho)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTelaHoje() {
    WalkingTheme { TelaHoje(onIniciar = {}) }
}
