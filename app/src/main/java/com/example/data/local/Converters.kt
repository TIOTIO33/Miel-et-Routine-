package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.Category
import com.example.data.model.PeriodOfDay

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String {
        return category.name
    }

    @TypeConverter
    fun toCategory(value: String): Category {
        return Category.fromString(value)
    }

    @TypeConverter
    fun fromPeriodOfDay(period: PeriodOfDay): String {
        return period.name
    }

    @TypeConverter
    fun toPeriodOfDay(value: String): PeriodOfDay {
        return PeriodOfDay.fromString(value)
    }
}
