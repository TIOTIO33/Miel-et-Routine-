package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long = 0, // 0 = applicable to all children matching age, or specific child ID
    val title: String,
    val description: String,
    val category: Category,
    val points: Int = 2,
    val period: PeriodOfDay = PeriodOfDay.MATIN,
    val specificTime: String? = null, // e.g. "07:30"
    val daysOfWeek: String = "1,2,3,4,5,6,7", // comma-separated 1=Monday...7=Sunday
    val iconKey: String = "check",
    val requiresParentValidation: Boolean = false,
    val minAge: Int = 3, // Âge minimum recommandé (ex: 3, 5, 8)
    val maxAge: Int = 18, // Âge maximum recommandé (ex: 7, 10, 18)
    val isArchived: Boolean = false
) {
    fun appliesToDay(dayOfWeek: Int): Boolean {
        return daysOfWeek.split(",").mapNotNull { it.trim().toIntOrNull() }.contains(dayOfWeek)
    }

    fun isAppropriateForAge(childAge: Int): Boolean {
        return childAge in minAge..maxAge
    }

    val ageRangeLabel: String
        get() = when {
            minAge <= 3 && maxAge >= 18 -> "Tous âges"
            minAge <= 3 && maxAge <= 5 -> "3-5 ans"
            minAge in 6..7 && maxAge in 7..8 -> "6-8 ans"
            minAge >= 8 && maxAge >= 18 -> "8+ ans"
            minAge >= 9 -> "9+ ans"
            else -> "$minAge-$maxAge ans"
        }
}
