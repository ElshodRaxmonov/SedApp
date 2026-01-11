package com.example.sedapp.domain.usecase.home.restaurant

import com.example.sedapp.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRestaurantFoodsUseCase @Inject constructor(private val repository: RestaurantRepository) {
    suspend operator fun invoke(name: String) = repository.getRestaurantFoods(name)
}