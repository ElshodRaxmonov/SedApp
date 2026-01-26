package com.example.sedapp.presentation.dashboard.bag.payment


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sedapp.R
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.PayMethod
import com.example.sedapp.domain.model.PaymentMethod
import com.example.sedapp.domain.usecase.bag.ClearBagUseCase
import com.example.sedapp.domain.usecase.bag.PayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PaymentUiState(
    val selectedMethod: PaymentMethod = PaymentMethod.TOUCH_N_GO,
    val isProcessing: Boolean = false,
    val totalAmount: Double = 0.0,
    val paymentSuccess: Boolean = false,
    var paymentMethods: List<PayMethod> = emptyList()
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val pay: PayUseCase,
    private val clearBag: ClearBagUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    private var order: Order? = null

    init {
        // Observe order from SavedStateHandle to handle cases where it's set after ViewModel initialization
        savedStateHandle.getStateFlow<Order?>("order", null)
            .onEach { ord ->
                Log.d("TAG", "data come: ${ord?.totalPrice}")
                if (ord != null) {
                    order = ord
                    _uiState.update { it.copy(totalAmount = ord.totalPrice) }
                    listPaymentMethods(ord.orderType)
                }
            }.launchIn(viewModelScope)
    }

    fun setOrder(order: Order) {
        this.order = order
        _uiState.update {
            it.copy(totalAmount = order.totalPrice)
        }
        listPaymentMethods(order.orderType)
    }

    fun listPaymentMethods(orderType: OrderType) {
        val methods = if (orderType == OrderType.DELIVERY) {
            listOf(
                PayMethod(
                    type = PaymentMethod.VISA,
                    label = "VISA",
                    iconRes = R.drawable.visa
                ),
                PayMethod(
                    type = PaymentMethod.MASTER_CARD,
                    label = "Master Card",
                    iconRes = R.drawable.mastercard
                ),
                PayMethod(
                    type = PaymentMethod.TOUCH_N_GO,
                    label = "Touch & Go",
                    iconRes = R.drawable.toucngo
                )
            )
        } else {
            listOf(
                PayMethod(
                    type = PaymentMethod.VISA,
                    label = "VISA",
                    iconRes = R.drawable.visa
                ),
                PayMethod(
                    type = PaymentMethod.MASTER_CARD,
                    label = "Master Card",
                    iconRes = R.drawable.mastercard
                ),
                PayMethod(
                    type = PaymentMethod.TOUCH_N_GO,
                    label = "Touch & Go",
                    iconRes = R.drawable.toucngo
                ),
                PayMethod(
                    type = PaymentMethod.CASH,
                    label = "Cash",
                    iconRes = R.drawable.cash
                )
            )
        }
        _uiState.update { it.copy(paymentMethods = methods) }
    }

    fun selectMethod(method: PaymentMethod) {
        _uiState.update { it.copy(selectedMethod = method) }
        order?.paymentMethod = method
    }

    fun onPayClicked() {
        val currentOrder = order ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true) }

            val result = pay(
                order = currentOrder
            )

            if (result) {
                clearBag()
                _uiState.update { it.copy(isProcessing = false, paymentSuccess = true) }
            } else {
                _uiState.update { it.copy(isProcessing = false) }
                // Handle error if needed
            }
        }
    }
}
