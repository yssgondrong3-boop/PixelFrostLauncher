package com.pixelfrost.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelfrost.launcher.designsystem.FrostContainer
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FrostPreviewScreen()
            }
        }
    }
}

@Composable
private fun FrostPreviewScreen() {
    val hazeState = rememberHazeState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Latar sementara sebagai pengganti wallpaper. Lapisan ini yang akan diburamkan.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .background(Color(0xFF1B2238)),
        ) {
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 0.dp, top = 80.dp)
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF378ADD)),
            )
            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(220.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7F77DD)),
            )
            Box(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 120.dp)
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1D9E75)),
            )
        }

        // Kartu kaca di tengah layar
        FrostContainer(
            hazeState = hazeState,
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 300.dp, height = 140.dp),
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("PixelFrost Tahap 1: kaca buram aktif")
            }
        }
    }
}
