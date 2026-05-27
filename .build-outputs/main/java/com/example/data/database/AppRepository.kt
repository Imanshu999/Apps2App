package com.example.data.database

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    // Bookmarks
    val allBookmarks: Flow<List<BookmarkEntity>> = appDao.getAllBookmarks()

    fun getBookmarkById(appId: String): Flow<BookmarkEntity?> = appDao.getBookmarkById(appId)

    suspend fun insertBookmark(bookmark: BookmarkEntity) {
        appDao.insertBookmark(bookmark)
    }

    suspend fun deleteBookmarkById(appId: String) {
        appDao.deleteBookmarkById(appId)
    }

    // Downloads
    val allDownloads: Flow<List<DownloadEntity>> = appDao.getAllDownloads()

    fun getDownloadById(appId: String): Flow<DownloadEntity?> = appDao.getDownloadById(appId)

    suspend fun insertOrUpdateDownload(download: DownloadEntity) {
        appDao.insertOrUpdateDownload(download)
    }

    suspend fun deleteDownloadById(appId: String) {
        appDao.deleteDownloadById(appId)
    }

    suspend fun updateDownloadStatus(appId: String, progress: Float, status: String) {
        appDao.updateDownloadStatus(appId, progress, status)
    }
}
