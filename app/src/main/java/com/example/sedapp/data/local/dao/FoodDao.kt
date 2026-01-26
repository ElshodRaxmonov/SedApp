package com.example.sedapp.data.local.dao

import androidx.room.*
import com.example.sedapp.data.local.entity.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedFood(food: FoodEntity)

    @Delete
    suspend fun deleteLikedFood(food: FoodEntity)

    @Query("SELECT * FROM liked_foods")
    fun getAllLikedFoods(): Flow<List<FoodEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM liked_foods WHERE foodId = :foodId)")
    fun isFoodLiked(foodId: String): Flow<Boolean>

    @Query("DELETE FROM liked_foods")
    suspend fun deleteAllLikedFoods()
}
