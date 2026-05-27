package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppCatalog
import com.example.data.AppItem
import com.example.data.database.AppDatabase
import com.example.data.database.AppRepository
import com.example.data.database.BookmarkEntity
import com.example.data.database.DownloadEntity
import com.example.network.GeminiClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appDao())
    }

    // UI state streams
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadEntity>> = repository.allDownloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search and filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _showGamesOnly = MutableStateFlow<Boolean?>(null) // null = all, true = games, false = apps
    val showGamesOnly: StateFlow<Boolean?> = _showGamesOnly.asStateFlow()

    // Filtered Apps
    val filteredApps: StateFlow<List<AppItem>> = combine(
        searchQuery, selectedCategory, showGamesOnly
    ) { query, category, gamesOnly ->
        AppCatalog.items.filter { item ->
            val matchesQuery = item.title.contains(query, ignoreCase = true) ||
                    item.packageName.contains(query, ignoreCase = true) ||
                    item.fullDescription.contains(query, ignoreCase = true) ||
                    item.modFeatures.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = category == "All" || item.category == category

            val matchesType = gamesOnly == null || item.isGame == gamesOnly

            matchesQuery && matchesCategory && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppCatalog.items)

    // Current selected app details or route selection
    private val _selectedAppId = MutableStateFlow<String?>(null)
    val selectedAppId: StateFlow<String?> = _selectedAppId.asStateFlow()

    fun selectApp(appId: String?) {
        _selectedAppId.value = appId
        if (appId != null) {
            clearAiChat()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setFilterType(gamesOnly: Boolean?) {
        _showGamesOnly.value = gamesOnly
    }

    // Bookmarking Toggle
    fun toggleBookmark(appItem: AppItem) {
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.appId == appItem.id }
            if (isBookmarked) {
                repository.deleteBookmarkById(appItem.id)
            } else {
                repository.insertBookmark(
                    BookmarkEntity(
                        appId = appItem.id,
                        title = appItem.title,
                        category = appItem.category,
                        version = appItem.version
                    )
                )
            }
        }
    }

    // Interactive Simulated Downloads & Installer logic
    private val _activeInstallingAppId = MutableStateFlow<String?>(null)
    val activeInstallingAppId: StateFlow<String?> = _activeInstallingAppId.asStateFlow()

    fun startDownload(appItem: AppItem) {
        viewModelScope.launch {
            // Put into PENDING state
            repository.insertOrUpdateDownload(
                DownloadEntity(
                    appId = appItem.id,
                    title = appItem.title,
                    version = appItem.version,
                    size = appItem.size,
                    progress = 0.0f,
                    status = "PENDING"
                )
            )
            delay(800)

            // DOWNLOADING progress simulation loop
            var step = 0.0f
            while (step <= 1.0f) {
                repository.updateDownloadStatus(appItem.id, step, "DOWNLOADING")
                delay(120)
                step += 0.05f
            }

            // STYLING_INSTALLER phase
            repository.updateDownloadStatus(appItem.id, 1.0f, "STYLING_INSTALLER")
            delay(1000)

            // Trigger installer visual prompt overlay
            _activeInstallingAppId.value = appItem.id
        }
    }

    fun completeInstallation(appId: String) {
        _activeInstallingAppId.value = null
        viewModelScope.launch {
            repository.updateDownloadStatus(appId, 1.0f, "INSTALLED")
        }
    }

    fun cancelInstallation() {
        val appId = _activeInstallingAppId.value
        _activeInstallingAppId.value = null
        if (appId != null) {
            viewModelScope.launch {
                repository.updateDownloadStatus(appId, 0.0f, "CANCELLED")
            }
        }
    }

    fun uninstallApp(appId: String) {
        viewModelScope.launch {
            repository.deleteDownloadById(appId)
        }
    }

    // AI Mod Analyst Chat Interface
    data class ChatMessage(val sender: String, val text: String, val timestamp: Long = System.currentTimeMillis())

    private val _aiChatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val aiChatHistory: StateFlow<List<ChatMessage>> = _aiChatHistory.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    fun clearAiChat() {
        _aiChatHistory.value = emptyList()
    }

    fun askAiAnalyst(appItem: AppItem, userQuery: String) {
        if (userQuery.trim().isEmpty()) return

        val userMessage = ChatMessage("USER", userQuery)
        _aiChatHistory.value = _aiChatHistory.value + userMessage
        _isAiLoading.value = true

        val systemPrompt = """
            You are the specialized Apps2App AI Mod Analyst, an expert Android reverse engineer and cybersecurity researcher.
            You are analyzing the application: '${appItem.title}' (Package: ${appItem.packageName}, Category: ${itemCategoryPromptStr(appItem)}).
            The app features these mods: ${appItem.modFeatures.joinToString(", ")}.
            
            Your guidelines:
            1. Keep answers concise, highly structured, authoritative, yet friendly and informative.
            2. Fully detail potential privacy advantages (e.g. tracker blocking with unofficial clients) vs security guidelines (e.g. download only from certified sites like Apps2App).
            3. Act strictly as a system debugger/engineer. Do not write full generic paragraphs, use descriptive bullet points instead.
            4. If asked about keys or how to hack, explain the security of APK signing keys and standard security practices.
        """.trimIndent()

        val fullPrompt = """
            User asks: "$userQuery"
            Provide an elegant reverse-engineering style report advising on mod feature safety, functionality, or install methods for '${appItem.title}'.
        """.trimIndent()

        viewModelScope.launch {
            val response = GeminiClient.generateResponse(prompt = fullPrompt, systemPrompt = systemPrompt)
            val aiMessage = ChatMessage("AI", response)
            _aiChatHistory.value = _aiChatHistory.value + aiMessage
            _isAiLoading.value = false
        }
    }

    private fun itemCategoryPromptStr(item: AppItem): String {
        return if (item.isGame) "Game (${item.category})" else "Application (${item.category})"
    }
}
