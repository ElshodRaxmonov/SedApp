package com.example.sedapp.domain.model

data class User(
    val uid: String?,
    val name: String?,
    val email: String
)

data class Category(
    val categoryId: Int,
    val name: String,
    var isCategorySelected: Boolean
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
    val available: Boolean,
)

data class OrderedItem(
    val food: Food,
    val quantity: Int
)

data class Order(
    val orderItem: List<OrderedItem>,
    val restaurantId: String,
    val status: String,
    val createdAt: Long,
    val userId: String
)
