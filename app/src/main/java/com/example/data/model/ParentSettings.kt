package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parent_settings")
data class ParentSettings(
    @PrimaryKey val id: Int = 1,
    val pinCode: String = "1234",
    val familyName: String = "La Ruche Joyeuse",
    val morningReminderTime: String = "07:30",
    val eveningReminderTime: String = "19:30",
    val notificationsEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val reducedAnimations: Boolean = false,
    val defaultRequireParentValidation: Boolean = false,
    val notifyOnValidation: Boolean = true,
    val requireValidationUnderAge7: Boolean = true
)
