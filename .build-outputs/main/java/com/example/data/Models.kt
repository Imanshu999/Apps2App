package com.example.data

data class AppItem(
    val id: String,
    val title: String,
    val packageName: String,
    val category: String,
    val isGame: Boolean,
    val version: String,
    val size: String,
    val rating: Float,
    val downloads: String,
    val developer: String,
    val modFeatures: List<String>,
    val fullDescription: String,
    val gradientColors: List<Long>, // For beautiful, individual dynamic neon/brand themes in Compose
    val developerNote: String = "Ensured clean signature. Safely checked by Apps2App sandbox engine."
)

object Categories {
    val APPS = listOf(
        "Social & Communication",
        "Music & Audio",
        "Productivity",
        "Photography",
        "Education",
        "Tools"
    )
    val GAMES = listOf(
        "Action",
        "Adventure",
        "Arcade",
        "Strategy",
        "Puzzle",
        "Simulation"
    )
}
