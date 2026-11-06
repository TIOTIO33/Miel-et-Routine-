package com.example.data.model

enum class PeriodOfDay(
    val labelFr: String,
    val emoji: String,
    val timeHint: String
) {
    MATIN("Matin", "☀️", "07:00 - 11:30"),
    MIDI("Midi", "🥪", "11:30 - 14:00"),
    SOIR("Soir", "🌙", "17:30 - 20:30"),
    JOURNEE("Toute la journée", "⭐", "À tout moment");

    companion object {
        fun fromString(value: String): PeriodOfDay {
            return try {
                valueOf(value)
            } catch (e: Exception) {
                JOURNEE
            }
        }
    }
}
