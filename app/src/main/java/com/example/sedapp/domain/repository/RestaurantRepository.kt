package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant

interface RestaurantRepository {
    suspend fun getTopRestaurants(): List<Restaurant>

    suspend fun getRestaurantFoods(restaurantName: String): List<Food>
    suspend fun getRestaurantDetails(restaurantName: String): Restaurant
    suspend fun getCuisines(): Set<String>
    suspend fun getSearchedFoods(query: String): List<Food>
    suspend fun getSearchedRestaurants(query: String): List<Restaurant>
}




