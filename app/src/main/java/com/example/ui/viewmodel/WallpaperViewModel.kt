package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiGenerationConfig
import com.example.ai.AiWallpaperEngine
import com.example.ai.AiWallpaperResult
import com.example.data.local.AiHistoryEntity
import com.example.data.local.CollectionEntity
import com.example.data.local.DownloadEntity
import com.example.data.local.FavoriteEntity
import com.example.data.repository.WallpaperRepository
import com.example.model.ClockConfig
import com.example.model.EditorState
import com.example.model.IconConfig
import com.example.model.Wallpaper
import com.example.model.WallpaperCategory
import com.example.model.WallpaperResolution
import com.example.ui.theme.AppThemeMode
import com.example.util.BitmapHelper
import com.example.util.WallpaperSetter
import com.example.util.WallpaperTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WallpaperRepository(application)

    // Online status
    val isOnline: StateFlow<Boolean> = repository.isOnline

    // Theme Mode
    private val _themeMode = MutableStateFlow(AppThemeMode.DARK)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Library Wallpapers
    val allWallpapers: StateFlow<List<Wallpaper>> = repository.wallpapers

    // Filters
    private val _selectedCategory = MutableStateFlow(WallpaperCategory.ALL)
    val selectedCategory: StateFlow<WallpaperCategory> = _selectedCategory.asStateFlow()

    private val _selectedResolution = MutableStateFlow<WallpaperResolution?>(null)
    val selectedResolution: StateFlow<WallpaperResolution?> = _selectedResolution.asStateFlow()

    // Filtered Wallpapers for Explore / Library
    val filteredWallpapers: StateFlow<List<Wallpaper>> = combine(
        allWallpapers,
        _selectedCategory,
        _selectedResolution
    ) { wallpapers, category, resolution ->
        wallpapers.filter { wp ->
            val matchCategory = category == WallpaperCategory.ALL || wp.category.equals(category.id, ignoreCase = true)
            val matchResolution = resolution == null || wp.resolution == resolution
            matchCategory && matchResolution
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Featured, Trending, Latest, AI sections
    val featuredWallpapers: StateFlow<List<Wallpaper>> = allWallpapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.isFeatured }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trendingWallpapers: StateFlow<List<Wallpaper>> = allWallpapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.isTrending }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestWallpapers: StateFlow<List<Wallpaper>> = allWallpapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.isLatest }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiWallpapers: StateFlow<List<Wallpaper>> = allWallpapers.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.isAiGenerated }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recently viewed list
    private val _recentlyViewed = MutableStateFlow<List<Wallpaper>>(emptyList())
    val recentlyViewed: StateFlow<List<Wallpaper>> = _recentlyViewed.asStateFlow()

    // Room Favorites
    val favorites: StateFlow<List<FavoriteEntity>> = repository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Collections
    val collections: StateFlow<List<CollectionEntity>> = repository.getAllCollections()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room Downloads
    val downloads: StateFlow<List<DownloadEntity>> = repository.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Room AI History
    val aiHistory: StateFlow<List<AiHistoryEntity>> = repository.getAllAiHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Clock & Icon Customization Configurations
    private val _clockConfig = MutableStateFlow(ClockConfig())
    val clockConfig: StateFlow<ClockConfig> = _clockConfig.asStateFlow()

    private val _iconConfig = MutableStateFlow(IconConfig())
    val iconConfig: StateFlow<IconConfig> = _iconConfig.asStateFlow()

    // Active Editor State
    private val _editorState = MutableStateFlow(EditorState())
    val editorState: StateFlow<EditorState> = _editorState.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _recentSearches = MutableStateFlow(listOf("Cyberpunk", "AMOLED", "Supercar", "Space", "Anime", "Minimal"))
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    val searchResults: StateFlow<List<Wallpaper>> = combine(
        allWallpapers,
        _searchQuery
    ) { wallpapers, query ->
        if (query.isBlank()) emptyList()
        else repository.searchWallpapers(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Generation UI State
    private val _isGeneratingAi = MutableStateFlow(false)
    val isGeneratingAi: StateFlow<Boolean> = _isGeneratingAi.asStateFlow()

    private val _lastAiResult = MutableStateFlow<AiWallpaperResult?>(null)
    val lastAiResult: StateFlow<AiWallpaperResult?> = _lastAiResult.asStateFlow()

    // Active Fullscreen Viewer selection
    private val _selectedWallpaperForViewer = MutableStateFlow<Wallpaper?>(null)
    val selectedWallpaperForViewer: StateFlow<Wallpaper?> = _selectedWallpaperForViewer.asStateFlow()

    // User photo for Wallpaper Maker
    private val _customPhotoUri = MutableStateFlow<String?>(null)
    val customPhotoUri: StateFlow<String?> = _customPhotoUri.asStateFlow()

    // Action Methods
    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun selectCategory(category: WallpaperCategory) {
        _selectedCategory.value = category
    }

    fun selectResolution(resolution: WallpaperResolution?) {
        _selectedResolution.value = resolution
    }

    fun openViewer(wallpaper: Wallpaper) {
        _selectedWallpaperForViewer.value = wallpaper
        trackRecentlyViewed(wallpaper)
    }

    fun trackRecentlyViewed(wallpaper: Wallpaper) {
        val current = _recentlyViewed.value.filter { it.id != wallpaper.id }.toMutableList()
        current.add(0, wallpaper)
        _recentlyViewed.value = current.take(10)
    }

    fun closeViewer() {
        _selectedWallpaperForViewer.value = null
    }

    fun toggleFavorite(wallpaper: Wallpaper) {
        viewModelScope.launch {
            repository.toggleFavorite(wallpaper)
        }
    }

    fun isWallpaperFavorite(id: String): Boolean {
        return favorites.value.any { it.wallpaperId == id }
    }

    // Collections Management
    fun createCollection(name: String, description: String = "", coverUrl: String = "") {
        viewModelScope.launch {
            repository.createCollection(name, description, coverUrl)
        }
    }

    fun deleteCollection(id: Long) {
        viewModelScope.launch {
            repository.deleteCollection(id)
        }
    }

    fun addWallpaperToCollection(collectionId: Long, wallpaperId: String) {
        viewModelScope.launch {
            repository.addWallpaperToCollection(collectionId, wallpaperId)
        }
    }

    fun removeWallpaperFromCollection(collectionId: Long, wallpaperId: String) {
        viewModelScope.launch {
            repository.removeWallpaperFromCollection(collectionId, wallpaperId)
        }
    }

    // Clock & Icon Customizer
    fun updateClockConfig(config: ClockConfig) {
        _clockConfig.value = config
    }

    fun updateClockOffset(offsetX: Float, offsetY: Float) {
        _clockConfig.value = _clockConfig.value.copy(
            customOffsetX = offsetX,
            customOffsetY = offsetY
        )
    }

    fun updateIconConfig(config: IconConfig) {
        _iconConfig.value = config
    }

    // Wallpaper Editor
    fun updateEditorState(state: EditorState) {
        _editorState.value = state
    }

    fun resetEditorState() {
        _editorState.value = EditorState()
    }

    fun setCustomPhotoUri(uri: String?) {
        _customPhotoUri.value = uri
    }

    // Wallpaper Setting
    fun applyWallpaper(
        context: Context,
        wallpaper: Wallpaper,
        target: WallpaperTarget,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val bitmap = BitmapHelper.loadBitmapSafe(context, wallpaper.fullUrl)
            if (bitmap != null) {
                val result = WallpaperSetter.setWallpaper(context, bitmap, target)
                withContext(Dispatchers.Main) {
                    if (result.isSuccess) {
                        Toast.makeText(context, "Wallpaper applied to ${target.label}!", Toast.LENGTH_SHORT).show()
                        onComplete(true)
                    } else {
                        Toast.makeText(context, "Failed to apply wallpaper", Toast.LENGTH_SHORT).show()
                        onComplete(false)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Unable to load wallpaper bitmap", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }
        }
    }

    // Download Wallpaper
    fun downloadWallpaper(context: Context, wallpaper: Wallpaper, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val bitmap = BitmapHelper.loadBitmapSafe(context, wallpaper.fullUrl)
            if (bitmap != null) {
                val uri = BitmapHelper.saveBitmapToGallery(context, bitmap, wallpaper.title)
                if (uri != null) {
                    repository.recordDownload(
                        wallpaperId = wallpaper.id,
                        title = wallpaper.title,
                        localUri = uri.toString(),
                        fileSizeBytes = (bitmap.allocationByteCount).toLong(),
                        resolution = wallpaper.resolution.label
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Downloaded to Gallery: Wallora folder!", Toast.LENGTH_SHORT).show()
                        onComplete(true)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to save wallpaper", Toast.LENGTH_SHORT).show()
                        onComplete(false)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Could not fetch image to download", Toast.LENGTH_SHORT).show()
                    onComplete(false)
                }
            }
        }
    }

    fun deleteDownload(wallpaperId: String) {
        viewModelScope.launch {
            repository.deleteDownload(wallpaperId)
        }
    }

    // Save Edited Wallpaper to Gallery and add to Library
    fun saveEditedWallpaper(
        context: Context,
        baseBitmap: Bitmap,
        title: String = "Wallora_Custom",
        onSuccess: (Wallpaper) -> Unit
    ) {
        viewModelScope.launch {
            val transformed = BitmapHelper.applyEditorTransformations(baseBitmap, _editorState.value)
            val uri = BitmapHelper.saveBitmapToGallery(context, transformed, title)
            if (uri != null) {
                val newWp = Wallpaper(
                    id = "custom_${System.currentTimeMillis()}",
                    title = title,
                    author = "You (Wallora Studio)",
                    category = WallpaperCategory.ABSTRACT.id,
                    resolution = WallpaperResolution.UHD_4K,
                    thumbnailUrl = uri.toString(),
                    fullUrl = uri.toString(),
                    tags = listOf("custom", "edited", "photo"),
                    isLatest = true
                )
                repository.addCustomWallpaper(newWp)
                repository.recordDownload(
                    wallpaperId = newWp.id,
                    title = newWp.title,
                    localUri = uri.toString(),
                    fileSizeBytes = transformed.allocationByteCount.toLong(),
                    resolution = "4K Custom"
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Saved to Gallery and Library!", Toast.LENGTH_SHORT).show()
                    onSuccess(newWp)
                }
            }
        }
    }

    // AI Generation
    fun generateAiWallpaper(context: Context, config: AiGenerationConfig) {
        _isGeneratingAi.value = true
        viewModelScope.launch {
            val result = AiWallpaperEngine.generateWallpaper(context, config)
            _isGeneratingAi.value = false
            if (result.isSuccess) {
                val aiRes = result.getOrNull()
                _lastAiResult.value = aiRes
                if (aiRes != null) {
                    repository.recordAiGeneration(
                        prompt = aiRes.prompt,
                        style = aiRes.style,
                        aspectRatio = aiRes.aspectRatio,
                        quality = aiRes.quality,
                        localUri = aiRes.localUri
                    )
                    val newWp = Wallpaper(
                        id = aiRes.id,
                        title = "AI: ${aiRes.prompt.take(24)}...",
                        author = "Wallora AI (${aiRes.style})",
                        category = WallpaperCategory.ABSTRACT.id,
                        resolution = WallpaperResolution.UHD_4K,
                        thumbnailUrl = aiRes.localUri,
                        fullUrl = aiRes.localUri,
                        tags = listOf("ai", aiRes.style.lowercase(), "generated"),
                        isAiGenerated = true,
                        isLatest = true,
                        description = aiRes.prompt
                    )
                    repository.addCustomWallpaper(newWp)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "AI Wallpaper Generated Successfully!", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Generation failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Search
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun submitSearch(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank() && !_recentSearches.value.contains(query)) {
            val list = _recentSearches.value.toMutableList()
            list.add(0, query)
            _recentSearches.value = list.take(8)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }
}
