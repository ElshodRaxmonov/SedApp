package com.example.sedapp.presentation.dashboard.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.util.getFormattedPrice
import com.example.sedapp.domain.model.Currency
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Preview(showBackground = true)
@Composable
fun PreviewCard() {
    val order = Order(
        orderId = "1",
        orderItem = listOf(
            com.example.sedapp.domain.model.OrderedItem(
                itemId = "1",
                food = com.example.sedapp.domain.model.Food(
                    foodId = "1",
                    name = "Pizza",
                    description = "Delicious pizza",
                    price = 10.0,
                    image = "https://example.com/pizza.jpg",
                    isHalal = true,
                    category = "meal",
                    time = 30,
                    rating = 4.5,
                    restaurant = "1"
                ),
                quantity = 2
            )
        ),
        restaurantId = "1",
        status = com.example.sedapp.domain.model.OrderStatus.PENDING,
        createdAt = 123456789,
        userId = "1",
        totalPrice = 10.0,
        orderType = com.example.sedapp.domain.model.OrderType.DELIVERY,
        paymentMethod = com.example.sedapp.domain.model.PaymentMethod.CASH
    )

    OrderCard(
        order = order,
        onReorder = {

        }
    )
}

@Composable
fun OrderCard(
    order: Order,
    onReorder: (Order) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }


    val format = remember(order.createdAt) {
        Instant
            .ofEpochMilli(order.createdAt)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy | HH:mm"))
    }

    // Applying padding to the outer modifier of the Card is key for shadow visibility.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier.weight(2f),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "#${order.orderId.takeLast(6).uppercase()}",
                            fontSize = 16.sp,
                            color = Color.Black,
                            textDecoration = TextDecoration.Underline
                        )

                        VerticalDivider(
                            Modifier
                                .padding(vertical = 4.dp)
                                .height(12.dp),
                            thickness = 0.5.dp,
                            color = Color.LightGray
                        )

                        Text(
                            text = order.status.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (order.status) {
                                OrderStatus.PENDING -> SedAppOrange
                                OrderStatus.CANCELLED -> Color.Red
                                OrderStatus.COMPLETED -> Color.Green
                            }
                        )
                    }
                    Text(
                        text = format,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total:",
                            color = Color.LightGray,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            getFormattedPrice(Currency.RM, order.totalPrice),
                            color = SedAppOrange,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        "${order.orderItem.size} items",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Expanded Content
            if (isExpanded) {
                HorizontalDivider(
                    Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = Color.LightGray
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1.5f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text("Order: ${order.orderType}", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            "Payment: ${order.paymentMethod}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                    Column(
                        modifier = Modifier.weight(2f),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        order.orderItem.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${item.quantity}x ${item.food.name}",
                                    fontSize = 11.sp,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End,
                                    color = Color.Black
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = getFormattedPrice(Currency.RM, item.food.price),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                if (order.status == OrderStatus.COMPLETED) {
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { onReorder(order) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SedAppOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("RE-ORDER", color = Color.White)
                    }
                }
            }
        }
    }
}
