package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderedItem
import kotlinx.coroutines.flow.Flow

interface BagRepository {
    fun observeBagItems(): Flow<List<OrderedItem>>
    suspend fun addItem(food: Food, quantity: Int)
    suspend fun updateQuantity(foodId: String, newQuantity: Int)
    suspend fun removeItem(foodId: String)
    suspend fun clearBag()
    suspend fun placeOrder(order: Order)

}
