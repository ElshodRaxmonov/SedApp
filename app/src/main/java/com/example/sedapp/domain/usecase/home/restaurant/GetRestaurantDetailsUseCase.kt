package com.example.sedapp.domain.usecase.home.restaurant

import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRestaurantDetailsUseCase @Inject constructor(private val restaurantRepository: RestaurantRepository) {
    suspend operator fun invoke(restaurantName: String) =
        restaurantRepository.getRestaurantDetails(restaurantName)
}
