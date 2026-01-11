package com.example.sedapp.domain.usecase.home

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.repository.RestaurantRepository
import javax.inject.Inject

class RestaurantSearchUseCase @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) {
    suspend operator fun invoke(query: String): List<Restaurant> =
        restaurantRepository.getSearchedRestaurants(query)
}