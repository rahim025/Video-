package com.videowall.app

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val pickCode = 1001
    private lateinit var status: TextView
    private lateinit var soundBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pad = (24 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(pad, pad * 2, pad, pad)
        }

        val title = TextView(this).apply {
            text = "VideoWall"
            textSize = 28f
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            textSize = 16f
            gravity = Gravity.CENTER
            setPadding(0, pad, 0, pad)
        }
        refreshStatus()

        val pick = Button(this).apply {
            text = "1. Choisir une vidéo (galerie)"
            setOnClickListener { pickVideo() }
        }

        soundBtn = Button(this).apply {
            setOnClickListener {
                Prefs.setMuted(this@MainActivity, !Prefs.isMuted(this@MainActivity))
                refreshSound()
            }
        }
        refreshSound()

        val set = Button(this).apply {
            text = "3. Définir comme fond d'écran"
            setOnClickListener { setWallpaper() }
        }

        val hint = TextView(this).apply {
            text = "À l'étape 3, choisis « Écran de verrouillage » ou « Accueil et verrouillage » si Android te le propose. Le changement de son s'applique quand l'écran se rallume."
            textSize = 14f
            gravity = Gravity.CENTER
            setPadding(0, pad, 0, 0)
        }

        root.addView(title)
        root.addView(status)
        root.addView(pick)
        root.addView(soundBtn)
        root.addView(set)
        root.addView(hint)
        setContentView(root)
    }

    private fun refreshStatus() {
        status.text = if (Prefs.getVideoUri(this) == null)
            "Aucune vidéo choisie"
        else
            "Vidéo choisie ✓"
    }

    private fun refreshSound() {
        soundBtn.text = if (Prefs.isMuted(this))
            "2. Son : coupé (appuie pour activer)"
        else
            "2. Son : activé (appuie pour couper)"
    }

    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, pickCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickCode && resultCode == RESULT_OK) {
            val uri: Uri = data?.data ?: return
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            Prefs.setVideoUri(this, uri.toString())
            refreshStatus()
        }
    }

    private fun setWallpaper() {
        if (Prefs.getVideoUri(this) == null) {
            Toast.makeText(this, "Choisis d'abord une vidéo", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@MainActivity, VideoWallpaperService::class.java)
                )
            }
            startActivity(intent)
        } catch (_: Exception) {
            try {
                startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
            } catch (_: Exception) {
                Toast.makeText(this, "Fonds d'écran animés non pris en charge", Toast.LENGTH_LONG).show()
            }
        }
    }
}
