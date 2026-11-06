package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mission_completions",
    indices = [
        Index(value = ["missionId", "childId", "dateStr"], unique = true)
    ]
)
data class MissionCompletion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val missionId: Long,
    val childId: Long,
    val dateStr: String, // Format: YYYY-MM-DD
    val completedTimestamp: Long = System.currentTimeMillis(),
    val status: String, // "COMPLETED_AUTO", "PENDING_PARENT", "APPROVED", "REJECTED"
    val validatedTimestamp: Long? = null
) {
    val isEffectivelyCompleted: Boolean
        get() = status == "COMPLETED_AUTO" || status == "APPROVED"
}
