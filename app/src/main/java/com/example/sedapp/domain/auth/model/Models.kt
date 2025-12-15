package com.example.sedapp.domain.auth.model

data class User(
    val uid: String?,
    val name: String?,
    val email: String
)

data class Category(
    val categoryId: String,
    val name: String,
    val iconUrl: String
)

data class Restaurant(
    val restaurantId: String,
    val location: String,
    val rating: Double,
    val name: String,
    val cuisine: String,
    val images: List<String>
)

data class Food(
    val foodId: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val isHalal: Boolean,
    val category: String,
    val available: Boolean
)

data class OrderedItem(
    val food: Food,
    val quantity: Int
    )


