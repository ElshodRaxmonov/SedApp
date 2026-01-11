package com.example.sedapp.data.repository

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderedItem
import com.example.sedapp.domain.repository.BagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BagRepositoryImpl @Inject constructor() : BagRepository {
    
    private val _bagItems = MutableStateFlow<List<OrderedItem>>(emptyList())

    override fun observeBagItems(): Flow<List<OrderedItem>> = _bagItems.asStateFlow()

    override suspend fun addItem(food: Food, quantity: Int) {
        _bagItems.update { currentItems ->
            val existingItem = currentItems.find { it.food.foodId == food.foodId }
            if (existingItem != null) {
                currentItems.map { 
                    if (it.food.foodId == food.foodId) it.copy(quantity = it.quantity + quantity) else it 
                }
            } else {
                currentItems + OrderedItem(
                    itemId = food.foodId,
                    food = food,
                    quantity = quantity
                )
            }
        }
    }

    override suspend fun updateQuantity(foodId: String, newQuantity: Int) {
        _bagItems.update { currentItems ->
            currentItems.map { 
                if (it.food.foodId == foodId) it.copy(quantity = newQuantity) else it 
            }
        }
    }

    override suspend fun removeItem(foodId: String) {
        _bagItems.update { currentItems ->
            currentItems.filter { it.food.foodId != foodId }
        }
    }

    override suspend fun clearBag() {
        _bagItems.value = emptyList()
    }

    override suspend fun placeOrder(order: Order) {
        // Logic to send order to Firebase
        clearBag()
    }
}
