package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.BadgeDao
import com.example.data.local.dao.ChildDao
import com.example.data.local.dao.MissionDao
import com.example.data.local.dao.ParentSettingsDao
import com.example.data.local.dao.RewardDao
import com.example.data.model.Badge
import com.example.data.model.ChildBadgeUnlock
import com.example.data.model.ChildProfile
import com.example.data.model.Mission
import com.example.data.model.MissionCompletion
import com.example.data.model.ParentSettings
import com.example.data.model.Reward
import com.example.data.model.RewardRedemption

@Database(
    entities = [
        ChildProfile::class,
        Mission::class,
        MissionCompletion::class,
        Reward::class,
        RewardRedemption::class,
        Badge::class,
        ChildBadgeUnlock::class,
        ParentSettings::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun missionDao(): MissionDao
    abstract fun rewardDao(): RewardDao
    abstract fun badgeDao(): BadgeDao
    abstract fun parentSettingsDao(): ParentSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miel_routines_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
