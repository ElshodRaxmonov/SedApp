package com.example.sedapp.domain.usecase.bag

import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.repository.PaymentRepository
import javax.inject.Inject

class PayUseCase @Inject constructor(
    private val repository: PaymentRepository
) {

    suspend operator fun invoke(order: Order): Boolean {
        return repository.executePayment(order)
    }

}