package com.example.sedapp.data.repository

import com.example.sedapp.data.local.dao.FoodDao
import com.example.sedapp.data.local.entity.toEntity
import com.example.sedapp.data.local.entity.toFood
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.repository.SavedFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavedFoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao
) : SavedFoodRepository {

    override suspend fun insertLikedFood(food: Food) {
        foodDao.insertLikedFood(food.toEntity())
    }

    override suspend fun deleteLikedFood(food: Food) {
        foodDao.deleteLikedFood(food.toEntity())
    }

    override fun getAllLikedFoods(): Flow<List<Food>> {
        return foodDao.getAllLikedFoods().map { entities ->
            entities.map { it.toFood() }
        }
    }

    override fun isFoodLiked(foodId: String): Flow<Boolean> {
        return foodDao.isFoodLiked(foodId)
    }
    override suspend fun deleteAllLikedFoods() {
        foodDao.deleteAllLikedFoods()
    }
}
