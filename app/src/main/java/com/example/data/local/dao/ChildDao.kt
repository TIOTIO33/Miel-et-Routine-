package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChildProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {
    @Query("SELECT * FROM children ORDER BY id ASC")
    fun getAllChildren(): Flow<List<ChildProfile>>

    @Query("SELECT * FROM children WHERE id = :id LIMIT 1")
    fun getChildById(id: Long): Flow<ChildProfile?>

    @Query("SELECT * FROM children WHERE id = :id LIMIT 1")
    suspend fun getChildByIdOnce(id: Long): ChildProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: ChildProfile): Long

    @Update
    suspend fun updateChild(child: ChildProfile)

    @Delete
    suspend fun deleteChild(child: ChildProfile)

    @Query("UPDATE children SET currentPoints = currentPoints + :points, totalPointsEarned = totalPointsEarned + :points WHERE id = :childId")
    suspend fun addPoints(childId: Long, points: Int)

    @Query("UPDATE children SET currentPoints = MAX(0, currentPoints - :points) WHERE id = :childId")
    suspend fun deductPoints(childId: Long, points: Int)

    @Query("UPDATE children SET completedMissionsCount = completedMissionsCount + 1 WHERE id = :childId")
    suspend fun incrementCompletedCount(childId: Long)

    @Query("UPDATE children SET beeHat = :hat, beeColor = :color WHERE id = :childId")
    suspend fun updateBeeCustomization(childId: Long, hat: String, color: String)

    @Query("UPDATE children SET level = :level WHERE id = :childId")
    suspend fun updateLevel(childId: Long, level: Int)
}
