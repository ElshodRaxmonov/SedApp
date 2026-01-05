package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.Food

interface FoodRepository {
    suspend fun getFoods(): List<Food>
}
