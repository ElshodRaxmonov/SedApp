package com.example.sedapp.domain.model

import android.os.Parcelable
import com.example.sedapp.presentation.dashboard.orders.OrdersScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val uid: String?,
    val name: String?,
    val email: String
) : Parcelable

@Parcelize
data class Category(
    val categoryId: Int,
    val name: String,
    var isCategorySelected: Boolean
) : Parcelable

@Parcelize
data class Restaurant(
    val restaurantId: String,
    val location: String,
    val rating: Double,
    val name: String,
    val cuisine: String,
    val images: List<String>,
    val description: String
) : Parcelable

@Parcelize
data class Food(
    val foodId: String,
    val name: String,
    val description: String,
    val price: Double,
    val image: String,
    val isHalal: Boolean,
    val category: String,
    val time: Int,
    val rating: Double,
    val restaurant: String
) : Parcelable

@Parcelize
data class OrderedItem(
    val itemId: String,
    val food: Food,
    val quantity: Int
) : Parcelable

@Parcelize
data class Order(
    val orderId: String,
    val orderItem: List<OrderedItem>,
    val restaurantId: String,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Long,
    val userId: String,
    val totalPrice: Double,
    val orderType: OrderType,
    var paymentMethod: PaymentMethod? = null

) : Parcelable

@Parcelize
enum class OrderType : Parcelable {
    DELIVERY,
    PRE_ORDER
}

@Parcelize
enum class PaymentMethod : Parcelable {
    CASH,
    VISA,
    MASTER_CARD,
    TOUCH_N_GO
}

data class PayMethod(
    val type: PaymentMethod,
    val label: String,
    val iconRes: Int
)


enum class OrderStatus {
    PENDING,
    CANCELLED,
    COMPLETED
}

enum class Currency {
    RM,
    USD
}

