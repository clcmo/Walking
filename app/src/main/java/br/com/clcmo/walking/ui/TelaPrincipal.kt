package br.com.clcmo.walking.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.clcmo.walking.R
import br.com.clcmo.walking.domain.FaseTreino
import br.com.clcmo.walking.ui.hoje.TelaHoje
import br.com.clcmo.walking.ui.treino.TelaTreino
import br.com.clcmo.walking.ui.treino.TreinoViewModel

private enum class Aba(@StringRes val rotulo: Int, val icone: ImageVector) {
    Hoje(R.string.tab_today, Icons.Filled.Home),
    Historico(R.string.tab_history, Icons.AutoMirrored.Filled.List),
    Perfil(R.string.tab_profile, Icons.Filled.Person),
}

@Composable
fun TelaPrincipal(viewModel: TreinoViewModel = hiltViewModel()) {
    val treino by viewModel.ui.collectAsStateWithLifecycle()

    // Navigation Compose entra no marco 3. Por ora, o treino ocupa a tela toda.
    if (treino.fase == FaseTreino.Parado) {
        AbasPrincipais(onIniciar = viewModel::iniciar)
    } else {
        TelaTreino(
            ui = treino,
            onPausar = viewModel::pausar,
            onRetomar = viewModel::retomar,
            onFinalizar = viewModel::finalizar,
            onConcluir = viewModel::concluir,
        )
    }
}

@Composable
private fun AbasPrincipais(onIniciar: () -> Unit) {
    var abaAtual by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Aba.entries.forEachIndexed { indice, aba ->
                    NavigationBarItem(
                        selected = abaAtual == indice,
                        onClick = { abaAtual = indice },
                        icon = { Icon(aba.icone, contentDescription = null) },
                        label = { Text(stringResource(aba.rotulo)) },
                    )
                }
            }
        },
    ) { margens ->
        Box(Modifier.padding(margens).fillMaxSize()) {
            when (Aba.entries[abaAtual]) {
                Aba.Hoje -> TelaHoje(onIniciar = onIniciar)
                Aba.Historico -> TextoCentral(R.string.history_empty)
                Aba.Perfil -> TextoCentral(R.string.profile_soon)
            }
        }
    }
}

@Composable
private fun TextoCentral(@StringRes texto: Int) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Text(stringResource(texto))
    }
}
