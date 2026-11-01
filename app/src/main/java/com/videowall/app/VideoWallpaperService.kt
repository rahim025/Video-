package com.videowall.app

import android.media.MediaPlayer
import android.net.Uri
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class VideoWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = VideoEngine()

    inner class VideoEngine : Engine() {
        private var player: MediaPlayer? = null
        private var currentUri: String? = null

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            startPlayer(holder)
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            if (visible) {
                val saved = Prefs.getVideoUri(applicationContext)
                if (player == null || saved != currentUri) {
                    startPlayer(surfaceHolder)
                } else {
                    try { player?.start() } catch (_: Exception) {}
                }
            } else {
                try { player?.pause() } catch (_: Exception) {}
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            releasePlayer()
        }

        override fun onDestroy() {
            releasePlayer()
            super.onDestroy()
        }

        private fun startPlayer(holder: SurfaceHolder) {
            releasePlayer()
            val uriString = Prefs.getVideoUri(applicationContext) ?: return
            currentUri = uriString
            try {
                val mp = MediaPlayer()
                mp.setSurface(holder.surface)
                mp.setDataSource(applicationContext, Uri.parse(uriString))
                mp.isLooping = true
                mp.setVolume(0f, 0f)
                try {
                    mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                } catch (_: Exception) {}
                mp.setOnPreparedListener { if (isVisible) it.start() }
                mp.setOnErrorListener { _, _, _ -> true }
                mp.prepareAsync()
                player = mp
            } catch (_: Exception) {
                releasePlayer()
            }
        }

        private fun releasePlayer() {
            try { player?.release() } catch (_: Exception) {}
            player = null
        }
    }
}
