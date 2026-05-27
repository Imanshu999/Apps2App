package com.example.data

object AppCatalog {
    val items = listOf(
        AppItem(
            id = "spotify_mod",
            title = "Spotify Premium MOD",
            packageName = "com.spotify.music",
            category = "Music & Audio",
            isGame = false,
            version = "9.1.44.2120",
            size = "58 MB",
            rating = 4.9f,
            downloads = "12.8M",
            developer = "Spotify AB (Sam Mods)",
            modFeatures = listOf(
                "Premium Features Unlocked",
                "No Ads / Unlimited Skips",
                "Extreme Audio Quality Unlocked",
                "Canvas & Genius Lyrics Enabled",
                "Offline Lyrics Syncing Support"
            ),
            fullDescription = "Experience the top musical universe without limitations. Spotify Premium Mod lets you play any song, swipe through any playlist, repeat, shuffle and experience the ultimate high-definition sound with completely bypassed visual or audio commercial interruptions.",
            gradientColors = listOf(0xFF1DB954, 0xFF191414, 0xFF1ED760)
        ),
        AppItem(
            id = "insta_pro",
            title = "Insta Pro (Instagram)",
            packageName = "com.instagram.android.pro",
            category = "Social & Communication",
            isGame = false,
            version = "14.80",
            size = "68 MB",
            rating = 4.8f,
            downloads = "8.4M",
            developer = "Sam Mods (Apps2App Staff)",
            modFeatures = listOf(
                "In-App High-Res Reels & Images Downloader",
                "Anonymously View Stories & Direct Messages",
                "Hide Direct Message 'Typing...' Indicator",
                "Ad-Free Feed and Sponsored Reels Block",
                "Built-in App Lock (PIN / Fingerprint)"
            ),
            fullDescription = "Take back control over your social feed. Insta Pro provides massive premium upgrades to the standard social framework, letting you download any visual material with a single tap, read DMs without triggering read receipts, and lock your app with system-grade privacy controllers.",
            gradientColors = listOf(0xFFC13584, 0xFF405DE6, 0xFFF77737)
        ),
        AppItem(
            id = "megapari",
            title = "Megapari Official Hub",
            packageName = "com.megapari.sports",
            category = "Tools",
            isGame = false,
            version = "1.25.1",
            size = "42 MB",
            rating = 4.5f,
            downloads = "2.1M",
            developer = "Megapari Team",
            modFeatures = listOf(
                "Premium Betting Visualizers",
                "Instant Odds Notification System",
                "Zero External Popup Ads",
                "No Registration Limit Tracker",
                "Optimized Low-bandwidth Core Engine"
            ),
            fullDescription = "Keep full, real-time control over major sports tournaments globally. Analyze key historical charts, secure live scoreboard feeds, and unlock expert analysts' dashboards in complete luxury styling with the official Apps2App wrapper integration.",
            gradientColors = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)
        ),
        AppItem(
            id = "minecraft_pe",
            title = "Minecraft Bedrock Edition",
            packageName = "com.mojang.minecraftpe",
            category = "Adventure",
            isGame = true,
            version = "1.21.30",
            size = "650 MB",
            rating = 4.9f,
            downloads = "43.5M",
            developer = "Mojang Studios (Modded Staff)",
            modFeatures = listOf(
                "All Premium Skins and Texture Packs Unlocked",
                "Single-Player God Mode & Damage Immunity",
                "Unlimited Inventories & Instant Resource Spawn",
                "Free Render Distance Adjuster Premium Engine",
                "Zero License Verification Check Bypass"
            ),
            fullDescription = "Build, explore, craft, and survive in the legendary sandbox standard. Play in infinite visual fields. With our custom bypass, premium skin-packs and visual texture enhancements are completely unlocked from first load-in without purchase.",
            gradientColors = listOf(0xFF2E8B57, 0xFF141F12, 0xFF3CB371)
        ),
        AppItem(
            id = "subway_surfers",
            title = "Subway Surfers VIP Elite",
            packageName = "com.kiloo.subwaysurf.vip",
            category = "Arcade",
            isGame = true,
            version = "3.32.0",
            size = "140 MB",
            rating = 4.7f,
            downloads = "98.2M",
            developer = "Kiloo (Modded Staff)",
            modFeatures = listOf(
                "Unlimited Gold Coins & Platinum Keys",
                "All Runners and Board Outfits Purchased",
                "Permanent 100x Score Multiplayer Boost",
                "No Ad Interstitial Breaks",
                "High Speed Gliding Physics Enabled"
            ),
            fullDescription = "Subway Surfers VIP Elite makes you a top-tier running superstar. Collect trillions of custom coins and keys to resume runs instantly. Dodge inspectors on neon hoverboards with unlimited double-jumps enabled in full fluid visual graphics.",
            gradientColors = listOf(0xFFF7971E, 0xFFFF007F, 0xFFFFD200)
        ),
        AppItem(
            id = "picsart_gold",
            title = "PicsArt editor & Collage GOLD",
            packageName = "com.picsart.studio.gold",
            category = "Photography",
            isGame = false,
            version = "24.1.2",
            size = "82 MB",
            rating = 4.6f,
            downloads = "15.3M",
            developer = "PicsArt Inc (Gold Mods)",
            modFeatures = listOf(
                "Gold Subscription Unlocked",
                "AI Filters and Magic Avatar Generator Bypassed",
                "Ultra-HD 4K Canvas Image Export",
                "Unrestricted Sticker Store Downloads",
                "Completely Ad-Free User Workspace"
            ),
            fullDescription = "Generate stellar photography. Unlock massive aesthetic features including gold sticker collections, neural filters, and precision background removal with immediate render bypass. Start telling deep, stylized stories visually.",
            gradientColors = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121)
        ),
        AppItem(
            id = "duolingo_plus",
            title = "Duolingo: Super Plus",
            packageName = "com.duolingo.superplus",
            category = "Education",
            isGame = false,
            version = "5.150.1",
            size = "50 MB",
            rating = 4.7f,
            downloads = "22.5M",
            developer = "Duolingo (Plus Premium Mods)",
            modFeatures = listOf(
                "Unlimited Hearts Support (Never Lose Progress)",
                "Super Duolingo Theme and UI Skins",
                "Completely Ad-free Lessons & Stories",
                "Progress tests and Skill Checkouts Bypassed",
                "Offline Lessons Storage Cache Unlocked"
            ),
            fullDescription = "Master new vocabulary, grammar rules, and cultural fluency. Super Duolingo Mod is built to make language-learning interactive and frustration-free, giving you unlimited hearts and unrestricted custom tests to fast track your learning.",
            gradientColors = listOf(0xFF58CC02, 0xFF04852F, 0xFF89E219)
        )
    )

    fun getItemById(id: String) = items.find { it.id == id }
}
