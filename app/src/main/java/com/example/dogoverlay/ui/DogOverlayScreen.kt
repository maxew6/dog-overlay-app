package com.example.dogoverlay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dogoverlay.DogState
import com.example.dogoverlay.R

/**
 * Full-screen scene: gradient sky + ground background, the animated dog
 * near the bottom, and the Walk / Sit / Bark controls.
 *
 * [onBarkRequested] fires when the user taps Bark. The caller (MainActivity)
 * plays bark.mp3 and must invoke the supplied `onFinished` callback once
 * playback ends, so this composable can revert to SIT and re-enable Bark.
 */
@Composable
fun DogOverlayScreen(onBarkRequested: (onFinished: () -> Unit) -> Unit) {
    var state by remember { mutableStateOf(DogState.SIT) }
    var barkEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFB3E5FC), Color(0xFFE1F5FE))
                )
            )
    ) {
        // Simple ground strip so the dog isn't floating in empty sky.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.BottomCenter)
                .background(Color(0xFFC8E6C9))
        )

        DogCanvas(
            state = state,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 130.dp)
                .size(width = 240.dp, height = 180.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ActionButton(text = stringResource(R.string.action_walk)) {
                state = DogState.WALK
            }
            ActionButton(text = stringResource(R.string.action_sit)) {
                state = DogState.SIT
            }
            ActionButton(text = stringResource(R.string.action_bark), enabled = barkEnabled) {
                barkEnabled = false
                state = DogState.BARK
                onBarkRequested {
                    state = DogState.SIT
                    barkEnabled = true
                }
            }
        }
    }
}

@Composable
private fun ActionButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6D4C41),
            contentColor = Color.White
        )
    ) {
        Text(text)
    }
}
