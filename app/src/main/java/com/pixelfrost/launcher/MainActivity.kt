package com.pixelfrost.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toBitmap
import com.pixelfrost.launcher.apps.AppInfo
import com.pixelfrost.launcher.apps.loadLaunchableApps
import com.pixelfrost.launcher.designsystem.FrostContainer
import com.pixelfrost.launcher.home.HomeOrderStore
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

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
    var query by remember { mutableStateOf("") }
    var order by remember { mutableStateOf(HomeOrderStore.load(context)) }

    val homeApps = remember(apps, order) {
        val byPackage = apps.associateBy { it.packageName }
        order.mapNotNull { byPackage[it] } +
            apps.filter { app -> order.none { it == app.packageName } }
    }

    val onMove: (Int, Int) -> Unit = { from, to ->
        val list = homeApps.map { it.packageName }.toMutableList()
        list.add(to, list.removeAt(from))
        order = list
        HomeOrderStore.save(context, list)
    }

    val filteredApps = apps.filter { it.label.contains(query, ignoreCase = true) }

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

        HomeGrid(
            apps = homeApps.take(16),
            onLaunch = launch,
            onMove = onMove,
        )

        if (showDrawer) {
            FrostContainer(
                hazeState = hazeState,
                cornerRadius = 36.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 12.dp, top = 48.dp, bottom = 12.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Text("Cari aplikasi", color = Color.White.copy(alpha = 0.6f))
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White,
                            focusedContainerColor = Color.White.copy(alpha = 0.12f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.12f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        contentPadding = PaddingValues(top = 4.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            AppItem(app = app, onClick = { launch(app.packageName) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeGrid(
    apps: List<AppInfo>,
    onLaunch: (String) -> Unit,
    onMove: (Int, Int) -> Unit,
) {
    var cellSize by remember { mutableStateOf(IntSize.Zero) }
    var dragIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 72.dp, start = 16.dp, end = 16.dp),
    ) {
        apps.chunked(4).forEachIndexed { rowIndex, row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { colIndex, app ->
                    val index = rowIndex * 4 + colIndex
                    val isDragging = dragIndex == index
                    AppItem(
                        app = app,
                        modifier = Modifier
                            .weight(1f)
                            .onSizeChanged { cellSize = it }
                            .zIndex(if (isDragging) 1f else 0f)
                            .offset {
                                if (isDragging) {
                                    IntOffset(dragOffset.x.roundToInt(), dragOffset.y.roundToInt())
                                } else {
                                    IntOffset.Zero
                                }
                            }
                            .pointerInput(index, apps.size) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        dragIndex = index
                                        dragOffset = Offset.Zero
                                    },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        dragOffset += amount
                                    },
                                    onDragEnd = {
                                        val cw = cellSize.width.coerceAtLeast(1)
                                        val ch = cellSize.height.coerceAtLeast(1)
                                        val dc = (dragOffset.x / cw).roundToInt()
                                        val dr = (dragOffset.y / ch).roundToInt()
                                        val target = (index + dr * 4 + dc)
                                            .coerceIn(0, apps.lastIndex)
                                        if (target != index) onMove(index, target)
                                        dragIndex = null
                                        dragOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        dragIndex = null
                                        dragOffset = Offset.Zero
                                    },
                                )
                            },
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
