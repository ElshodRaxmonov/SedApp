package com.example.sedapp.data.repository

import android.util.Log
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderStatus
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.OrderedItem
import com.example.sedapp.domain.model.PaymentMethod
import com.example.sedapp.domain.repository.OrderRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OrderRepository {
    override fun getOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val query = firestore.collection("orders")
            .whereEqualTo("userId", userId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FIRESTORE", "Error: ${error.message}")
                return@addSnapshotListener
            }

            Log.d("FIRESTORE", "Documents found in snapshot: ${snapshot?.size() ?: 0}")

            val orders = snapshot?.documents?.mapNotNull { it.toOrder() } ?: emptyList()
            trySend(orders)
        }
        awaitClose { listener.remove() }
    }
}

private fun DocumentSnapshot.toOrder(): Order? {
    return try {
        // Aligned with PaymentRepositoryImpl which uses "items"
        val itemsData = get("items") as? List<Map<String, Any>> ?: emptyList()

        val orderItems = itemsData.mapNotNull { itemMap ->
            val foodId = itemMap["foodId"] as? String ?: ""
            OrderedItem(
                itemId = foodId,
                food = Food(
                    foodId = foodId,
                    name = itemMap["name"] as? String ?: "Unknown",
                    description = itemMap["description"] as? String ?: "",
                    price = (itemMap["price"] as? Number)?.toDouble() ?: 0.0,
                    image = itemMap["image"] as? String ?: "",
                    restaurant = itemMap["restaurant"] as? String ?: "", // Aligned with "restaurant" key
                    isHalal = itemMap["isHalal"] as? Boolean ?: false,
                    category = itemMap["category"] as? String ?: "",
                    time = (itemMap["time"] as? Number)?.toInt() ?: 0,
                    rating = (itemMap["rating"] as? Number)?.toDouble() ?: 0.0
                ),
                quantity = (itemMap["quantity"] as? Number)?.toInt() ?: 0
            )
        }

        Order(
            orderId = id,
            orderItem = orderItems,
            restaurantId = getString("restaurantId") ?: "",
            status = safeEnum(getString("status"), OrderStatus.PENDING),
            createdAt = getLong("createdAt") ?: 0L,
            userId = getString("userId") ?: "",
            totalPrice = (get("totalPrice") as? Number)?.toDouble() ?: 0.0,
            orderType = safeEnum(getString("orderType"), OrderType.DELIVERY),
            paymentMethod = getString("paymentMethod")?.let {
                safeEnum(it, PaymentMethod.CASH)
            }
        )
    } catch (e: Exception) {
        Log.e("MAPPER_ERROR", "Error parsing Order document $id: ${e.message}", e)
        null
    }
}

private inline fun <reified T : Enum<T>> safeEnum(value: String?, default: T): T {
    return try {
        if (value == null) default else enumValueOf<T>(value.uppercase())
    } catch (e: Exception) {
        default
    }
}
