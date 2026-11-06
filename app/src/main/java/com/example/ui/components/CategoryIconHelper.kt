package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.Category

object CategoryIconHelper {
    fun getIconForCategory(category: Category): ImageVector {
        return when (category) {
            Category.AUTONOMIE -> Icons.Default.Star
            Category.HYGIENE -> Icons.Default.Bathtub
            Category.HABILLEMENT -> Icons.Default.Checkroom
            Category.ECOLE -> Icons.Default.School
            Category.DEVOIRS -> Icons.Default.MenuBook
            Category.RANGEMENT -> Icons.Default.Inventory2
            Category.BIENVEILLANCE -> Icons.Default.Favorite
            Category.SOMMEIL -> Icons.Default.Bedtime
            Category.REPAS -> Icons.Default.Restaurant
            Category.RESPONSABILITES -> Icons.Default.Psychology
            Category.VIE_QUOTIDIENNE -> Icons.Default.WbSunny
        }
    }

    fun getIconByKey(key: String): ImageVector {
        return when (key) {
            "bed" -> Icons.Default.Bed
            "checkroom" -> Icons.Default.Checkroom
            "shower", "wash" -> Icons.Default.Bathtub
            "school" -> Icons.Default.School
            "water_drop" -> Icons.Default.WaterDrop
            "home" -> Icons.Default.Home
            "menu_book" -> Icons.Default.MenuBook
            "inventory_2" -> Icons.Default.Inventory2
            "favorite" -> Icons.Default.Favorite
            "bedtime" -> Icons.Default.Bedtime
            "restaurant" -> Icons.Default.Restaurant
            "cleaning_services" -> Icons.Default.CleaningServices
            "park" -> Icons.Default.Park
            "sports_esports" -> Icons.Default.SportsEsports
            else -> Icons.Default.Star
        }
    }

    fun getEmojiForCategory(category: Category): String {
        return when (category) {
            Category.AUTONOMIE -> "⭐"
            Category.HYGIENE -> "🧼"
            Category.HABILLEMENT -> "👕"
            Category.ECOLE -> "🎒"
            Category.DEVOIRS -> "✏️"
            Category.RANGEMENT -> "🧸"
            Category.BIENVEILLANCE -> "💖"
            Category.SOMMEIL -> "🌙"
            Category.REPAS -> "🍽️"
            Category.RESPONSABILITES -> "🌱"
            Category.VIE_QUOTIDIENNE -> "☀️"
        }
    }
}
