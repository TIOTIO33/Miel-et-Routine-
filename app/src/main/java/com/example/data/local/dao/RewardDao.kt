package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Reward
import com.example.data.model.RewardRedemption
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards WHERE isActive = 1 ORDER BY costPoints ASC")
    fun getActiveRewards(): Flow<List<Reward>>

    @Query("SELECT * FROM rewards ORDER BY costPoints ASC")
    fun getAllRewards(): Flow<List<Reward>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: Reward): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewards(rewards: List<Reward>)

    @Update
    suspend fun updateReward(reward: Reward)

    @Delete
    suspend fun deleteReward(reward: Reward)

    // Redemptions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedemption(redemption: RewardRedemption): Long

    @Query("UPDATE reward_redemptions SET status = :status, resolvedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateRedemptionStatus(id: Long, status: String, timestamp: Long)

    @Query("SELECT * FROM reward_redemptions ORDER BY requestedTimestamp DESC")
    fun getAllRedemptions(): Flow<List<RewardRedemption>>

    @Query("SELECT * FROM reward_redemptions WHERE childId = :childId ORDER BY requestedTimestamp DESC")
    fun getRedemptionsForChild(childId: Long): Flow<List<RewardRedemption>>

    @Query("SELECT * FROM reward_redemptions WHERE status = 'PENDING' ORDER BY requestedTimestamp DESC")
    fun getPendingRedemptions(): Flow<List<RewardRedemption>>

    @Query("SELECT * FROM reward_redemptions WHERE id = :id LIMIT 1")
    suspend fun getRedemptionById(id: Long): RewardRedemption?
}
