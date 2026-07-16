package com.example.dogoverlay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.dogoverlay.audio.BarkPlayer
import com.example.dogoverlay.ui.DogOverlayScreen

class MainActivity : ComponentActivity() {

    private lateinit var barkPlayer: BarkPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        barkPlayer = BarkPlayer(applicationContext)

        setContent {
            MaterialTheme {
                DogOverlayScreen(
                    onBarkRequested = { onFinished -> barkPlayer.play(onFinished) }
                )
            }
        }
    }

    override fun onDestroy() {
        barkPlayer.release()
        super.onDestroy()
    }
}
