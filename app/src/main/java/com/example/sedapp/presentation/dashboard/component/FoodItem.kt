package com.example.sedapp.presentation.dashboard.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.DeepOrange
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.util.getFormattedPrice
import com.example.sedapp.domain.model.Food

@Preview
@Composable
fun PreviewFoodItem() {
    val food = Food(
        "3",
        "Burger",
        "Delicious burger",
        10.0,
        "https://example.com/burger.jpg",
        true,
        "Drink",
        10,
        4.5,
        "Burger Bistro"
    )
    FoodItem(
        food = food,
        quantity = 0,
        isLiked = false,
        onFoodClicked = {},
        onIncrement = {},
        onDecrement = {},
        onToggleLike = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItem(
    food: Food,
    quantity: Int,
    isLiked: Boolean,
    onFoodClicked: (Food) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onToggleLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onFoodClicked(food) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = WarmWhite
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = food.image,
                    contentDescription = food.name,
                    modifier = Modifier
                        .height(80.dp)
                        .clip(
                            RoundedCornerShape(16.dp)
                        ),
                    contentScale = ContentScale.Crop,

                    placeholder = when (food.category) {
                        "meal" -> painterResource(id = R.drawable.meal)
                        "drink" -> painterResource(id = R.drawable.drink)
                        "bake" -> painterResource(id = R.drawable.bake)
                        "snack" -> painterResource(id = R.drawable.snack)
                        else -> painterResource(id = R.drawable.meal)
                    }
                )

                IconButtonSedApp(
                    icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    onClick = onToggleLike,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    containerColor = Color.White.copy(alpha = 0.7f),
                    iconColor = if (isLiked) Color.Red else Color.Gray,
                    containerSize = 28.dp,
                    iconSize = 18.dp
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = food.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1,
                color = Color.Black
            )
            Text(text = food.restaurant, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp, start = 2.dp, end = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = getFormattedPrice(price = food.price),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                if (quantity == 0) {
                    IconButtonSedApp(
                        icon = Icons.Default.Add,
                        onClick = onIncrement,
                        modifier = Modifier
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonSedApp(
                            icon = Icons.Default.Remove,
                            onClick = onDecrement,
                            modifier = Modifier
                        )
                        Text(
                            text = "$quantity",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        IconButtonSedApp(
                            icon = Icons.Default.Add,
                            onClick = onIncrement,
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun IconButtonSedApp(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerSize: Dp = 20.dp,
    iconSize: Dp = 18.dp,
    containerColor: Color = DeepOrange,
    iconColor: Color = Color.White
) {
    Box(
        modifier = modifier.run {
            size(containerSize)
                .clip(CircleShape)
                .background(containerColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                )
        },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = iconColor
        )
    }
}
