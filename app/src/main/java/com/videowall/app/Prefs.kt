package com.videowall.app

import android.content.Context

object Prefs {
    private const val FILE = "videowall"
    private const val KEY_URI = "video_uri"

    fun getVideoUri(context: Context): String? =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_URI, null)

    fun setVideoUri(context: Context, uri: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY_URI, uri).apply()
    }
}
