package com.example.sedapp.domain.usecase.home.food

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.repository.SavedFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ToggleLikeFoodUseCase @Inject constructor(
    private val repository: SavedFoodRepository
) {
    suspend operator fun invoke(food: Food, isCurrentlyLiked: Boolean) {
        if (isCurrentlyLiked) {
            repository.deleteLikedFood(food)
        } else {
            repository.insertLikedFood(food)
        }
    }
}

class GetLikedFoodsUseCase @Inject constructor(
    private val repository: SavedFoodRepository
) {
    operator fun invoke(): Flow<List<Food>> {
        return repository.getAllLikedFoods()
    }
}

class IsFoodLikedUseCase @Inject constructor(
    private val repository: SavedFoodRepository
) {
    operator fun invoke(foodId: String): Flow<Boolean> {
        return repository.isFoodLiked(foodId)
    }
}

class DeleteAllLikedFoodsUseCase @Inject constructor(
    private val repository: SavedFoodRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllLikedFoods()
    }
}

