package com.example.data

import androidx.room.TypeConverter
import com.example.model.ClassType

class Converters {
    @TypeConverter
    fun fromClassType(value: ClassType): String = value.name

    @TypeConverter
    fun toClassType(value: String): ClassType = try {
        ClassType.valueOf(value)
    } catch (e: Exception) {
        ClassType.WARRIOR
    }
}
