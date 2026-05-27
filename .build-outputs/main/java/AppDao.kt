package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE appId = :appId LIMIT 1")
    fun getBookmarkById(appId: String): Flow<BookmarkEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE appId = :appId")
    suspend fun deleteBookmarkById(appId: String)

    // Downloads
    @Query("SELECT * FROM downloads ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE appId = :appId LIMIT 1")
    fun getDownloadById(appId: String): Flow<DownloadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE appId = :appId")
    suspend fun deleteDownloadById(appId: String)

    @Query("UPDATE downloads SET progress = :progress, status = :status WHERE appId = :appId")
    suspend fun updateDownloadStatus(appId: String, progress: Float, status: String)
}
