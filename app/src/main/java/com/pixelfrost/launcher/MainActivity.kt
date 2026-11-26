package com.pixelfrost.launcher

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.pixelfrost.launcher.apps.AppInfo
import com.pixelfrost.launcher.apps.loadLaunchableApps
import com.pixelfrost.launcher.designsystem.FrostContainer
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LauncherScreen()
            }
        }
    }
}

@Composable
private fun LauncherScreen() {
    val context = LocalContext.current
    val hazeState = rememberHazeState()
    val apps by produceState(initialValue = emptyList<AppInfo>()) {
        value = withContext(Dispatchers.IO) { loadLaunchableApps(context) }
    }
    var showDrawer by remember { mutableStateOf(false) }

    BackHandler(enabled = showDrawer) {
        showDrawer = false
    }

    val launch: (String) -> Unit = { packageName ->
        context.packageManager.getLaunchIntentForPackage(packageName)?.let {
            context.startActivity(it)
        }
        showDrawer = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30f) showDrawer = true
                }
            },
    ) {
        // Wallpaper sementara, sumber blur untuk kaca
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(hazeState)
                .background(Color(0xFF1B2238)),
        ) {
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 80.dp)
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

        HomeGrid(apps = apps.take(16), onLaunch = launch)

        if (showDrawer) {
            FrostContainer(
                hazeState = hazeState,
                cornerRadius = 36.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp, top = 48.dp, bottom = 12.dp),
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(apps, key = { it.packageName }) { app ->
                        AppItem(app = app, onClick = { launch(app.packageName) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeGrid(apps: List<AppInfo>, onLaunch: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 72.dp, start = 16.dp, end = 16.dp),
    ) {
        apps.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { app ->
                    AppItem(
                        app = app,
                        modifier = Modifier.weight(1f),
                        onClick = { onLaunch(app.packageName) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    app: AppInfo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val icon = remember(app.packageName) {
        context.packageManager.getApplicationIcon(app.packageName)
            .toBitmap(width = 144, height = 144)
            .asImageBitmap()
    }
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            bitmap = icon,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(16.dp)),
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.label,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
