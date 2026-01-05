package com.example.sedapp.presentation.dashboard.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SedAppTheme
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.domain.model.Category

@Composable
fun CategoryChip(
    category: Category,
    categoryClicked: (Category) -> Unit
) {
    Card(
        shape = RoundedCornerShape(100, 40, 60, 20),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = WarmWhite),
        modifier = Modifier.height(44.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Use a safe fallback for previews to avoid render errors
            if (LocalInspectionMode.current) {
                Icon(
                    imageVector = Icons.Rounded.Restaurant,
                    contentDescription = category.name,
                    modifier = Modifier.size(25.dp),
                    tint = SedAppOrange
                )
            } else {
                Image(
                    painter = painterResource(getIconForCategory(category.name)),
                    contentDescription = category.name,
                    modifier = Modifier.size(25.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(category.name, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        }
    }
}

@DrawableRes
private fun getIconForCategory(iconName: String): Int {
    return when (iconName.lowercase()) {
        "meal" -> R.drawable.meal
        // You can add your other real drawables here
        // Using a placeholder for now to prevent crashes
        "drink" -> R.drawable.drink
        "bake" -> R.drawable.bake
        "snack" -> R.drawable.snack
        else -> R.drawable.cash
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCategory() {
    SedAppTheme {
        CategoryChip(
            Category(1, "Meal",false) // Pass a string to match the data model{
            , categoryClicked = {}
        )
    }
}
