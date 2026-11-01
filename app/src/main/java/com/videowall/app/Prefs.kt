package com.videowall.app

import android.content.Context

object Prefs {
    private const val FILE = "videowall"
    private const val KEY_URI = "video_uri"
    private const val KEY_MUTED = "muted"

    fun getVideoUri(context: Context): String? =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_URI, null)

    fun setVideoUri(context: Context, uri: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_URI, uri).apply()
    }

    // Son activé par défaut (muted = false)
    fun isMuted(context: Context): Boolean =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean(KEY_MUTED, false)

    fun setMuted(context: Context, muted: Boolean) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_MUTED, muted).apply()
    }
}
