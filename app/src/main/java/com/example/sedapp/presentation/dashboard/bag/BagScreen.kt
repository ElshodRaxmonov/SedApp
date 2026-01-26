package com.example.sedapp.presentation.dashboard.bag

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.buttonElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.DeepOrange
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.ui.theme.White
import com.example.sedapp.core.util.getFormattedPrice
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.OrderedItem
import com.example.sedapp.presentation.dashboard.component.PickerDropUp
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar
import com.example.sedapp.presentation.dashboard.component.WheelPicker
import com.example.sedapp.presentation.dashboard.home.search.SearchAnimationContent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BagScreen(
    viewModel: BagViewModel = hiltViewModel(),
    onNavigateToPayment: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val timePickerState by viewModel.timePickerState.collectAsStateWithLifecycle()
    val blockPickerState by viewModel.blockPickerState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is BagEvent.NavigateToPayment -> onNavigateToPayment(event.order)
            }
        }
    }

    Scaffold(
        topBar = {
            SedAppTopBar(title = "My Bag")
        },
        containerColor = Charcoal,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            if (state.items.isEmpty()) {
                EmptyBagView()
            } else {
                BagContent(
                    state = state,
                    onIncrease = viewModel::onIncrease,
                    onDecrease = viewModel::onDecrease,
                    onRemove = viewModel::onRemove,
                    onOrderTypeChange = viewModel::onOrderTypeChange,
                    onLocationClick = { viewModel.toggleBlockPicker(true) },
                    onTimeClick = { viewModel.toggleTimePicker(true) },
                    onPlaceOrder = viewModel::onPlaceOrderClicked
                )
            }

            // Pickers
            if (timePickerState.isVisible) {
                PickerDropUp(
                    title = "After how much time",
                    onDismiss = { viewModel.toggleTimePicker(false) }
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            WheelPicker(
                                items = (1..12).toList(),
                                initialIndex = 2,
                                onItemSelected = { h ->
                                    viewModel.onHourSelected(h)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                ":",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            WheelPicker(
                                items = (0..59).map { it.toString().padStart(2, '0') },
                                initialIndex = 15,
                                onItemSelected = { m ->
                                    viewModel.onMinuteSelected(m.toInt())
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.confirmTimeSelection()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            elevation = buttonElevation(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SedAppOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }

            if (blockPickerState.isVisible) {
                PickerDropUp(
                    title = "Select Building",
                    onDismiss = { viewModel.toggleBlockPicker(false) },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    ) {
                        WheelPicker(
                            items = listOf("LY1", "LY2", "LY3", "LY4", "LY5", "LY6"),
                            initialIndex = 4,
                            onItemSelected = { location ->
                                viewModel.onLocationSelected(location)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                viewModel.confirmBlockSelection()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            elevation = buttonElevation(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SedAppOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BagContent(
    state: BagUiState,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit,
    onOrderTypeChange: (OrderType) -> Unit,
    onLocationClick: () -> Unit,
    onTimeClick: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.items, key = { it.itemId }) { item ->
                BagItemRow(
                    item = item,
                    onIncrease = onIncrease,
                    onDecrease = onDecrease,
                    onRemove = onRemove
                )
            }
        }

        OrderSummaryPanel(
            state = state,
            onOrderTypeChange = onOrderTypeChange,
            onLocationClick = onLocationClick,
            onTimeClick = onTimeClick,
            onPlaceOrder = onPlaceOrder
        )
    }
}

@Composable
fun BagItemRow(
    item: OrderedItem,
    onIncrease: (String) -> Unit,
    onDecrease: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.food.image,
            contentDescription = item.food.name,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.meal)
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.food.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(
                    onClick = { onRemove(item.itemId) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color(0xFFE57373),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = getFormattedPrice(price = item.food.price),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = item.food.restaurant, color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.DarkGray, CircleShape)
                            .clickable {
                                if (item.quantity > 1)
                                    onDecrease(item.itemId) else
                                    onRemove(item.itemId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${item.quantity}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.DarkGray, CircleShape)
                            .clickable { onIncrease(item.itemId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummaryPanel(
    state: BagUiState,
    onOrderTypeChange: (OrderType) -> Unit,
    onLocationClick: () -> Unit,
    onTimeClick: () -> Unit,
    onPlaceOrder: () -> Unit
) {
    Surface(
        color = SoftGold,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 16.dp, start = 16.dp, bottom = 120.dp),
        shadowElevation = 16.dp,
        tonalElevation = 16.dp
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
        ) {
            // Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.clickable { onOrderTypeChange(OrderType.DELIVERY) },
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        "DELIVERY",
                        color = if (state.orderType == OrderType.DELIVERY) DeepOrange else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (state.orderType == OrderType.DELIVERY) {
                        Box(
                            Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(DeepOrange)
                        )
                    }
                }

                Column(
                    modifier = Modifier.clickable { onOrderTypeChange(OrderType.PRE_ORDER) },
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "PRE ORDER",
                        color = if (state.orderType == OrderType.PRE_ORDER) DeepOrange else Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                    if (state.orderType == OrderType.PRE_ORDER) {
                        Box(
                            Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(DeepOrange)
                        )
                    }
                }
            }
            Text(
                text = "You can choose either delivery or pre-order",
                color = Color.Gray,
                fontSize = 8.sp
            )
            Spacer(Modifier.height(4.dp))

            // Clickable summary areas trigger pickers via callbacks
            Surface(
                color = WarmWhite,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.orderType == OrderType.DELIVERY)
                            (state.deliveryLocation ?: "Select the building...")
                        else (state.preOrderTime ?: "Ready for 2 hours"),
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            if (state.orderType == OrderType.DELIVERY) onLocationClick()
                            else onTimeClick()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (state.orderType == OrderType.DELIVERY) R.drawable.location else R.drawable.time_add
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black
                        )
                    }
                }
            }

            if (state.orderType == OrderType.DELIVERY && state.deliveryLocation == null) {
                Text(
                    "The location is unknown !",
                    color = Color.Red,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }
            if (state.orderType == OrderType.PRE_ORDER && state.preOrderTime == null) {
                Text(
                    "The time is unknown !",
                    color = Color.Red,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TOTAL: ", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        getFormattedPrice(price = state.totalAmount),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Breakdown ", color = DeepOrange, fontSize = 14.sp)
                    Icon(
                        painter = painterResource(id = R.drawable.cuisine),
                        contentDescription = null,
                        tint = SedAppOrange,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onPlaceOrder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SedAppOrange),
                shape = RoundedCornerShape(12.dp),
                enabled = true
            ) {
                Text(
                    "PLACE ORDER",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = White
                )
            }
        }
    }
}

@Composable
fun EmptyBagView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SearchAnimationContent(
            animationRes = "raw/not_found_animation.json",
            title = "Your bag is empty",
            subtitle = "Add some items to your bag"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPanel() {
    OrderSummaryPanel(
        state = BagUiState(),
        onOrderTypeChange = {},
        onLocationClick = {},
        onTimeClick = {},
        onPlaceOrder = {}
    )
}
