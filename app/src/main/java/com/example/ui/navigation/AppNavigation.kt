package com.example.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.model.Wallpaper
import com.example.model.WallpaperCategory
import com.example.ui.screens.ClockCustomizerSheet
import com.example.ui.screens.CreateStudioScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.FullscreenViewerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IconCustomizerSheet
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SmartPreviewScreen
import com.example.ui.screens.WallpaperEditorScreen
import com.example.ui.theme.WalloraNeonCyan
import com.example.ui.theme.WalloraVividMagenta
import com.example.ui.viewmodel.WallpaperViewModel

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Explore : Screen("explore", "Explore", Icons.Filled.Explore, Icons.Outlined.Explore)
    object Create : Screen("create", "Create", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)

    // Detailed routes
    object Viewer : Screen("viewer/{wallpaperId}", "Viewer", Icons.Filled.Home, Icons.Outlined.Home) {
        fun createRoute(wallpaperId: String) = "viewer/$wallpaperId"
    }

    object SmartPreview : Screen("smart_preview/{wallpaperId}", "Preview", Icons.Filled.Home, Icons.Outlined.Home) {
        fun createRoute(wallpaperId: String) = "smart_preview/$wallpaperId"
    }

    object Editor : Screen("editor?source={source}&title={title}", "Editor", Icons.Filled.Home, Icons.Outlined.Home) {
        fun createRoute(source: String, title: String): String {
            val encodedSource = Uri.encode(source)
            val encodedTitle = Uri.encode(title)
            return "editor?source=$encodedSource&title=$encodedTitle"
        }
    }

    object Search : Screen("search", "Search", Icons.Filled.Home, Icons.Outlined.Home)
}

val BOTTOM_NAV_ITEMS = listOf(
    Screen.Home,
    Screen.Explore,
    Screen.Create,
    Screen.Favorites,
    Screen.Settings
)

@Composable
fun WalloraAppNavigation(
    viewModel: WallpaperViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val allWallpapers by viewModel.allWallpapers.collectAsState()

    var showClockSheet by remember { mutableStateOf(false) }
    var showIconSheet by remember { mutableStateOf(false) }

    val showBottomBar = currentRoute in BOTTOM_NAV_ITEMS.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    BOTTOM_NAV_ITEMS.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = WalloraNeonCyan,
                                indicatorColor = WalloraNeonCyan,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.route}")
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(250)) },
            exitTransition = { fadeOut(animationSpec = tween(250)) }
        ) {
            // 1. HOME SCREEN
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onOpenWallpaper = { wp ->
                        viewModel.trackRecentlyViewed(wp)
                        navController.navigate(Screen.Viewer.createRoute(wp.id))
                    },
                    onOpenSearch = { navController.navigate(Screen.Search.route) },
                    onOpenCreateTab = { navController.navigate(Screen.Create.route) },
                    onExploreCategory = { cat ->
                        viewModel.selectCategory(cat)
                        navController.navigate(Screen.Explore.route)
                    }
                )
            }

            // 2. EXPLORE SCREEN
            composable(Screen.Explore.route) {
                ExploreScreen(
                    viewModel = viewModel,
                    onOpenWallpaper = { wp ->
                        viewModel.trackRecentlyViewed(wp)
                        navController.navigate(Screen.Viewer.createRoute(wp.id))
                    },
                    onOpenSearch = { navController.navigate(Screen.Search.route) }
                )
            }

            // 3. CREATE STUDIO SCREEN
            composable(Screen.Create.route) {
                CreateStudioScreen(
                    viewModel = viewModel,
                    onOpenEditorForPhoto = { source, title ->
                        navController.navigate(Screen.Editor.createRoute(source, title))
                    },
                    onOpenWallpaper = { wp ->
                        viewModel.trackRecentlyViewed(wp)
                        navController.navigate(Screen.Viewer.createRoute(wp.id))
                    }
                )
            }

            // 4. FAVORITES SCREEN
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onOpenWallpaper = { wp ->
                        viewModel.trackRecentlyViewed(wp)
                        navController.navigate(Screen.Viewer.createRoute(wp.id))
                    }
                )
            }

            // 5. SETTINGS SCREEN
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }

            // 6. FULLSCREEN VIEWER
            composable(
                route = Screen.Viewer.route,
                arguments = listOf(navArgument("wallpaperId") { type = NavType.StringType })
            ) { backStackEntry ->
                val wpId = backStackEntry.arguments?.getString("wallpaperId") ?: ""
                val wallpaper = allWallpapers.find { it.id == wpId } ?: allWallpapers.firstOrNull()

                if (wallpaper != null) {
                    FullscreenViewerScreen(
                        initialWallpaper = wallpaper,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenInteractivePreview = { targetWp ->
                            navController.navigate(Screen.SmartPreview.createRoute(targetWp.id))
                        },
                        onOpenEditor = { targetWp ->
                            navController.navigate(Screen.Editor.createRoute(targetWp.fullUrl, targetWp.title))
                        }
                    )
                }
            }

            // 7. SMART PHONE MOCKUP PREVIEW
            composable(
                route = Screen.SmartPreview.route,
                arguments = listOf(navArgument("wallpaperId") { type = NavType.StringType })
            ) { backStackEntry ->
                val wpId = backStackEntry.arguments?.getString("wallpaperId") ?: ""
                val wallpaper = allWallpapers.find { it.id == wpId } ?: allWallpapers.firstOrNull()

                if (wallpaper != null) {
                    SmartPreviewScreen(
                        wallpaper = wallpaper,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onOpenClockEditor = { showClockSheet = true },
                        onOpenIconEditor = { showIconSheet = true }
                    )
                }
            }

            // 8. ADVANCED PHOTO / WALLPAPER EDITOR
            composable(
                route = Screen.Editor.route,
                arguments = listOf(
                    navArgument("source") { type = NavType.StringType },
                    navArgument("title") {
                        type = NavType.StringType
                        defaultValue = "Wallpaper"
                    }
                )
            ) { backStackEntry ->
                val rawSource = backStackEntry.arguments?.getString("source") ?: ""
                val rawTitle = backStackEntry.arguments?.getString("title") ?: "Wallpaper"
                val source = Uri.decode(rawSource)
                val title = Uri.decode(rawTitle)

                WallpaperEditorScreen(
                    wallpaperSource = source,
                    wallpaperTitle = title,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { savedWp ->
                        navController.popBackStack()
                        navController.navigate(Screen.Viewer.createRoute(savedWp.id))
                    }
                )
            }

            // 9. SEARCH SCREEN
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onOpenWallpaper = { wp ->
                        viewModel.trackRecentlyViewed(wp)
                        navController.navigate(Screen.Viewer.createRoute(wp.id))
                    }
                )
            }
        }

        // Global Modal Sheets (Clock & Icon Customizers)
        if (showClockSheet) {
            ClockCustomizerSheet(
                viewModel = viewModel,
                onDismiss = { showClockSheet = false }
            )
        }

        if (showIconSheet) {
            IconCustomizerSheet(
                viewModel = viewModel,
                onDismiss = { showIconSheet = false }
            )
        }
    }
}
