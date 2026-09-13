package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.ClockPosition
import com.example.model.Wallpaper
import com.example.ui.components.ClockDisplayView
import com.example.ui.components.IconPreviewRow
import com.example.ui.components.SetWallpaperBottomSheet
import com.example.ui.theme.WalloraNeonCyan
import com.example.ui.theme.WalloraVividMagenta
import com.example.ui.viewmodel.WallpaperViewModel
import com.example.util.WallpaperTarget

enum class PreviewMode {
    LOCK_SCREEN,
    HOME_SCREEN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartPreviewScreen(
    wallpaper: Wallpaper,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit,
    onOpenClockEditor: () -> Unit,
    onOpenIconEditor: () -> Unit
) {
    val context = LocalContext.current
    var previewMode by remember { mutableStateOf(PreviewMode.LOCK_SCREEN) }
    var showSetSheet by remember { mutableStateOf(false) }

    val clockConfig by viewModel.clockConfig.collectAsState()
    val iconConfig by viewModel.iconConfig.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interactive Preview", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenClockEditor) {
                        Icon(Icons.Default.Schedule, contentDescription = "Clock Customizer", tint = WalloraNeonCyan)
                    }
                    IconButton(onClick = onOpenIconEditor) {
                        Icon(Icons.Default.Palette, contentDescription = "Icon Customizer", tint = WalloraVividMagenta)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Mode Selector Tabs
                    TabRow(
                        selectedTabIndex = if (previewMode == PreviewMode.LOCK_SCREEN) 0 else 1,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.tabIndicatorOffset(tabPositions[if (previewMode == PreviewMode.LOCK_SCREEN) 0 else 1]),
                                color = WalloraNeonCyan,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = previewMode == PreviewMode.LOCK_SCREEN,
                            onClick = { previewMode = PreviewMode.LOCK_SCREEN },
                            text = { Text("Lock Screen", fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = previewMode == PreviewMode.HOME_SCREEN,
                            onClick = { previewMode = PreviewMode.HOME_SCREEN },
                            text = { Text("Home Screen", fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onOpenClockEditor,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Clock", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = { showSetSheet = true },
                            modifier = Modifier.weight(1.2f),
                            colors = ButtonDefaults.buttonColors(containerColor = WalloraNeonCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Wallpaper, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Set Wallpaper", fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            // Realistic Phone Mockup Frame
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(3.dp, Color(0xFF2A2E3D), RoundedCornerShape(32.dp))
                    .shadow(16.dp, RoundedCornerShape(32.dp))
            ) {
                // Wallpaper background image
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(wallpaper.fullUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Subtle vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.45f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.55f)
                                )
                            )
                        )
                )

                // Top System Status Bar & Camera punch hole
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("10:09", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                        // Camera cutout notch
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(Color.Black, CircleShape)
                                .border(1.dp, Color(0x33FFFFFF), CircleShape)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Main Interactive Preview Content
                if (previewMode == PreviewMode.LOCK_SCREEN) {
                    // LOCK SCREEN MODE
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Draggable Clock & Date
                        ClockDisplayView(
                            config = clockConfig,
                            modifier = Modifier.align(Alignment.TopCenter),
                            isInteractiveDrag = clockConfig.position == ClockPosition.CUSTOM,
                            onDragOffsetChange = { x, y ->
                                viewModel.updateClockOffset(x, y)
                            }
                        )

                        // Notification Card Mockup
                        Card(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.9f)
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Black.copy(alpha = 0.45f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(WalloraNeonCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Wallora Ultimate", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Wallpaper ready to customize and set", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                }
                            }
                        }

                        // Bottom Lock Screen Shortcuts (Flashlight and Camera)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 24.dp, vertical = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }

                            // Swipe up hint bar
                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .height(4.dp)
                                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                            )

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                        }
                    }
                } else {
                    // HOME SCREEN MODE
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 40.dp, bottom = 16.dp, start = 12.dp, end = 12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Clock & Weather Widget
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f)),
                            shape = RoundedCornerShape(24.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("10:09", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                    Text("Friday, Sep 12", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Cloud, contentDescription = null, tint = WalloraNeonCyan, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("24°C Sunny", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }

                        // Search Bar Widget
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .height(48.dp)
                                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(24.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(24.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Search web & apps...", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                                }
                                Icon(Icons.Default.Mic, contentDescription = null, tint = WalloraNeonCyan, modifier = Modifier.size(20.dp))
                            }
                        }

                        // App Icons Grid (Row 1)
                        IconPreviewRow(config = iconConfig, modifier = Modifier.padding(vertical = 8.dp), isDock = false)

                        // App Dock (Row 2 with dock styling)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(28.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(28.dp))
                                .padding(vertical = 10.dp)
                        ) {
                            IconPreviewRow(config = iconConfig, isDock = true)
                        }
                    }
                }
            }
        }
    }

    if (showSetSheet) {
        SetWallpaperBottomSheet(
            onDismiss = { showSetSheet = false },
            onSelectTarget = { target ->
                showSetSheet = false
                viewModel.applyWallpaper(context, wallpaper, target) {}
            }
        )
    }
}
