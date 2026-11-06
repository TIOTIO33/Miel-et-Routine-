package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Mission
import com.example.data.model.MissionCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions WHERE isArchived = 0 ORDER BY id ASC")
    fun getAllMissions(): Flow<List<Mission>>

    @Query("SELECT * FROM missions WHERE id = :id LIMIT 1")
    suspend fun getMissionById(id: Long): Mission?

    @Query("SELECT * FROM missions WHERE isArchived = 0 AND (childId = 0 OR childId = :childId) ORDER BY period ASC, id ASC")
    fun getMissionsForChild(childId: Long): Flow<List<Mission>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<Mission>)

    @Update
    suspend fun updateMission(mission: Mission)

    @Delete
    suspend fun deleteMission(mission: Mission)

    @Query("DELETE FROM missions WHERE id = :id")
    suspend fun deleteMissionById(id: Long)

    // Completions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: MissionCompletion): Long

    @Query("UPDATE mission_completions SET status = :status, validatedTimestamp = :validatedTimestamp WHERE id = :completionId")
    suspend fun updateCompletionStatus(completionId: Long, status: String, validatedTimestamp: Long?)

    @Query("SELECT * FROM mission_completions WHERE dateStr = :dateStr")
    fun getCompletionsForDate(dateStr: String): Flow<List<MissionCompletion>>

    @Query("SELECT * FROM mission_completions WHERE childId = :childId AND dateStr = :dateStr")
    fun getCompletionsForChildAndDate(childId: Long, dateStr: String): Flow<List<MissionCompletion>>

    @Query("SELECT * FROM mission_completions ORDER BY completedTimestamp DESC")
    fun getAllCompletions(): Flow<List<MissionCompletion>>

    @Query("SELECT * FROM mission_completions WHERE status = 'PENDING_PARENT'")
    fun getPendingValidations(): Flow<List<MissionCompletion>>

    @Query("DELETE FROM mission_completions WHERE missionId = :missionId AND childId = :childId AND dateStr = :dateStr")
    suspend fun deleteCompletion(missionId: Long, childId: Long, dateStr: String)
}
