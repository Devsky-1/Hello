package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val wallpaperId: String,
    val title: String,
    val category: String,
    val fullUrl: String,
    val thumbnailUrl: String,
    val resolution: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val coverUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "collection_items",
    primaryKeys = ["collectionId", "wallpaperId"]
)
data class CollectionItemEntity(
    val collectionId: Long,
    val wallpaperId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val wallpaperId: String,
    val title: String,
    val localUri: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val fileSizeBytes: Long = 0L,
    val resolution: String = "4K Ultra HD"
)

@Entity(tableName = "ai_history")
data class AiHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val prompt: String,
    val style: String,
    val aspectRatio: String,
    val quality: String,
    val localUri: String,
    val createdAt: Long = System.currentTimeMillis()
)
