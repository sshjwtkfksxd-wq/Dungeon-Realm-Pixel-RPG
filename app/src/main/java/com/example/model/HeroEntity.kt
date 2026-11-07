package com.example.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing the Player Hero stats, level, experience, and class.
 */
@Entity(tableName = "player_hero")
data class HeroEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "hero_name") val name: String,
    @ColumnInfo(name = "current_class") val currentClass: ClassType,
    @ColumnInfo(name = "level") val level: Int = 1,
    @ColumnInfo(name = "experience") val experience: Int = 0,
    @ColumnInfo(name = "max_experience") val maxExperience: Int = 100,
    @ColumnInfo(name = "current_hp") val hp: Int = 150,
    @ColumnInfo(name = "max_hp") val maxHp: Int = 150,
    @ColumnInfo(name = "current_mp") val mp: Int = 40,
    @ColumnInfo(name = "max_mp") val maxMp: Int = 40,
    @ColumnInfo(name = "strength") val strength: Int = 15,
    @ColumnInfo(name = "magic") val magic: Int = 5,
    @ColumnInfo(name = "agility") val agility: Int = 8,
    @ColumnInfo(name = "vitality") val vitality: Int = 12,
    @ColumnInfo(name = "unallocated_stats") val unallocatedStats: Int = 0,
    @ColumnInfo(name = "gold") val gold: Int = 300,
    @ColumnInfo(name = "gems") val gems: Int = 50,
    @ColumnInfo(name = "max_floor_unlocked") val maxFloorUnlocked: Int = 1
)
