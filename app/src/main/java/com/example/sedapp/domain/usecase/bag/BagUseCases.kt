package com.example.sedapp.domain.usecase.bag

import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.OrderedItem
import com.example.sedapp.domain.repository.BagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBagItemsUseCase @Inject constructor(
    private val repository: BagRepository
) {
    operator fun invoke(): Flow<List<OrderedItem>> = repository.observeBagItems()
}

class AddItemToBagUseCase @Inject constructor(
    private val repository: BagRepository
) {
    suspend operator fun invoke(food: Food, quantity: Int) {
        repository.addItem(food, quantity)
    }
}

class UpdateItemQuantityUseCase @Inject constructor(
    private val repository: BagRepository
) {
    suspend operator fun invoke(foodId: String, newQuantity: Int) {
        repository.updateQuantity(foodId, newQuantity)
    }

}

class RemoveItemUseCase @Inject constructor(
    private val repository: BagRepository
) {
    suspend operator fun invoke(foodId: String) {
        repository.removeItem(foodId)
    }
}
