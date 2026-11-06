package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "child_badge_unlocks",
    indices = [
        Index(value = ["childId", "badgeKey"], unique = true)
    ]
)
data class ChildBadgeUnlock(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val badgeKey: String,
    val unlockedTimestamp: Long = System.currentTimeMillis()
)
