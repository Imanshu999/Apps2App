package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.AppCatalog
import com.example.data.AppItem
import com.example.data.Categories
import com.example.data.database.DownloadEntity
import com.example.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    // UI state flows
    val filteredApps by viewModel.filteredApps.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val showGamesOnly by viewModel.showGamesOnly.collectAsStateWithLifecycle()
    val selectedAppId by viewModel.selectedAppId.collectAsStateWithLifecycle()
    val activeInstallingAppId by viewModel.activeInstallingAppId.collectAsStateWithLifecycle()

    // Navigation and sub-routing tab states
    var currentTab by remember { mutableStateOf("discover") }

    val selectedAppItem = selectedAppId?.let { AppCatalog.getItemById(it) }

    // Overlaid Safe Installer prompt popup
    val installingApp = activeInstallingAppId?.let { AppCatalog.getItemById(it) }
    if (installingApp != null) {
        Dialog(onDismissRequest = { viewModel.cancelInstallation() }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Safe Checkmark Seal
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Safe Seal",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Apps2App Safe Installer",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Do you want to install ${installingApp.title}?\nVersion: ${installingApp.version}\n\nOur sandbox engine verified this signature as safe and ad-free.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.cancelInstallation() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = { viewModel.completeInstallation(installingApp.id) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("install_confirm_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Install", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
            }
        }
    }

    // Capture standard Android Back presses
    if (selectedAppItem != null) {
        BackHandler {
            viewModel.selectApp(null)
        }
    }

    Scaffold(
        bottomBar = {
            if (selectedAppItem == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    NavigationBarItem(
                        selected = currentTab == "discover",
                        onClick = { currentTab = "discover" },
                        icon = { Icon(imageVector = if (currentTab == "discover") Icons.Filled.GridView else Icons.Outlined.GridView, contentDescription = "Discover") },
                        label = { Text("Discover") },
                        modifier = Modifier.testTag("nav_tab_discover")
                    )
                    NavigationBarItem(
                        selected = currentTab == "ai_analyst",
                        onClick = { currentTab = "ai_analyst" },
                        icon = { Icon(imageVector = if (currentTab == "ai_analyst") Icons.Filled.Psychology else Icons.Outlined.Psychology, contentDescription = "AI Analyst") },
                        label = { Text("AI Advisor") },
                        modifier = Modifier.testTag("nav_tab_ai")
                    )
                    NavigationBarItem(
                        selected = currentTab == "library",
                        onClick = { currentTab = "library" },
                        icon = { Icon(imageVector = if (currentTab == "library") Icons.Filled.FolderSpecial else Icons.Outlined.FolderSpecial, contentDescription = "Library") },
                        label = { Text("My Library") },
                        modifier = Modifier.testTag("nav_tab_library")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = selectedAppItem ?: currentTab,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { targetState ->
                when (targetState) {
                    is AppItem -> {
                        // Detailed Single App Specs and Controller Section Page
                        AppDetailsView(
                            appItem = targetState,
                            bookmarks = bookmarks,
                            downloads = downloads,
                            viewModel = viewModel,
                            onBackPressed = { viewModel.selectApp(null) }
                        )
                    }
                    "discover" -> {
                        DiscoverView(
                            apps = filteredApps,
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            showGamesOnly = showGamesOnly,
                            downloads = downloads,
                            onQueryChanged = { viewModel.setSearchQuery(it) },
                            onCategorySelected = { viewModel.setCategory(it) },
                            onFilterTypeSelected = { viewModel.setFilterType(it) },
                            onAppSelected = { viewModel.selectApp(it.id) }
                        )
                    }
                    "ai_analyst" -> {
                        // General Advice interface
                        GeneralAiView(viewModel = viewModel)
                    }
                    "library" -> {
                        // Offline downloaded status and bookmarks state controller
                        LibraryView(
                            bookmarks = bookmarks,
                            downloads = downloads,
                            viewModel = viewModel,
                            onAppSelected = { viewModel.selectApp(it.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverView(
    apps: List<AppItem>,
    searchQuery: String,
    selectedCategory: String,
    showGamesOnly: Boolean?,
    downloads: List<DownloadEntity>,
    onQueryChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onFilterTypeSelected: (Boolean?) -> Unit,
    onAppSelected: (AppItem) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize()) {
        // App Header
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "APPS2APP",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Premium & Safe Sandbox Hub",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Small glowing green tag representing online safety
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.Green, CircleShape)
                    )
                    Text(
                        text = "SANDBOX OK",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Premium Styled Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChanged,
            placeholder = { Text("Search premium mods, features or packages...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("discover_search_bar")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hero rotating featured App Banner representing visual highlight
        val carouselItem = AppCatalog.items.first() // Spotify
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1DB954),
                            Color(0xFF0F2027),
                            Color(0xFF203A43)
                        )
                    )
                )
                .clickable { onAppSelected(carouselItem) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "APP OF THE WEEK",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = carouselItem.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )

                Text(
                    text = "Unlocked Hi-Fi Stream & Ad bypass. Verified Clean.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Glow Arrow
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Details",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sub Filter Toggles: All | Apps | Games
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterSubChip(
                selected = showGamesOnly == null,
                label = "All Catalogs",
                onClick = { onFilterTypeSelected(null) },
                modifier = Modifier.weight(1f)
            )
            FilterSubChip(
                selected = showGamesOnly == false,
                label = "Apps",
                onClick = { onFilterTypeSelected(false) },
                icon = Icons.Default.Apps,
                modifier = Modifier.weight(1f)
            )
            FilterSubChip(
                selected = showGamesOnly == true,
                label = "Games",
                onClick = { onFilterTypeSelected(true) },
                icon = Icons.Default.Gamepad,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Category Pills Lazy List Row
        val currentCategories = if (showGamesOnly == true) Categories.GAMES else if (showGamesOnly == false) Categories.APPS else (Categories.APPS + Categories.GAMES).distinct()
        val allCategoryOptions = listOf("All") + currentCategories

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(allCategoryOptions) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                        .clickable { onCategorySelected(cat) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apps List Grid
        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "Not found",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No compatible mods found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Try clearing queries or filtering another catalog.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                items(apps, key = { it.id }) { app ->
                    val appDownload = downloads.find { it.appId == app.id }
                    AppRowItem(
                        app = app,
                        downloadEntity = appDownload,
                        onClick = { onAppSelected(app) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterSubChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f) else Color.Transparent,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = if (selected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppRowItem(
    app: AppItem,
    downloadEntity: DownloadEntity?,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("app_item_${app.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Neon Logo Holder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = app.gradientColors.map { Color(it) }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Large initial
                Text(
                    text = app.title.first().toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = app.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    // Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "MOD",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${app.category} • ${app.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Highlight standard Mod features
                Text(
                    text = app.modFeatures.first(),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Trail action button indicating installation progress or normal action button
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = app.rating.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (downloadEntity != null) {
                    val label = when (downloadEntity.status) {
                        "PENDING" -> "Queue..."
                        "DOWNLOADING" -> "${(downloadEntity.progress * 100).toInt()}%"
                        "STYLING_INSTALLER" -> "Verifying..."
                        "INSTALLED" -> "Installed"
                        else -> "Get"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (downloadEntity.status == "INSTALLED") MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (downloadEntity.status == "INSTALLED") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Download icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun AppDetailsView(
    appItem: AppItem,
    bookmarks: List<com.example.data.database.BookmarkEntity>,
    downloads: List<DownloadEntity>,
    viewModel: AppViewModel,
    onBackPressed: () -> Unit
) {
    // Determine bookmasked status
    val isBookmarked = bookmarks.any { it.appId == appItem.id }
    val downloadStatus = downloads.find { it.appId == appItem.id }

    // Tab state: "overview" or "analyst" (AI Advisor)
    var detailTab by remember { mutableStateOf("overview") }
    var chatInput by remember { mutableStateOf("") }
    val aiChatHistory by viewModel.aiChatHistory.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Detailed Custom Context-colored Atmospheric Backdrop Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(appItem.gradientColors.first()).copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.testTag("app_detail_back")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { viewModel.toggleBookmark(appItem) },
                    modifier = Modifier.testTag("app_detail_bookmark")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark button",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // App Meta Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = appItem.gradientColors.map { Color(it) }
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = appItem.title.first().toString(),
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appItem.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = appItem.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = appItem.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "V${appItem.version}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Multi Spec Metadata Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SpecCell(label = "Downloads", value = "${appItem.downloads}+")
            SpecBorder()
            SpecCell(label = "File Size", value = appItem.size)
            SpecBorder()
            SpecCell(label = "Security Verified", value = "100% Clean")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GET APK Download / Interactive Progress Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            if (downloadStatus == null || downloadStatus.status == "CANCELLED") {
                Button(
                    onClick = { viewModel.startDownload(appItem) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("get_apk_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Get Premium APK", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        val progressNormalized = downloadStatus.progress
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressNormalized)
                                .fillMaxHeight()
                                .background(
                                    if (downloadStatus.status == "INSTALLED") MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.primary
                                )
                        )

                        // Label
                        val labelText = when (downloadStatus.status) {
                            "PENDING" -> "Locking connection pool..."
                            "DOWNLOADING" -> "Downloading Premium APK: ${(progressNormalized * 100).toInt()}%"
                            "STYLING_INSTALLER" -> "Verifying signatures..."
                            "INSTALLED" -> "Installed successfully!"
                            else -> "Processing..."
                        }

                        Text(
                            text = labelText,
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (progressNormalized > 0.45f && downloadStatus.status != "INSTALLED") Color.Black else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 12.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    if (downloadStatus.status == "INSTALLED") {
                        IconButton(
                            onClick = { viewModel.uninstallApp(appItem.id) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
                                .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Uninstall",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Tabs to switch between Description / AI Advisor Analyst
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TabHeaderOption(
                selected = detailTab == "overview",
                title = "Overview & Mods",
                onClick = { detailTab = "overview" },
                modifier = Modifier.weight(1f)
            )

            TabHeaderOption(
                selected = detailTab == "analyst",
                title = "AI Analyst Advisor",
                onClick = { detailTab = "analyst" },
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
        ) {
            AnimatedContent(targetState = detailTab, label = "TabAnimate") { selected ->
                if (selected == "overview") {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Text(
                                text = "About this application",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = appItem.fullDescription,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }

                        item {
                            Text(
                                text = "MOD Specifications",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                appItem.modFeatures.forEach { feat ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Shield Check",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = feat,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Security Status",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Verified Sandbox Pass",
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = appItem.developerNote,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Chat Interface with app analyst
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Chats message field
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            if (aiChatHistory.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.Psychology,
                                                contentDescription = null,
                                                modifier = Modifier.size(48.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = "Apps2App AI Mod Analyst Ready",
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                            Text(
                                                text = "Ask safety queries, verify mod parameters, or requesting debug advice about '${appItem.title}'.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 24.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(aiChatHistory) { msg ->
                                    val isUser = msg.sender == "USER"
                                    ChatBubble(text = msg.text, isUser = isUser)
                                }
                            }

                            if (isAiLoading) {
                                item {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Analyst is parsing logs & signatures...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        // Input field
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatInput,
                                onValueChange = { chatInput = it },
                                placeholder = { Text("Ask about safe installation, tracker blocks...") },
                                maxLines = 3,
                                singleLine = false,
                                shape = RoundedCornerShape(20.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedContainerColor = MaterialTheme.colorScheme.background,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("ai_details_input")
                            )

                            IconButton(
                                onClick = {
                                    if (chatInput.trim().isNotEmpty()) {
                                        viewModel.askAiAnalyst(appItem, chatInput)
                                        chatInput = ""
                                    }
                                },
                                enabled = !isAiLoading && chatInput.trim().isNotEmpty(),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .testTag("ai_details_send")
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send advice query")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun TabHeaderOption(
    selected: Boolean,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(if (selected) Color.Transparent else MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            )
        }
    }
}

@Composable
fun SpecCell(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SpecBorder() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(24.dp)
            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
    )
}

@Composable
fun GeneralAiView(viewModel: AppViewModel) {
    var generalQuery by remember { mutableStateOf("") }
    val generalApp = AppCatalog.items.first() // Representative app
    val aiChatHistory by viewModel.aiChatHistory.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header Title
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "AI MOD ADVISOR",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Cybersecurity Diagnostics & Signature Safe checks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chats History Box
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (aiChatHistory.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Sandbox Security Inspector",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Ask me anything about Android MOD security:\n• Safe APK installation checks\n• Bypassing tracker integrations\n• Bypassing verification locks\n• Re-signing keys details",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            } else {
                items(aiChatHistory) { msg ->
                    ChatBubble(text = msg.text, isUser = msg.sender == "USER")
                }
            }

            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "AI Advisor is analyzing reverse engineering database...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Search Input Bar bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = generalQuery,
                onValueChange = { generalQuery = it },
                placeholder = { Text("Ask safe mod questions...") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_general_input")
            )

            IconButton(
                onClick = {
                    if (generalQuery.trim().isNotEmpty()) {
                        viewModel.askAiAnalyst(generalApp, generalQuery)
                        generalQuery = ""
                    }
                },
                enabled = !isAiLoading && generalQuery.trim().isNotEmpty(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .testTag("ai_general_send")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun LibraryView(
    bookmarks: List<com.example.data.database.BookmarkEntity>,
    downloads: List<DownloadEntity>,
    viewModel: AppViewModel,
    onAppSelected: (AppItem) -> Unit
) {
    // Switch between Bookmarked and History
    var libraryTab by remember { mutableStateOf("bookmarks") }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "MY LIBRARY",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Offline Installation packages & bookmarks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Option row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            TabHeaderOption(
                selected = libraryTab == "bookmarks",
                title = "Favorites (${bookmarks.size})",
                onClick = { libraryTab = "bookmarks" },
                modifier = Modifier.weight(1f)
            )

            TabHeaderOption(
                selected = libraryTab == "downloads",
                title = "Downloads (${downloads.size})",
                onClick = { libraryTab = "downloads" },
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
        ) {
            AnimatedContent(targetState = libraryTab, label = "LibraryContent") { selectedTab ->
                if (selectedTab == "bookmarks") {
                    if (bookmarks.isEmpty()) {
                        EmptyLibraryState(
                            title = "No Bookmarks Saved",
                            desc = "Tap the bookmark icon in any Premium App details view to save it to your quick discovery chest.",
                            icon = Icons.Outlined.BookmarkBorder
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(bookmarks, key = { it.appId }) { item ->
                                val premiumCatalogItem = AppCatalog.getItemById(item.appId)
                                if (premiumCatalogItem != null) {
                                    AppRowItem(
                                        app = premiumCatalogItem,
                                        downloadEntity = downloads.find { it.appId == premiumCatalogItem.id },
                                        onClick = { onAppSelected(premiumCatalogItem) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    if (downloads.isEmpty()) {
                        EmptyLibraryState(
                            title = "No downloads yet",
                            desc = "Downloads will appear here. Get dynamic APK progress controls, styled checkmarks system reviews, and safely download modded components.",
                            icon = Icons.Outlined.CloudDownload
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(downloads, key = { it.appId }) { dl ->
                                val premiumCatalogItem = AppCatalog.getItemById(dl.appId)
                                if (premiumCatalogItem != null) {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onAppSelected(premiumCatalogItem) }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(46.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        Brush.linearGradient(
                                                            colors = premiumCatalogItem.gradientColors.map { Color(it) }
                                                        )
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = premiumCatalogItem.title.first().toString(),
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace),
                                                    color = Color.White
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(14.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = dl.title,
                                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = "Status: ${dl.status.replace("_", " ")}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = if (dl.status == "INSTALLED") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            IconButton(
                                                onClick = { viewModel.uninstallApp(dl.appId) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Uninstall Package",
                                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLibraryState(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
