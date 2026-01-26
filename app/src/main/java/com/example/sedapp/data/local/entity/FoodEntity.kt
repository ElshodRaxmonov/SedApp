package com.example.sedapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sedapp.domain.model.Food

@Entity(tableName = "liked_foods")
data class FoodEntity(
    @PrimaryKey val foodId: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val isHalal: Boolean,
    val category: String,
    val time: Int,
    val rating: Double,
    val restaurant: String
)

fun FoodEntity.toFood() = Food(
    foodId = foodId,
    name = name,
    description = description,
    price = price,
    image = image,
    isHalal = isHalal,
    category = category,
    time = time,
    rating = rating,
    restaurant = restaurant
)

fun Food.toEntity() = FoodEntity(
    foodId = foodId,
    name = name,
    description = description,
    price = price,
    image = image,
    isHalal = isHalal,
    category = category,
    time = time,
    rating = rating,
    restaurant = restaurant
)
