package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "children")
data class ChildProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int,
    val avatarKey: String = "bee_happy", // bee_happy, bee_smart, bee_cool, bee_star
    val beeHat: String = "none", // none, crown, sunglasses, chef, party
    val beeColor: String = "honey_gold", // honey_gold, sage_mint, sky_blue, coral_rose
    val currentPoints: Int = 0,
    val totalPointsEarned: Int = 0,
    val completedMissionsCount: Int = 0,
    val level: Int = 1,
    val requiresParentValidation: Boolean = false
) {
    val levelTitle: String
        get() = when (level) {
            1 -> "Petite Abeille Débutante"
            2 -> "Butineur Curieux"
            3 -> "Abeille des Fleurs"
            4 -> "Champion de l'Alvéole"
            5 -> "Grand Maître du Miel"
            else -> "Super Abeille Dorée 👑"
        }

    val pointsToNextLevel: Int
        get() = (level * 25)
}
