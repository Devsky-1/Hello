package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.data.local.AiHistoryEntity
import com.example.data.local.AppDatabase
import com.example.data.local.CollectionEntity
import com.example.data.local.CollectionItemEntity
import com.example.data.local.DownloadEntity
import com.example.data.local.FavoriteEntity
import com.example.model.Wallpaper
import com.example.model.WallpaperCategory
import com.example.model.WallpaperResolution
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class WallpaperRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context)
) {
    private val favoriteDao = database.favoriteDao()
    private val collectionDao = database.collectionDao()
    private val downloadDao = database.downloadDao()
    private val aiHistoryDao = database.aiHistoryDao()

    // Network connectivity status
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // In-memory list of all wallpapers (combines curated library + user created)
    private val _wallpapers = MutableStateFlow(SampleWallpaperData.wallpapers)
    val wallpapers: StateFlow<List<Wallpaper>> = _wallpapers.asStateFlow()

    init {
        monitorNetwork(context)
    }

    private fun monitorNetwork(context: Context) {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (cm != null) {
                val activeNetwork = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(activeNetwork)
                _isOnline.value = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

                val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

                cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isOnline.value = true
                    }

                    override fun onLost(network: Network) {
                        _isOnline.value = false
                    }
                })
            }
        } catch (e: Exception) {
            _isOnline.value = true
        }
    }

    // Dynamic wallpapers addition (e.g. from AI generator or Wallpaper Maker)
    fun addCustomWallpaper(wallpaper: Wallpaper) {
        val current = _wallpapers.value.toMutableList()
        current.add(0, wallpaper)
        _wallpapers.value = current
    }

    fun getWallpaperById(id: String): Wallpaper? {
        return _wallpapers.value.find { it.id == id }
    }

    fun searchWallpapers(
        query: String,
        category: String? = null,
        resolution: WallpaperResolution? = null,
        tag: String? = null
    ): List<Wallpaper> {
        return _wallpapers.value.filter { wp ->
            val matchesQuery = query.isBlank() ||
                    wp.title.contains(query, ignoreCase = true) ||
                    wp.category.contains(query, ignoreCase = true) ||
                    wp.author.contains(query, ignoreCase = true) ||
                    wp.tags.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = category == null ||
                    category == WallpaperCategory.ALL.id ||
                    wp.category.equals(category, ignoreCase = true)

            val matchesResolution = resolution == null || wp.resolution == resolution

            val matchesTag = tag == null || wp.tags.any { it.equals(tag, ignoreCase = true) }

            matchesQuery && matchesCategory && matchesResolution && matchesTag
        }
    }

    // Favorites
    fun getAllFavorites(): Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites().flowOn(Dispatchers.IO)

    fun isFavorite(id: String): Flow<Boolean> = favoriteDao.isFavorite(id).flowOn(Dispatchers.IO)

    suspend fun toggleFavorite(wallpaper: Wallpaper) {
        val isFav = favoriteDao.isFavoriteDirect(wallpaper.id)
        if (isFav) {
            favoriteDao.deleteFavorite(wallpaper.id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    wallpaperId = wallpaper.id,
                    title = wallpaper.title,
                    category = wallpaper.category,
                    fullUrl = wallpaper.fullUrl,
                    thumbnailUrl = wallpaper.thumbnailUrl,
                    resolution = wallpaper.resolution.label
                )
            )
        }
    }

    // Collections
    fun getAllCollections(): Flow<List<CollectionEntity>> = collectionDao.getAllCollections().flowOn(Dispatchers.IO)

    suspend fun createCollection(name: String, description: String = "", coverUrl: String = ""): Long {
        return collectionDao.insertCollection(
            CollectionEntity(name = name, description = description, coverUrl = coverUrl)
        )
    }

    suspend fun deleteCollection(id: Long) {
        collectionDao.deleteItemsForCollection(id)
        collectionDao.deleteCollection(id)
    }

    fun getCollectionWallpaperIds(collectionId: Long): Flow<List<String>> =
        collectionDao.getWallpaperIdsForCollection(collectionId).flowOn(Dispatchers.IO)

    suspend fun addWallpaperToCollection(collectionId: Long, wallpaperId: String) {
        collectionDao.insertCollectionItem(CollectionItemEntity(collectionId, wallpaperId))
    }

    suspend fun removeWallpaperFromCollection(collectionId: Long, wallpaperId: String) {
        collectionDao.removeCollectionItem(collectionId, wallpaperId)
    }

    // Downloads
    fun getAllDownloads(): Flow<List<DownloadEntity>> = downloadDao.getAllDownloads().flowOn(Dispatchers.IO)

    fun isDownloaded(id: String): Flow<Boolean> = downloadDao.isDownloaded(id).flowOn(Dispatchers.IO)

    suspend fun recordDownload(
        wallpaperId: String,
        title: String,
        localUri: String,
        fileSizeBytes: Long,
        resolution: String = "4K Ultra HD"
    ) {
        downloadDao.insertDownload(
            DownloadEntity(
                wallpaperId = wallpaperId,
                title = title,
                localUri = localUri,
                fileSizeBytes = fileSizeBytes,
                resolution = resolution
            )
        )
    }

    suspend fun deleteDownload(wallpaperId: String) {
        downloadDao.deleteDownload(wallpaperId)
    }

    // AI History
    fun getAllAiHistory(): Flow<List<AiHistoryEntity>> = aiHistoryDao.getAllHistory().flowOn(Dispatchers.IO)

    suspend fun recordAiGeneration(
        prompt: String,
        style: String,
        aspectRatio: String,
        quality: String,
        localUri: String
    ): Long {
        return aiHistoryDao.insertHistory(
            AiHistoryEntity(
                prompt = prompt,
                style = style,
                aspectRatio = aspectRatio,
                quality = quality,
                localUri = localUri
            )
        )
    }

    suspend fun deleteAiHistory(id: Long) {
        aiHistoryDao.deleteHistory(id)
    }
}
