package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.Restaurant

interface RestaurantRepository {
    suspend fun getTopRestaurants(): List<Restaurant>

}