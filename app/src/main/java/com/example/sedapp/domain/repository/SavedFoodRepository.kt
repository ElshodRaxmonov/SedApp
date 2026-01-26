package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.Food
import kotlinx.coroutines.flow.Flow

interface SavedFoodRepository {
    suspend fun insertLikedFood(food: Food)
    suspend fun deleteLikedFood(food: Food)
    fun getAllLikedFoods(): Flow<List<Food>>
    fun isFoodLiked(foodId: String): Flow<Boolean>
    suspend fun deleteAllLikedFoods()
}
