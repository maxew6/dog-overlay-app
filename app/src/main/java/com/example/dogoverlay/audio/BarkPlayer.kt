package com.example.dogoverlay.audio

import android.content.Context
import android.media.MediaPlayer
import com.example.dogoverlay.R

/**
 * Minimal wrapper around [MediaPlayer] for playing the one-shot bark.mp3
 * (res/raw/bark.mp3). No SoundPool/session handling is needed since only
 * one short clip ever plays at a time.
 */
class BarkPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    /** Plays bark.mp3 once and invokes [onFinished] on the main thread when it ends. */
    fun play(onFinished: () -> Unit) {
        release()
        val player = MediaPlayer.create(context, R.raw.bark) ?: run {
            // Asset missing/corrupt: fail gracefully and still return to Sit.
            onFinished()
            return
        }
        mediaPlayer = player
        player.setOnCompletionListener {
            release()
            onFinished()
        }
        player.start()
    }

    fun release() {
        mediaPlayer?.let {
            it.setOnCompletionListener(null)
            it.release()
        }
        mediaPlayer = null
    }
}
