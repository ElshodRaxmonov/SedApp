package com.example.sedapp.domain.usecase

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.repository.FoodRepository
import javax.inject.Inject

class GetFoodsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend operator fun invoke(): List<Food> = foodRepository.getFoods()
}
