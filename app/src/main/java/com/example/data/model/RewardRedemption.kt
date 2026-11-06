package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_redemptions")
data class RewardRedemption(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rewardId: Long,
    val rewardTitle: String,
    val childId: Long,
    val childName: String,
    val costPoints: Int,
    val requestedTimestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "APPROVED", "DELIVERED", "REJECTED"
    val resolvedTimestamp: Long? = null
)
