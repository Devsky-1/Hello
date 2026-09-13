package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ai.AiGenerationConfig
import com.example.ai.AiWallpaperEngine
import com.example.model.Wallpaper
import com.example.model.WallpaperResolution
import com.example.ui.components.SetWallpaperBottomSheet
import com.example.ui.theme.WalloraGold
import com.example.ui.theme.WalloraNeonCyan
import com.example.ui.theme.WalloraVividMagenta
import com.example.ui.viewmodel.WallpaperViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateStudioScreen(
    viewModel: WallpaperViewModel,
    onOpenEditorForPhoto: (String, String) -> Unit,
    onOpenWallpaper: (Wallpaper) -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: AI Generator, 1: Wallpaper Maker

    // AI Generator state
    var promptText by remember {
        mutableStateOf("Futuristic black supercar in a neon city at night, cinematic lighting, realistic, vertical phone wallpaper.")
    }
    var selectedStyle by remember { mutableStateOf(AiWallpaperEngine.styles[0]) }
    var selectedAspectRatio by remember { mutableStateOf(AiWallpaperEngine.aspectRatios[0]) }
    var selectedQuality by remember { mutableStateOf(AiWallpaperEngine.qualities[2]) } // 4K Cinematic

    val isGenerating by viewModel.isGeneratingAi.collectAsState()
    val lastAiResult by viewModel.lastAiResult.collectAsState()
    val aiHistory by viewModel.aiHistory.collectAsState()

    var showSetSheetForAi by remember { mutableStateOf(false) }

    // Photo picker launcher (zero permission, compliant with Play Policy)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onOpenEditorForPhoto(uri.toString(), "My Custom Photo")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Creation Studio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
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
            // Tabs: AI Generator vs Photo Wallpaper Maker
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = WalloraNeonCyan,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (selectedTab == 0) WalloraNeonCyan else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Generator", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (selectedTab == 1) WalloraVividMagenta else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wallpaper Maker", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (selectedTab == 0) {
                // TAB 1: AI GENERATOR
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Prompt Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = promptText,
                            onValueChange = { promptText = it },
                            placeholder = { Text("Describe your dream wallpaper...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_prompt_input"),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WalloraNeonCyan,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        // Inspiration chips
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = WalloraGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inspirations:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(AiWallpaperEngine.samplePrompts) { prompt ->
                                AssistChip(
                                    onClick = { promptText = prompt },
                                    label = { Text(prompt.take(28) + "...", fontSize = 11.sp) },
                                    shape = RoundedCornerShape(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Art Style selector
                        Text("Artistic Style", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AiWallpaperEngine.styles.forEach { style ->
                                val isSelected = selectedStyle == style
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedStyle = style },
                                    label = { Text(style, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = WalloraNeonCyan,
                                        selectedLabelColor = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Aspect Ratio & Quality
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Aspect Ratio", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    AiWallpaperEngine.aspectRatios.forEach { ratio ->
                                        FilterChip(
                                            selected = selectedAspectRatio == ratio,
                                            onClick = { selectedAspectRatio = ratio },
                                            label = { Text(ratio, fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quality Preset", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    AiWallpaperEngine.qualities.forEach { q ->
                                        FilterChip(
                                            selected = selectedQuality == q,
                                            onClick = { selectedQuality = q },
                                            label = { Text(q, fontSize = 11.sp) },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Generate Button
                        Button(
                            onClick = {
                                if (promptText.isNotBlank()) {
                                    val config = AiGenerationConfig(
                                        prompt = promptText,
                                        style = selectedStyle,
                                        aspectRatio = selectedAspectRatio,
                                        quality = selectedQuality
                                    )
                                    viewModel.generateAiWallpaper(context, config)
                                }
                            },
                            enabled = !isGenerating && promptText.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("generate_ai_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = WalloraNeonCyan),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Synthesizing 4K AI Art...", color = Color.Black, fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Wallpaper", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }

                    // Result Preview Card (if generated)
                    lastAiResult?.let { res ->
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Generated Masterpiece", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(0.65f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(2.dp, WalloraNeonCyan, RoundedCornerShape(20.dp)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {
                                    androidx.compose.foundation.Image(
                                        bitmap = res.bitmap.asImageBitmap(),
                                        contentDescription = "AI Generated Wallpaper",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Action bar overlay
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.BottomCenter),
                                        color = Color.Black.copy(alpha = 0.8f)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = {
                                                    val wp = Wallpaper(
                                                        id = res.id,
                                                        title = "AI: ${res.prompt.take(24)}",
                                                        author = "Wallora AI",
                                                        category = "AI",
                                                        resolution = WallpaperResolution.UHD_4K,
                                                        thumbnailUrl = res.localUri,
                                                        fullUrl = res.localUri
                                                    )
                                                    showSetSheetForAi = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = WalloraNeonCyan)
                                            ) {
                                                Icon(Icons.Default.Wallpaper, contentDescription = null, tint = Color.Black)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Apply", color = Color.Black, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = {
                                                    onOpenEditorForPhoto(res.localUri, "AI Masterpiece")
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Edit")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Generation History Section
                    if (aiHistory.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, contentDescription = null, tint = WalloraGold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generation History (${aiHistory.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(aiHistory) { hist ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        val wp = Wallpaper(
                                            id = "ai_${hist.id}",
                                            title = "AI: ${hist.prompt.take(24)}",
                                            author = "Wallora AI (${hist.style})",
                                            category = "AI",
                                            resolution = WallpaperResolution.UHD_4K,
                                            thumbnailUrl = hist.localUri,
                                            fullUrl = hist.localUri
                                        )
                                        onOpenWallpaper(wp)
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(hist.localUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(hist.prompt, maxLines = 1, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                        Text("${hist.style} • ${hist.quality}", fontSize = 11.sp, color = WalloraNeonCyan)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // TAB 2: WALLPAPER MAKER
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                                .testTag("pick_photo_button"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, WalloraNeonCyan)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(WalloraNeonCyan.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = WalloraNeonCyan,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Select Photo from Device",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Transform personal pictures into 4K customized wallpapers using filters, gradients, clock stamps, and crop presets.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Or Start from Curated Templates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Preset templates to open in editor
                    val templates = listOf(
                        Triple("Cyberpunk Neon Highway", "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=1920&q=80", "Cyber"),
                        Triple("Deep Ocean Luminescence", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=1920&q=80", "Nature"),
                        Triple("Minimal Sand Horizon", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=1920&q=80", "Minimal"),
                        Triple("Alpine Mist Summit", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1920&q=80", "Mountains")
                    )

                    items(templates) { (title, url, tag) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onOpenEditorForPhoto(url, title) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(url)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Template • $tag", fontSize = 11.sp, color = WalloraNeonCyan)
                                }
                                Button(
                                    onClick = { onOpenEditorForPhoto(url, title) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Text("Edit", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSetSheetForAi && lastAiResult != null) {
        SetWallpaperBottomSheet(
            onDismiss = { showSetSheetForAi = false },
            onSelectTarget = { target ->
                showSetSheetForAi = false
                val wp = Wallpaper(
                    id = lastAiResult!!.id,
                    title = "AI: ${lastAiResult!!.prompt.take(24)}",
                    author = "Wallora AI",
                    category = "AI",
                    resolution = WallpaperResolution.UHD_4K,
                    thumbnailUrl = lastAiResult!!.localUri,
                    fullUrl = lastAiResult!!.localUri
                )
                viewModel.applyWallpaper(context, wp, target) {}
            }
        )
    }
}
