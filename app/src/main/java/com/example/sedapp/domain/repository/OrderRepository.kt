package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(userId: String): Flow<List<Order>>

}