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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.sedapp.core.ui.theme.SedAppOrange
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
        onFoodClicked = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodItem(
    food: Food, onFoodClicked: (Food) -> Unit
) {
    var quantity by remember { mutableStateOf(0) }

    // Card is drawn first, appearing in the background
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onFoodClicked(food) },
        shape = RoundedCornerShape(16.dp ),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = food.image,
                contentDescription = food.name,
                modifier = Modifier.height(80.dp).clip(
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

            // Spacer to push the content down, creating room for the image
            Spacer(Modifier.height(8.dp))
            Text(text = food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = food.restaurant, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "RM${food.price}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (quantity == 0) {
                    IconButtonSedApp(
                        icon = Icons.Default.Add,
                        onClick = { quantity++ },
                        modifier = Modifier
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButtonSedApp(
                            icon = Icons.Default.Remove,
                            onClick = { quantity-- },
                            modifier = Modifier
                        )
                        Text(
                            text = "$quantity",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                        IconButtonSedApp(
                            icon = Icons.Default.Add,
                            onClick = { quantity++ },
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
    containerSize: Dp = 24.dp,
    iconSize: Dp = 20.dp,
    containerColor: Color = SedAppOrange,
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
