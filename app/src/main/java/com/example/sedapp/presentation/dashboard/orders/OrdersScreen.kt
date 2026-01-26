package com.example.sedapp.presentation.dashboard.orders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderStatus
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.OrderedItem
import com.example.sedapp.presentation.dashboard.component.OrderCard
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar
import kotlinx.coroutines.flow.collectLatest


@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    val state = OrdersUiState(
        orders = listOf(
            Order(
                orderId = "1",
                orderItem = listOf(
                    OrderedItem(
                        itemId = "i1", food = Food(
                            foodId = "f1",
                            name = "Margherita Pizza",
                            description = "Classic tomato and mozzarella",
                            price = 12.0,
                            image = "",
                            isHalal = true,
                            category = "Pizza",
                            time = 20,
                            rating = 4.5,
                            restaurant = "Pizza Hut"
                        ), quantity = 2
                    )
                ),
                restaurantId = "r1",
                status = OrderStatus.COMPLETED,
                createdAt = System.currentTimeMillis(),
                userId = "u1",
                totalPrice = 24.0,
                orderType = OrderType.DELIVERY
            ), Order(
                orderId = "2",
                orderItem = listOf(
                    OrderedItem(
                        itemId = "i2", food = Food(
                            foodId = "f2",
                            name = "Chicken Burger",
                            description = "Crispy chicken with lettuce",
                            price = 8.0,
                            image = "",
                            isHalal = true,
                            category = "Burger",
                            time = 15,
                            rating = 4.2,
                            restaurant = "KFC"
                        ), quantity = 1
                    )
                ),
                restaurantId = "r2",
                status = OrderStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                userId = "u1",
                totalPrice = 8.0,
                orderType = OrderType.PRE_ORDER
            )
        ), isLoading = false, error = null
    )
    OrdersScreenContent(
        state = state,
        onReorder = {},
        onSortSelected = {},
        onStatusSelected = {},
        scrollState = rememberLazyListState()
    )
}

@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel(),
    state: LazyListState = rememberLazyListState(),
    onNavigateToPayment: (Order) -> Unit = {}
) {
    val uiState by viewModel.ordersState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is OrdersEvent.NavigateToPayment -> onNavigateToPayment(event.order)
            }
        }
    }

    OrdersScreenContent(
        state = uiState,
        onReorder = viewModel::reorder,
        onSortSelected = viewModel::onSortSelected,
        onStatusSelected = viewModel::onStatusSelected,
        modifier = modifier,
        scrollState = state
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreenContent(
    state: OrdersUiState,
    onReorder: (Order) -> Unit,
    onSortSelected: (OrderSort) -> Unit,
    onStatusSelected: (OrderStatus?) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState
) {
    Scaffold(
        topBar = {
            SedAppTopBar(title = "My Orders")
        },
        containerColor = Charcoal
    ) { paddingValues ->

        Column(modifier = modifier.padding(paddingValues)) {
            
            OrderFilters(
                selectedSort = state.selectedSort,
                onSortSelected = onSortSelected,
                selectedStatus = state.selectedStatus,
                onStatusSelected = onStatusSelected
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.error != null) {
                    Text(
                        text = state.error,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                } else if (state.orders.isEmpty()) {
                    Text(
                        text = "No orders found",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.orders) { order ->
                            OrderCard(
                                order = order, onReorder = { onReorder(order) })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFilters(
    selectedSort: OrderSort,
    onSortSelected: (OrderSort) -> Unit,
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Sort Row
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Sort by:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftGold,
                modifier = Modifier.padding(end = 8.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    OrderFilterChip(
                        selected = selectedSort == OrderSort.TIME,
                        onClick = { onSortSelected(OrderSort.TIME) },
                        label = "Ordered Time"
                    )
                }
                item {
                    OrderFilterChip(
                        selected = selectedSort == OrderSort.PRICE,
                        onClick = { onSortSelected(OrderSort.PRICE) },
                        label = "Price"
                    )
                }
            }
        }

        // Status Row
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Status:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SoftGold,
                modifier = Modifier.padding(end = 8.dp)
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(OrderStatus.entries) { status ->
                    OrderFilterChip(
                        selected = selectedStatus == status,
                        onClick = { onStatusSelected(status) },
                        label = status.name.lowercase().replaceFirstChar { it.uppercase() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = 11.sp) },
        shape = RoundedCornerShape(100.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SedAppOrange,
            selectedLabelColor = Color.White,
            containerColor = Color(0xFFF0F2F5),
            labelColor = Color.Gray
        ),
        border = null,
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color.White
                )
            }
        } else null
    )
}
