package com.example.sedapp.presentation.dashboard.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        true
    )
    FoodItem(
        food,
        {}
    )
}


@Composable
fun FoodItem(food: Food, onFoodClicked: (Food) -> Unit) {
    var quantity by remember { mutableStateOf(0) }
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(160.dp) // Adjusted height for better visual
    ) {
        // Card is drawn first, appearing in the background
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .clickable { onFoodClicked(food) },
            shape = RoundedCornerShape(24.dp, 24.dp, 16.dp, 16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                // Spacer to push the content down, creating room for the image
                Spacer(Modifier.height(38.dp))
                Text(text = food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = food.description, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "$${food.price}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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

        // Image is drawn last, appearing on top
        Image(
            painter = painterResource(id = R.drawable.meal),
            contentDescription = "Food image",
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopCenter)
        )
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
                    // Custom ripple effect
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
