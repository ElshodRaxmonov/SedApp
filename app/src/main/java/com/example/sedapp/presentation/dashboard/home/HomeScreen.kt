package com.example.sedapp.presentation.dashboard.home


import android.app.Activity
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SedAppTheme
import com.example.sedapp.core.ui.theme.SedAppYellow
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.ui.theme.White
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Restaurant

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSearchClicked: () -> Unit,
    onRestaurantClicked: (String) -> Unit,
    onAllCategoriesClicked: () -> Unit,
    onAllRestaurantsClicked: () -> Unit,
    onCategoryClicked: (Category) -> Unit,
    onSavedFoodsClicked: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        state = state,
        onSearchClicked = onSearchClicked,
        onRestaurantClicked = onRestaurantClicked,
        onAllCategoriesClicked = onAllCategoriesClicked,
        onAllRestaurantsClicked = onAllRestaurantsClicked,
        onCategoryClicked = onCategoryClicked,
        onSavedFoodsClicked = onSavedFoodsClicked,
        scrollState = scrollState
    )
}


@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onSearchClicked: () -> Unit,
    onRestaurantClicked: (String) -> Unit,
    onAllCategoriesClicked: () -> Unit,
    onAllRestaurantsClicked: () -> Unit,
    onCategoryClicked: (Category) -> Unit = {},
    onSavedFoodsClicked: () -> Unit = {},
    scrollState: LazyListState = rememberLazyListState()
) {
    // Set Status Bar Color to match Header (Orange) or Background (Charcoal)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Change this to SedAppOrange if you want it to match the header exactly
            window.statusBarColor = Charcoal.toArgb()
            WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = false
        }
    }

    Scaffold(
        containerColor = Charcoal,
        topBar = {
            HomeHeader(
                greeting = state.greeting,
                onSavedFoodsClicked = onSavedFoodsClicked
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                HomeSearchBar(onClick = onSearchClicked)
            }

            // --- Categories ---
            item {
                Column {
                    SectionHeader(title = "All Categories", onSeeAllClicked = onAllCategoriesClicked)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.isLoading && state.categories.isEmpty()) {
                        CircularProgressIndicator(color = SedAppOrange, modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.categories) { category ->
                                CategoryChip(category = category, categoryClicked = onCategoryClicked)
                            }
                        }
                    }
                }
            }

            // --- Restaurants ---
            item {
                SectionHeader(title = "Open Restaurants", onSeeAllClicked = onAllRestaurantsClicked)
            }

            if (state.isLoading && state.restaurants.isEmpty()) {
                item {
                    CircularProgressIndicator(
                        color = SedAppOrange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .size(24.dp)
                    )
                }
            } else {
                // FIXED: Use items() directly in the main LazyColumn
                items(state.restaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = { onRestaurantClicked(restaurant.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader(
    greeting: String,
    onSavedFoodsClicked: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SedAppOrange,
            contentColor = Color.Black,
            disabledContainerColor = White,
            disabledContentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(24.dp, 24.dp, 64.dp, 24.dp),
        modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(com.example.sedapp.R.drawable.sedapp_logo),
                    contentDescription = "Logo",
                    tint = SedAppYellow,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        "SedApp",
                        color = SedAppYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text("Set your table up", fontSize = 12.sp, color = Color.White)
                }
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { onSavedFoodsClicked() },
                    colors = IconButtonDefaults
                        .iconButtonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                        )

                ) {
                    Icon(
                        painter = painterResource(com.example.sedapp.R.drawable.saved_foods),
                        contentDescription = "Remarked",
                        modifier = Modifier.padding(8.dp),
                        tint = WarmWhite
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = greeting,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAllClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SoftGold)
        TextButton(onClick = onSeeAllClicked) {
            Text("See All", color = SedAppOrange)
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = SedAppOrange,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCard() {
    RestaurantCard(
        restaurant = Restaurant(
            restaurantId = "1",
            name = "Rose Garden",
            cuisine = "Chinese • RM RM",
            rating = 4.7,
            images = emptyList(),
            location = "123 Main St",
            description = "This is a description"
        ),
        onClick = {

        }
    )
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = WarmWhite),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            AsyncImage(
                model = restaurant.images.firstOrNull(),
                placeholder = ColorPainter(Color.LightGray),
                contentDescription = restaurant.name,
                error = ColorPainter(Color.LightGray),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(
                        RoundedCornerShape(24.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        restaurant.name, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                        color = Color.Black,
                        maxLines = 1,
                        letterSpacing = 0.1.em
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        restaurant.cuisine.replace("$", "RM"),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = restaurant.rating.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.Black
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
        color = SoftGold,
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
                cuisine = "Chinese • RM RM",
                rating = 4.7,
                images = emptyList(),
                location = "123 Main St",
                description = "This is a description"
            ),
            Restaurant(
                restaurantId = "2",
                name = "Burger King",
                cuisine = "Fastfood • RM",
                rating = 4.2,
                images = emptyList(),
                location = "456 Broad St",
                description = "dsssssssssssssssssss"
            )
        )
        val state = HomeUiState(
            categories = categories,
            restaurants = restaurants,
            greeting = "Good Morning!"
        )

        HomeScreenContent(
            state = state,
            onSearchClicked = {},
            onRestaurantClicked = {},
            onAllCategoriesClicked = {},
            onAllRestaurantsClicked = {},
            onSavedFoodsClicked = {}
        )
    }
}
