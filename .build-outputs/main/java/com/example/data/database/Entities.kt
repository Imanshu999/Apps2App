package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val appId: String,
    val title: String,
    val category: String,
    val version: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val appId: String,
    val title: String,
    val version: String,
    val size: String,
    val progress: Float, // 0.0f to 1.0f (or progress bar indicator)
    val status: String,   // "PENDING", "DOWNLOADING", "STYLING_INSTALLER", "INSTALLED"
    val timestamp: Long = System.currentTimeMillis()
)
