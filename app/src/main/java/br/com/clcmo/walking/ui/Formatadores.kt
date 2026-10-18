package br.com.clcmo.walking.ui

import java.util.Locale

/** 125 s vira "02:05"; 3725 s vira "1:02:05". */
fun formatarDuracao(ms: Long): String {
    val total = ms / 1000
    val h = total / 3600
    val m = (total % 3600) / 60
    val s = total % 60
    return if (h > 0) {
        String.format(Locale.ROOT, "%d:%02d:%02d", h, m, s)
    } else {
        String.format(Locale.ROOT, "%02d:%02d", m, s)
    }
}

/** Metros em quilômetros com duas casas, no separador decimal do aparelho. */
fun formatarDistanciaKm(metros: Double): String =
    String.format(Locale.getDefault(), "%.2f", metros / 1000.0)

/** 6,5 min/km vira "6:30". */
fun formatarRitmo(minPorKm: Double): String {
    val totalSegundos = Math.round(minPorKm * 60).toInt()
    return String.format(Locale.ROOT, "%d:%02d", totalSegundos / 60, totalSegundos % 60)
}
