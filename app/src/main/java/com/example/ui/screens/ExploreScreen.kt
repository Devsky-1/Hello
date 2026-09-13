package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Wallpaper
import com.example.model.WallpaperCategory
import com.example.model.WallpaperResolution
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.ResolutionChipRow
import com.example.ui.components.WallpaperGrid
import com.example.ui.theme.WalloraNeonCyan
import com.example.ui.viewmodel.WallpaperViewModel

enum class WallpaperSortOrder(val label: String) {
    POPULAR("Most Popular"),
    DOWNLOADS("Most Downloaded"),
    LATEST("Newest Added")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: WallpaperViewModel,
    onOpenWallpaper: (Wallpaper) -> Unit,
    onOpenSearch: () -> Unit
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedResolution by viewModel.selectedResolution.collectAsState()
    val rawWallpapers by viewModel.filteredWallpapers.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    var sortOrder by remember { mutableStateOf(WallpaperSortOrder.POPULAR) }
    var showSortMenu by remember { mutableStateOf(false) }

    val sortedWallpapers = remember(rawWallpapers, sortOrder) {
        when (sortOrder) {
            WallpaperSortOrder.POPULAR -> rawWallpapers.sortedByDescending { it.likesCount }
            WallpaperSortOrder.DOWNLOADS -> rawWallpapers.sortedByDescending { it.downloadsCount }
            WallpaperSortOrder.LATEST -> rawWallpapers.sortedByDescending { it.viewsCount }
        }
    }

    val isFavCheck = { id: String -> favorites.any { it.wallpaperId == id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallpaper Library", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onOpenSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            WallpaperSortOrder.values().forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.label) },
                                    onClick = {
                                        sortOrder = order
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
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
            // Category Tabs
            val categories = WallpaperCategory.values()
            val selectedIndex = categories.indexOf(selectedCategory).coerceAtLeast(0)

            ScrollableTabRow(
                selectedTabIndex = selectedIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                        color = WalloraNeonCyan,
                        height = 2.5.dp
                    )
                },
                edgePadding = 16.dp
            ) {
                categories.forEachIndexed { index, cat ->
                    val isSelected = index == selectedIndex
                    Tab(
                        selected = isSelected,
                        onClick = { viewModel.selectCategory(cat) },
                        text = {
                            Text(
                                text = cat.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) WalloraNeonCyan else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.testTag("explore_tab_${cat.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quality / Resolution Filter Chips
            ResolutionChipRow(
                selectedResolution = selectedResolution,
                onResolutionSelected = { res -> viewModel.selectResolution(res) }
            )

            // Result count & active sort indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${sortedWallpapers.size} Wallpapers found",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sorted by: ${sortOrder.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = WalloraNeonCyan
                )
            }

            // Grid of Wallpapers
            WallpaperGrid(
                wallpapers = sortedWallpapers,
                isFavoriteCheck = isFavCheck,
                onFavoriteClick = { wp -> viewModel.toggleFavorite(wp) },
                onWallpaperClick = onOpenWallpaper,
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp)
            )
        }
    }
}
