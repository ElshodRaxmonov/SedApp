package com.example.sedapp.domain.repository

import com.example.sedapp.domain.model.Order

interface PaymentRepository {
    suspend fun executePayment(order: Order): Boolean
}