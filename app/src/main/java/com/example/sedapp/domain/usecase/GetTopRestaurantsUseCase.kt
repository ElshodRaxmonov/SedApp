package com.example.sedapp.domain.usecase

import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetTopRestaurantsUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(): List<Restaurant> {
        return repository.getTopRestaurants()
    }
}