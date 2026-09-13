package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Gradient
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatioOption
import com.example.model.FilterMode
import com.example.model.GradientOverlayOption
import com.example.model.Wallpaper
import com.example.ui.theme.WalloraGold
import com.example.ui.theme.WalloraNeonCyan
import com.example.ui.viewmodel.WallpaperViewModel
import com.example.util.BitmapHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WallpaperEditorScreen(
    wallpaperSource: String, // URL or Content URI
    wallpaperTitle: String,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit,
    onSaved: (Wallpaper) -> Unit
) {
    val context = LocalContext.current
    var baseBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    val editorState by viewModel.editorState.collectAsState()
    var selectedToolTab by remember { mutableIntStateOf(0) } // 0: Adjust, 1: Filter, 2: Crop/Rotate, 3: Gradient, 4: Text & Stamps

    // Load initial source bitmap
    LaunchedEffect(wallpaperSource) {
        isProcessing = true
        baseBitmap = BitmapHelper.loadBitmapSafe(context, wallpaperSource, maxDimension = 1440)
        isProcessing = false
    }

    // Recalculate preview bitmap on editor state changes
    LaunchedEffect(baseBitmap, editorState) {
        val base = baseBitmap ?: return@LaunchedEffect
        previewBitmap = BitmapHelper.applyEditorTransformations(base, editorState)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Studio Editor", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetEditorState() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    }
                    IconButton(
                        onClick = {
                            val base = baseBitmap ?: return@IconButton
                            isProcessing = true
                            viewModel.saveEditedWallpaper(
                                context = context,
                                baseBitmap = base,
                                title = "Edited_${wallpaperTitle.take(12)}"
                            ) { createdWp ->
                                isProcessing = false
                                onSaved(createdWp)
                            }
                        },
                        modifier = Modifier.testTag("save_editor_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save", tint = WalloraNeonCyan)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Live Preview Canvas Viewport
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = WalloraNeonCyan)
                } else if (previewBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = previewBitmap!!.asImageBitmap(),
                        contentDescription = "Editor Preview",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                    )
                } else {
                    Text("Loading Canvas...", color = Color.White)
                }
            }

            // Tool Tabs (Adjust, Filter, Crop, Gradient, Text)
            ScrollableTabRow(
                selectedTabIndex = selectedToolTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                edgePadding = 12.dp
            ) {
                Tab(
                    selected = selectedToolTab == 0,
                    onClick = { selectedToolTab = 0 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Adjust")
                    }}
                )
                Tab(
                    selected = selectedToolTab == 1,
                    onClick = { selectedToolTab = 1 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ColorLens, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Filters")
                    }}
                )
                Tab(
                    selected = selectedToolTab == 2,
                    onClick = { selectedToolTab = 2 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Crop/Rotate")
                    }}
                )
                Tab(
                    selected = selectedToolTab == 3,
                    onClick = { selectedToolTab = 3 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gradient, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gradients")
                    }}
                )
                Tab(
                    selected = selectedToolTab == 4,
                    onClick = { selectedToolTab = 4 },
                    text = { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Text & Stamp")
                    }}
                )
            }

            // Controls Drawer for Selected Tab
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedToolTab) {
                        0 -> {
                            // ADJUST TAB
                            Text("Brightness: ${(editorState.brightness * 100).toInt()}%", fontSize = 12.sp)
                            Slider(
                                value = editorState.brightness,
                                onValueChange = { viewModel.updateEditorState(editorState.copy(brightness = it)) },
                                valueRange = -0.6f..0.6f
                            )

                            Text("Contrast: ${(editorState.contrast * 100).toInt()}%", fontSize = 12.sp)
                            Slider(
                                value = editorState.contrast,
                                onValueChange = { viewModel.updateEditorState(editorState.copy(contrast = it)) },
                                valueRange = 0.5f..1.8f
                            )

                            Text("Saturation: ${(editorState.saturation * 100).toInt()}%", fontSize = 12.sp)
                            Slider(
                                value = editorState.saturation,
                                onValueChange = { viewModel.updateEditorState(editorState.copy(saturation = it)) },
                                valueRange = 0.0f..2.0f
                            )

                            Text("Soft Blur: ${editorState.blurRadius.toInt()} px", fontSize = 12.sp)
                            Slider(
                                value = editorState.blurRadius,
                                onValueChange = { viewModel.updateEditorState(editorState.copy(blurRadius = it)) },
                                valueRange = 0f..20f
                            )
                        }
                        1 -> {
                            // FILTERS TAB
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilterMode.values().forEach { mode ->
                                    val isSelected = editorState.filterMode == mode
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.updateEditorState(editorState.copy(filterMode = mode)) },
                                        label = { Text(mode.label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = WalloraNeonCyan,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }
                        }
                        2 -> {
                            // CROP & ROTATE
                            Text("Aspect Ratio", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AspectRatioOption.values().forEach { option ->
                                    val isSelected = editorState.cropRatio == option
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.updateEditorState(editorState.copy(cropRatio = option)) },
                                        label = { Text(option.label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = WalloraGold,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text("Rotation", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        val nextAngle = (editorState.rotationAngle + 90f) % 360f
                                        viewModel.updateEditorState(editorState.copy(rotationAngle = nextAngle))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Icon(Icons.Default.RotateRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Rotate 90° (Current: ${editorState.rotationAngle.toInt()}°)", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                        3 -> {
                            // GRADIENTS OVERLAY
                            Text("Gradient Overlays", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                GradientOverlayOption.values().forEach { grad ->
                                    val isSelected = editorState.gradientOverlay == grad
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.updateEditorState(editorState.copy(gradientOverlay = grad)) },
                                        label = { Text(grad.label) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = WalloraNeonCyan,
                                            selectedLabelColor = Color.Black
                                        )
                                    )
                                }
                            }

                            if (editorState.gradientOverlay != GradientOverlayOption.NONE) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Gradient Opacity: ${(editorState.gradientOpacity * 100).toInt()}%", fontSize = 12.sp)
                                Slider(
                                    value = editorState.gradientOpacity,
                                    onValueChange = { viewModel.updateEditorState(editorState.copy(gradientOpacity = it)) },
                                    valueRange = 0.1f..0.9f
                                )
                            }
                        }
                        4 -> {
                            // TEXT & STAMP
                            OutlinedTextField(
                                value = editorState.overlayText,
                                onValueChange = { viewModel.updateEditorState(editorState.copy(overlayText = it)) },
                                label = { Text("Overlay Text") },
                                placeholder = { Text("E.g. NEVER GIVE UP") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (editorState.overlayText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Text Position Y", fontSize = 12.sp)
                                Slider(
                                    value = editorState.overlayTextPosY,
                                    onValueChange = { viewModel.updateEditorState(editorState.copy(overlayTextPosY = it)) },
                                    valueRange = 0.1f..0.9f
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Watermark Stamp (Time)", fontSize = 13.sp)
                                Switch(
                                    checked = editorState.showClockStamp,
                                    onCheckedChange = { viewModel.updateEditorState(editorState.copy(showClockStamp = it)) }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Decorative Badges", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("", "4K ULTRA", "AMOLED", "CYBER 2077", "LIMITED").forEach { badge ->
                                    val isSelected = editorState.decorativeSticker == badge
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.updateEditorState(editorState.copy(decorativeSticker = badge)) },
                                        label = { Text(if (badge.isEmpty()) "None" else badge) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
