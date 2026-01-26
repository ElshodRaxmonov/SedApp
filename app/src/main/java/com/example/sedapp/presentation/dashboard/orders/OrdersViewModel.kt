package com.example.sedapp.presentation.dashboard.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderStatus
import com.example.sedapp.domain.repository.AuthRepository
import com.example.sedapp.domain.usecase.bag.GetOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class OrderSort {
    TIME, PRICE
}

data class OrdersUiState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedSort: OrderSort = OrderSort.TIME,
    val selectedStatus: OrderStatus? = null
)

sealed class OrdersEvent {
    data class NavigateToPayment(val order: Order) : OrdersEvent()
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _rawOrders = MutableStateFlow<List<Order>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _selectedSort = MutableStateFlow(OrderSort.TIME)
    private val _selectedStatus = MutableStateFlow<OrderStatus?>(null)

    private val _events = MutableSharedFlow<OrdersEvent>()
    val events: SharedFlow<OrdersEvent> = _events.asSharedFlow()

    val ordersState: StateFlow<OrdersUiState> = combine(
        _rawOrders,
        _isLoading,
        _error,
        _selectedSort,
        _selectedStatus
    ) { orders, loading, error, sort, status ->
        
        val filtered = if (status != null) {
            orders.filter { it.status == status }
        } else {
            orders
        }

        val sorted = when (sort) {
            OrderSort.TIME -> filtered.sortedByDescending { it.createdAt }
            OrderSort.PRICE -> filtered.sortedByDescending { it.totalPrice }
        }

        OrdersUiState(
            orders = sorted,
            isLoading = loading,
            error = error,
            selectedSort = sort,
            selectedStatus = status
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OrdersUiState(isLoading = true))

    init {
        loadOrders()
    }

    private fun loadOrders() {
        val user = auth.getCurrentUser()
        if (user?.uid == null) {
            _error.value = "User not logged in"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                getOrdersUseCase(user.uid).collect { orders ->
                    _rawOrders.value = orders
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun onSortSelected(sort: OrderSort) {
        _selectedSort.value = sort
    }

    fun onStatusSelected(status: OrderStatus?) {
        _selectedStatus.value = if (_selectedStatus.value == status) null else status
    }

    fun reorder(order: Order) {
        val newOrder = order.copy(
            orderId = UUID.randomUUID().toString(),
            createdAt = System.currentTimeMillis(),
            status = OrderStatus.PENDING,
            paymentMethod = null // User needs to select payment method again
        )
        viewModelScope.launch {
            _events.emit(OrdersEvent.NavigateToPayment(newOrder))
        }
    }
}
