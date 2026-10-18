package br.com.clcmo.walking.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import br.com.clcmo.walking.MainActivity
import br.com.clcmo.walking.R
import br.com.clcmo.walking.data.TreinoRepository
import br.com.clcmo.walking.domain.FaseTreino
import br.com.clcmo.walking.domain.PontoGPS
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Mantém o GPS ligado com a tela bloqueada, como serviço em primeiro plano. */
@AndroidEntryPoint
class TreinoService : Service() {

    @Inject lateinit var repositorio: TreinoRepository

    private lateinit var cliente: FusedLocationProviderClient
    private var atualizando = false

    private val retorno = object : LocationCallback() {
        override fun onLocationResult(resultado: LocationResult) {
            for (local in resultado.locations) {
                repositorio.registrar(
                    PontoGPS(
                        latitude = local.latitude,
                        longitude = local.longitude,
                        tempoMs = local.time,
                        precisaoM = if (local.hasAccuracy()) local.accuracy else Float.MAX_VALUE,
                    ),
                )
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        cliente = LocationServices.getFusedLocationProviderClient(this)
        criarCanal()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Todo startForegroundService exige um startForeground logo em seguida,
        // inclusive nas ações de pausar, retomar e finalizar.
        ServiceCompat.startForeground(
            this,
            ID_NOTIFICACAO,
            montarNotificacao(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION,
        )

        when (intent?.action) {
            ACAO_INICIAR -> {
                repositorio.iniciar()
                comecarLocalizacao()
            }
            ACAO_PAUSAR -> {
                repositorio.pausar()
                pararLocalizacao()
                atualizarNotificacao()
            }
            ACAO_RETOMAR -> {
                repositorio.retomar()
                comecarLocalizacao()
                atualizarNotificacao()
            }
            ACAO_FINALIZAR -> encerrar()
            else -> encerrar() // religado pelo sistema sem uma ação: nada a fazer
        }
        return START_NOT_STICKY
    }

    private fun encerrar() {
        if (repositorio.estado.value.fase == FaseTreino.Rodando ||
            repositorio.estado.value.fase == FaseTreino.Pausado
        ) {
            repositorio.finalizar()
        }
        pararLocalizacao()
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @SuppressLint("MissingPermission") // a tela só inicia o serviço com a permissão concedida
    private fun comecarLocalizacao() {
        if (atualizando) return
        val pedido = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2_000L)
            .setMinUpdateIntervalMillis(1_000L)
            .build()
        try {
            cliente.requestLocationUpdates(pedido, retorno, Looper.getMainLooper())
            atualizando = true
        } catch (e: SecurityException) {
            encerrar()
        }
    }

    private fun pararLocalizacao() {
        if (!atualizando) return
        cliente.removeLocationUpdates(retorno)
        atualizando = false
    }

    override fun onDestroy() {
        pararLocalizacao()
        super.onDestroy()
    }

    private fun criarCanal() {
        val canal = NotificationChannel(
            CANAL,
            getString(R.string.notif_channel),
            NotificationManager.IMPORTANCE_LOW,
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    private fun montarNotificacao(): Notification {
        val pausado = repositorio.estado.value.fase == FaseTreino.Pausado
        val abrir = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, CANAL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(if (pausado) R.string.phase_paused else R.string.phase_running))
            .setContentIntent(abrir)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun atualizarNotificacao() {
        getSystemService(NotificationManager::class.java).notify(ID_NOTIFICACAO, montarNotificacao())
    }

    companion object {
        const val ACAO_INICIAR = "br.com.clcmo.walking.INICIAR"
        const val ACAO_PAUSAR = "br.com.clcmo.walking.PAUSAR"
        const val ACAO_RETOMAR = "br.com.clcmo.walking.RETOMAR"
        const val ACAO_FINALIZAR = "br.com.clcmo.walking.FINALIZAR"

        private const val ID_NOTIFICACAO = 1
        private const val CANAL = "treino"
    }
}
