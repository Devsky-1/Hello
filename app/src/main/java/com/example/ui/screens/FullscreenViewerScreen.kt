package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.Wallpaper
import com.example.ui.components.SetWallpaperBottomSheet
import com.example.ui.theme.WalloraGold
import com.example.ui.theme.WalloraNeonCyan
import com.example.ui.theme.WalloraVividMagenta
import com.example.ui.viewmodel.WallpaperViewModel
import com.example.util.WallpaperTarget

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FullscreenViewerScreen(
    initialWallpaper: Wallpaper,
    viewModel: WallpaperViewModel,
    onBack: () -> Unit,
    onOpenInteractivePreview: (Wallpaper) -> Unit,
    onOpenEditor: (Wallpaper) -> Unit
) {
    val context = LocalContext.current
    val allWallpapers by viewModel.allWallpapers.collectAsState()

    // Find initial index
    val initialIndex = remember(initialWallpaper) {
        val idx = allWallpapers.indexOfFirst { it.id == initialWallpaper.id }
        if (idx >= 0) idx else 0
    }

    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { allWallpapers.size }
    )

    val currentWallpaper = allWallpapers.getOrNull(pagerState.currentPage) ?: initialWallpaper

    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = remember(favorites, currentWallpaper.id) {
        favorites.any { it.wallpaperId == currentWallpaper.id }
    }

    // UI visibility toggle (immersive mode)
    var isUiVisible by remember { mutableStateOf(true) }

    // Quick Live Adjustments
    var liveBlurDp by remember { mutableFloatStateOf(0f) }
    var liveBrightness by remember { mutableFloatStateOf(0f) }
    var isContentScaleCrop by remember { mutableStateOf(true) }

    var showSetSheet by remember { mutableStateOf(false) }
    var showAdjustmentsSheet by remember { mutableStateOf(false) }
    var showInfoSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isUiVisible = !isUiVisible
            }
    ) {
        // Horizontal Pager for swiping between wallpapers
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val wp = allWallpapers[page]

            // Image with live blur & brightness color filter
            val brightnessMatrix = remember(liveBrightness) {
                val translate = liveBrightness * 255f
                ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, translate,
                        0f, 1f, 0f, 0f, translate,
                        0f, 0f, 1f, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(if (liveBlurDp > 0f) Modifier.blur(liveBlurDp.dp) else Modifier)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(wp.fullUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = wp.title,
                    contentScale = if (isContentScaleCrop) ContentScale.Crop else ContentScale.Fit,
                    colorFilter = ColorFilter.colorMatrix(brightnessMatrix),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Top Gradient Scrim & Bar
        AnimatedVisibility(
            visible = isUiVisible,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
                    .padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .testTag("viewer_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentWallpaper.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${currentWallpaper.resolution.label} • ${currentWallpaper.category.uppercase()}",
                            color = WalloraNeonCyan,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showInfoSheet = true },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Info, contentDescription = "Info", tint = Color.White)
                        }
                    }
                }
            }
        }

        // Bottom Action Controls
        AnimatedVisibility(
            visible = isUiVisible,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f), Color.Black)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Secondary Actions Row: Preview Mockup, Quick Sliders, Edit, Share
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Phone Preview
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onOpenInteractivePreview(currentWallpaper) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = "Preview", tint = WalloraNeonCyan, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Preview", color = Color.White, fontSize = 11.sp)
                        }

                        // Quick Adjustments
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { showAdjustmentsSheet = true }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = "Adjust", tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Adjust", color = Color.White, fontSize = 11.sp)
                        }

                        // Advanced Photo Editor
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onOpenEditor(currentWallpaper) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Studio", tint = WalloraGold, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Edit", color = Color.White, fontSize = 11.sp)
                        }

                        // Share
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Check out '${currentWallpaper.title}' on Wallora Ultimate: ${currentWallpaper.fullUrl}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Wallpaper"))
                            }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Share", color = Color.White, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Row: Favorite, Download, Set Wallpaper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Favorite Toggle
                        IconButton(
                            onClick = { viewModel.toggleFavorite(currentWallpaper) },
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) WalloraVividMagenta else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Download Button
                        IconButton(
                            onClick = { viewModel.downloadWallpaper(context, currentWallpaper) },
                            modifier = Modifier
                                .size(52.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Set Wallpaper Main Button
                        Button(
                            onClick = { showSetSheet = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("set_wallpaper_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = WalloraNeonCyan),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Wallpaper, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Apply Wallpaper",
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Set Wallpaper Dialog
    if (showSetSheet) {
        SetWallpaperBottomSheet(
            onDismiss = { showSetSheet = false },
            onSelectTarget = { target ->
                showSetSheet = false
                viewModel.applyWallpaper(context, currentWallpaper, target) {}
            }
        )
    }

    // Quick Adjustments Sheet (Live Blur & Brightness)
    if (showAdjustmentsSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showAdjustmentsSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text("Quick Preview Adjustments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Blur slider
                Text("Live Blur: ${liveBlurDp.toInt()} dp", fontSize = 13.sp)
                Slider(
                    value = liveBlurDp,
                    onValueChange = { liveBlurDp = it },
                    valueRange = 0f..25f
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Brightness slider
                Text("Brightness: ${(liveBrightness * 100).toInt()}%", fontSize = 13.sp)
                Slider(
                    value = liveBrightness,
                    onValueChange = { liveBrightness = it },
                    valueRange = -0.5f..0.5f
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fit / Fill Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Aspect Fit / Fill", fontSize = 14.sp)
                    Button(
                        onClick = { isContentScaleCrop = !isContentScaleCrop },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(if (isContentScaleCrop) "Fill Screen" else "Fit Aspect", color = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Wallpaper Info Sheet
    if (showInfoSheet) {
        val sheetState = rememberModalBottomSheetState()
        ModalBottomSheet(
            onDismissRequest = { showInfoSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(currentWallpaper.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("By ${currentWallpaper.author}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Text(currentWallpaper.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Resolution", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(currentWallpaper.resolution.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Column {
                        Text("Category", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(currentWallpaper.category.uppercase(), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Column {
                        Text("Downloads", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${currentWallpaper.downloadsCount}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Color Palette", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    currentWallpaper.dominantColors.forEach { colorLong ->
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(colorLong), CircleShape)
                                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Tags", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    currentWallpaper.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text("#$tag", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
