package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.Badge
import com.example.data.model.ChildBadgeUnlock
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY id ASC")
    fun getAllBadges(): Flow<List<Badge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<Badge>)

    @Query("SELECT * FROM child_badge_unlocks WHERE childId = :childId")
    fun getUnlockedBadgesForChild(childId: Long): Flow<List<ChildBadgeUnlock>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockBadge(unlock: ChildBadgeUnlock): Long

    @Query("SELECT COUNT(*) FROM child_badge_unlocks WHERE childId = :childId AND badgeKey = :badgeKey")
    suspend fun hasBadge(childId: Long, badgeKey: String): Int
}
