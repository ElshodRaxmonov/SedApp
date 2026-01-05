package com.example.sedapp.presentation.dashboard.home


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SedAppTheme
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.ui.theme.White
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Restaurant

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(), // Helper needed or mock it
    onSearchClicked: () -> Unit,
    onRestaurantClicked: (String) -> Unit,
    onAllCategoriesClicked: () -> Unit,
    onAllRestaurantsClicked: () -> Unit,
    onCategoryClicked: (Category) -> Unit,
    modifier: Modifier
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        state = state,
        onSearchClicked = onSearchClicked,
        onRestaurantClicked = onRestaurantClicked,
        onAllCategoriesClicked = onAllCategoriesClicked,
        onAllRestaurantsClicked = onAllRestaurantsClicked,
        onCategoryClicked = onCategoryClicked

    )
}

@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onSearchClicked: () -> Unit,
    onRestaurantClicked: (String) -> Unit,
    onAllCategoriesClicked: () -> Unit,
    onAllRestaurantsClicked: () -> Unit,
    onCategoryClicked: (Category) -> Unit = {}
) {
    Scaffold(
        containerColor = White,
        topBar = {
            HomeHeader(
                userName = state.userName,
                greeting = state.greeting,

                )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- Search Bar ---
            item {
                HomeSearchBar(onClick = onSearchClicked)
            }

            // --- Categories ---
            item {
                Column {
                    SectionHeader(
                        title = "All Categories",
                        onSeeAllClicked = onAllCategoriesClicked
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.categories) { category ->
                            CategoryChip(category) {
                                onCategoryClicked(category)
                            }
                        }
                    }
                }
            }

            // --- Restaurants ---
            item {
                SectionHeader(title = "Open Restaurants", onSeeAllClicked = onAllRestaurantsClicked)
            }

            if (state.isLoading) {
                items(3) { // Show 3 shimmer placeholders
//                    ShimmerRestaurantCardPlaceholder() // You would need to create this composable
                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else if (state.restaurants.isEmpty()) {
                item {
                    Text("No restaurants available right now.", modifier = Modifier.padding(16.dp))
                }
            } else {
                items(state.restaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClicked(restaurant.restaurantId) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    userName: String?,
    greeting: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Using Icon instead of R.drawable to ensure Preview works
            Icon(
                painter = painterResource(com.example.sedapp.R.drawable.sedapp_logo),
                contentDescription = "Logo",
                tint = SedAppOrange,
                modifier = Modifier.size(54.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text("SedApp", color = SedAppOrange, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Set your table up", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.weight(1f))
            IconButton(
                modifier = Modifier.background(Color.Black),
                shape = CircleShape,
                onClick = {}

            ) {
                Icon(
                    painter = painterResource(com.example.sedapp.R.drawable.saved_foods),
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    tint = WarmWhite
                )
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Hey $userName, $greeting",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        TextButton(onClick = onSeeAllClicked) {
            Text("See All", color = SedAppOrange)
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = SedAppOrange,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun RestaurantCard(restaurant: Restaurant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            AsyncImage(
                model = restaurant.images.firstOrNull(),
                placeholder = ColorPainter(Color.LightGray),
                contentDescription = restaurant.name,
                error = ColorPainter(Color.LightGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(restaurant.cuisine, color = Color.Gray, fontSize = 14.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFFFF8E1), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = restaurant.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


@Composable
fun HomeSearchBar(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF0F5FA),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(12.dp))
            Text("Search dishes, restaurants...", color = Color.Gray)
        }
    }
}

// ==========================================
// PREVIEW
// ==========================================
@Preview(showBackground = true, name = "Home Screen Preview")
@Composable
fun PreviewHomeScreen() {
    SedAppTheme {
        val categories = listOf(
            Category(1, "Meal", false),
            Category(2, "Drink", false),
            Category(3, "Bake", false),
            Category(4, "Snack", false)
        )
        val restaurants = listOf(
            Restaurant(
                restaurantId = "1",
                name = "Rose Garden",
                cuisine = "Chinese • $$",
                rating = 4.7,
                images = emptyList(), // Empty list is fine, AsyncImage handles it
                location = "123 Main St"
            ),
            Restaurant(
                restaurantId = "2",
                name = "Burger King",
                cuisine = "Fastfood • $",
                rating = 4.2,
                images = emptyList(),
                location = "456 Broad St"
            )
        )
        val state = HomeUiState(
            categories = categories,
            restaurants = restaurants,
            userName = "Elshod",
            greeting = "Good Morning!"
        )

        HomeScreenContent(
            state = state,
            onSearchClicked = {},
            onRestaurantClicked = {},
            onAllCategoriesClicked = {},
            onAllRestaurantsClicked = {}
        )
    }
}
