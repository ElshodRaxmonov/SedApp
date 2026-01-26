package com.example.sedapp.data.repository

import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.repository.PaymentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PaymentRepository {

    override suspend fun executePayment(
        order: Order
    ): Boolean {
        return try {
            // Mapping the domain Order model to a Firestore-friendly hierarchy
            // Saving Enums as strings for easier retrieval and Firestore compatibility
            val orderData = mapOf(
                "userId" to order.userId,
                "totalPrice" to order.totalPrice,
                "status" to order.status.name,
                "createdAt" to order.createdAt,
                "orderType" to order.orderType.name,
                "paymentMethod" to order.paymentMethod?.name,
                "items" to order.orderItem.map { item ->
                    mapOf(
                        "foodId" to item.food.foodId,
                        "name" to item.food.name,
                        "price" to item.food.price,
                        "quantity" to item.quantity,
                        "restaurant" to item.food.restaurant,
                        "image" to item.food.image
                    )
                }
            )

            // Uploading to the "orders" collection
            firestore.collection("orders")
                .add(orderData)
                .await()

            true
        } catch (e: Exception) {
            false
        }
    }
}
