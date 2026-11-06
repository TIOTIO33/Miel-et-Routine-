package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val targetCount: Int,
    val badgeType: String // "FIRST_MISSION", "MISSIONS_COUNT", "HELP_COUNT", "WEEK_CHAMPION", "POINTS_GOAL"
)
