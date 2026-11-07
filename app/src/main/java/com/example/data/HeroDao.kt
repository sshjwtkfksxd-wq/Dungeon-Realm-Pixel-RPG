package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.HeroEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HeroDao {
    @Query("SELECT * FROM player_hero WHERE id = :id LIMIT 1")
    fun getHeroFlow(id: Int = 1): Flow<HeroEntity?>

    @Query("SELECT * FROM player_hero WHERE id = :id LIMIT 1")
    suspend fun getHeroDirect(id: Int = 1): HeroEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHero(hero: HeroEntity)

    @Query("DELETE FROM player_hero")
    suspend fun clearHero()
}
