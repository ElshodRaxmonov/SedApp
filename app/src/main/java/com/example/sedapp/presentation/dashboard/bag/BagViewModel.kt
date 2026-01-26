package com.example.sedapp.presentation.dashboard.bag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.OrderedItem
import com.example.sedapp.domain.repository.AuthRepository
import com.example.sedapp.domain.usecase.bag.ObserveBagItemsUseCase
import com.example.sedapp.domain.usecase.bag.RemoveItemUseCase
import com.example.sedapp.domain.usecase.bag.UpdateItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


data class PickerSelection(
    val selectedValue: String? = null,
    val isVisible: Boolean = false
)

data class BagUiState(
    val items: List<OrderedItem> = emptyList(),
    val orderType: OrderType = OrderType.DELIVERY,
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val deliveryLocation: String? = null,
    val preOrderTime: String? = null,
    val canPlaceOrder: Boolean = false
)

sealed class BagEvent {
    data class NavigateToPayment(val order: Order) : BagEvent()
}

@HiltViewModel
class BagViewModel @Inject constructor(
    private val observeBagItems: ObserveBagItemsUseCase,
    private val updateQuantity: UpdateItemQuantityUseCase,
    private val removeItem: RemoveItemUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BagUiState())
    val uiState: StateFlow<BagUiState> = _uiState.asStateFlow()

    private val _timePickerState = MutableStateFlow(PickerSelection(null, false))
    val timePickerState = _timePickerState.asStateFlow()

    private val _blockPickerState = MutableStateFlow(PickerSelection(null, false))
    val blockPickerState = _blockPickerState.asStateFlow()

    private val _events = MutableSharedFlow<BagEvent>()
    val events: SharedFlow<BagEvent> = _events.asSharedFlow()


    private var selectedHour = 3
    private var selectedMinute = 16
    private var selectedBlock = "LY5"


    init {
        observeItems()
    }

    private fun observeItems() {
        observeBagItems()
            .onEach { items ->
                val total = items.sumOf { it.food.price * it.quantity }
                _uiState.update {
                    it.copy(
                        items = items,
                        totalAmount = total,
                        canPlaceOrder = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun placeOrderEnable() {
        _uiState.update {
            it.copy(canPlaceOrder = true)
        }
    }

    fun onIncrease(itemId: String) {
        viewModelScope.launch {
            updateQuantity(itemId, 1)
        }
    }

    fun onDecrease(itemId: String) {
        viewModelScope.launch {
            updateQuantity(itemId, -1)
        }
    }

    fun onRemove(itemId: String) {
        viewModelScope.launch {
            removeItem(itemId)
        }
    }

    fun onOrderTypeChange(type: OrderType) {
        _uiState.update { it.copy(orderType = type) }
    }

    fun toggleTimePicker(isVisible: Boolean) {
        _timePickerState.update { it.copy(isVisible = isVisible) }
    }

    fun onHourSelected(h: Int) {
        selectedHour = h
    }

    fun onMinuteSelected(m: Int) {
        selectedMinute = m
    }

    fun confirmTimeSelection() {
        _uiState.update {
            it.copy(
                preOrderTime = "$selectedHour:$selectedMinute",
                deliveryLocation = null
            )
        }
        _timePickerState.update {
            it.copy(
                selectedValue = "$selectedHour:$selectedMinute",
                isVisible = false
            )
        }
        placeOrderEnable()
    }

    fun toggleBlockPicker(isVisible: Boolean) {
        _blockPickerState.update { it.copy(isVisible = isVisible) }
    }

    fun onLocationSelected(location: String) {
        selectedBlock = location
    }

    fun confirmBlockSelection() {
        _uiState.update {
            it.copy(
                deliveryLocation = selectedBlock,
                preOrderTime = null
            )
        }
        _blockPickerState.update {
            it.copy(
                selectedValue = selectedBlock,
                isVisible = false
            )
        }
        placeOrderEnable()
    }

    fun onPlaceOrderClicked() {
        val currentState = _uiState.value
        val user = authRepository.getCurrentUser()

        // Use items from the current state to ensure data is present
        if (currentState.items.isEmpty()) return

        val order = Order(
            orderId = UUID.randomUUID().toString(),
            orderItem = currentState.items,
            restaurantId = currentState.items.first().food.restaurant,
            createdAt = System.currentTimeMillis(),
            userId = user?.uid ?: "",
            totalPrice = currentState.totalAmount,
            orderType = currentState.orderType,
            paymentMethod = null
        )

        viewModelScope.launch {
            _events.emit(BagEvent.NavigateToPayment(order))
        }
    }

}
