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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.OrderedItem

@Composable
fun BagScreen(
    viewModel: BagViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color(0xFF1E1E1E) // Dark background like in design
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
                    onOrderTypeChange = viewModel::onOrderTypeChange
                )
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
    onOrderTypeChange: (OrderType) -> Unit
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
            onOrderTypeChange = onOrderTypeChange
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
                text = "$${item.food.price}",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "14\"", color = Color.Gray, fontSize = 12.sp) // Example variation
                Spacer(Modifier.weight(1f))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.DarkGray, CircleShape)
                            .clickable { onDecrease(item.itemId) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Remove, null, tint = Color.White, modifier = Modifier.size(16.dp))
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
                        Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderSummaryPanel(
    state: BagUiState,
    onOrderTypeChange: (OrderType) -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .navigationBarsPadding()
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
                        color = if (state.orderType == OrderType.DELIVERY) SedAppOrange else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (state.orderType == OrderType.DELIVERY) {
                        Box(Modifier.width(40.dp).height(2.dp).background(SedAppOrange))
                    }
                }

                Column(
                    modifier = Modifier.clickable { onOrderTypeChange(OrderType.PRE_ORDER) },
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        "PRE ORDER",
                        color = if (state.orderType == OrderType.PRE_ORDER) SedAppOrange else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (state.orderType == OrderType.PRE_ORDER) {
                        Box(Modifier.width(40.dp).height(2.dp).background(SedAppOrange))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Dynamic Input based on selection
            Surface(
                color = Color(0xFFF0F5FA),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.orderType == OrderType.DELIVERY) 
                            (state.deliveryLocation ?: "Select the building...") 
                            else "Ready for 2 hours",
                        color = Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        painter = painterResource(
                            id = if (state.orderType == OrderType.DELIVERY) R.drawable.location else R.drawable.cuisine
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Black
                    )
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

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("TOTAL: ", color = Color.Gray, fontSize = 14.sp)
                    Text("$${state.totalAmount}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Breakdown ", color = SedAppOrange, fontSize = 14.sp)
                    Icon(
                        painter = painterResource(id = R.drawable.cuisine), // Replace with Chevron icon
                        contentDescription = null,
                        tint = SedAppOrange,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { /* Handle Order */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SedAppOrange),
                shape = RoundedCornerShape(12.dp),
                enabled = state.canPlaceOrder
            ) {
                Text("PLACE ORDER", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
        Text(
            text = "Bag Is Empty",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}
