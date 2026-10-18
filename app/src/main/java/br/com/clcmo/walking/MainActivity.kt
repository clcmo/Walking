package br.com.clcmo.walking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.clcmo.walking.ui.TelaPrincipal
import br.com.clcmo.walking.ui.theme.WalkingTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalkingTheme {
                TelaPrincipal()
            }
        }
    }
}
