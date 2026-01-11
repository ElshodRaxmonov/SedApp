package com.example.sedapp.presentation.dashboard.bag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.OrderedItem
import com.example.sedapp.domain.usecase.bag.ObserveBagItemsUseCase
import com.example.sedapp.domain.usecase.bag.RemoveItemUseCase
import com.example.sedapp.domain.usecase.bag.UpdateItemQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class BagUiState(
    val items: List<OrderedItem> = emptyList(),
    val orderType: OrderType = OrderType.DELIVERY,
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = false,
    val deliveryLocation: String? = null,
    val preOrderTime: String? = null,
    val canPlaceOrder: Boolean = false
)

@HiltViewModel
class BagViewModel @Inject constructor(
    private val observeBagItems: ObserveBagItemsUseCase,
    private val updateQuantity: UpdateItemQuantityUseCase,
    private val removeItem: RemoveItemUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BagUiState())
    val uiState: StateFlow<BagUiState> = _uiState.asStateFlow()

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
                        canPlaceOrder = items.isNotEmpty()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onIncrease(itemId: String) {
        viewModelScope.launch {
            updateQuantity(itemId, +1)
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

    fun onLocationSelected(location: String) {
        _uiState.update { it.copy(deliveryLocation = location) }
    }

    fun onPreOrderTimeSelected(time: String) {
        _uiState.update { it.copy(preOrderTime = time) }
    }
}
