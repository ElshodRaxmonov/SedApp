package com.example.sedapp.domain.usecase.bag

import com.example.sedapp.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val repository: OrderRepository
) {
    suspend operator fun invoke(userId: String) = repository.getOrders(userId)
}