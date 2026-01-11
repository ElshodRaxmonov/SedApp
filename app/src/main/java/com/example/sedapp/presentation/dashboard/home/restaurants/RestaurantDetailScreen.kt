package com.example.sedapp.presentation.dashboard.home.restaurants

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.domain.model.Food
import com.example.sedapp.domain.model.Restaurant
import com.example.sedapp.presentation.dashboard.component.FoodItem
import com.example.sedapp.presentation.dashboard.home.foods.FoodDetailsSheet
import com.example.sedapp.presentation.dashboard.home.foods.FoodViewModel
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun RestaurantDetailsScreenPreview() {

    val state = RestaurantDetailsUiState(
        isLoading = false,
        errorMessage = null,
        restaurant = Restaurant(
            "Restaurant 1",
            "Location 1",
            4.5,
            "Astana",
            cuisine = "Central Asian",
            listOf("Image URL 1", "Image URL 2"),
            description = "cdscsdcsddddddddddddddddddddddddddddddddddddddddd"
        ),
        foods = listOf(
            Food(
                "dscscs",
                "Burger",
                "Delicious burger",
                price = 12.0,
                image = "https://example.com/burger.jpg",
                isHalal = true,
                category = "meal",
                time = 12,
                rating = 3.8,
                restaurant = "Astana"
            ),
            Food(
                "dscscs",
                "Burger",
                "Delicious burger",
                price = 12.0,
                image = "https://example.com/burger.jpg",
                isHalal = true,
                category = "meal",
                time = 12,
                rating = 3.8,
                restaurant = "Astana"
            )
        ),
    )

    RestaurantDetailsScreenContent(
        state = state,
        onBagClicked = {},
        onBackClicked = {},
        onAddToBag = { _, _ -> }
    )
}

@Composable
fun RestaurantDetailsScreen(
    viewModel: RestaurantViewModel = hiltViewModel(),
    foodViewModel: FoodViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onBagClicked: () -> Unit
) {

    val state by viewModel.detailsState.collectAsStateWithLifecycle()

    RestaurantDetailsScreenContent(
        state = state,
        onBackClicked = onBackClicked,
        onBagClicked = onBagClicked,
        onAddToBag = foodViewModel::addToBag
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailsScreenContent(
    state: RestaurantDetailsUiState,
    onBagClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onAddToBag: (Food, Int) -> Unit
) {
    val restaurant = state.restaurant
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedFood by remember { mutableStateOf<Food?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(restaurant?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onBagClicked) {
                        Image(
                            painter = painterResource(id = R.drawable.bag),
                            contentDescription = "Bag",
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (restaurant == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Restaurant not found")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .height(260.dp)
                        .fillMaxWidth()
                ) {
                    val items = restaurant.images
                    HorizontalMultiBrowseCarousel(
                        state = rememberCarouselState { items.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 16.dp),
                        preferredItemWidth = 186.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) { i ->
                        AsyncImage(
                            modifier = Modifier
                                .height(205.dp)
                                .maskClip(MaterialTheme.shapes.extraLarge),
                            model = items[i],
                            placeholder = painterResource(R.drawable.cuisine),
                            error = painterResource(R.drawable.cuisine),
                            contentDescription = "Restaurant Image"
                        )
                    }
                }
            }

            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(R.drawable.rating_star), null, tint = SedAppOrange)
                        Text(" ${restaurant.rating}", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(16.dp))
                        Icon(painter = painterResource(R.drawable.cuisine), null, tint = SedAppOrange, modifier = Modifier.size(16.dp))
                        Text(" ${restaurant.cuisine}")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(R.drawable.location), null, tint = SedAppOrange, modifier = Modifier.size(16.dp))
                        Text(" ${restaurant.location}", fontWeight = FontWeight.Bold)
                    }
                    Text(restaurant.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(restaurant.description, color = Color.Gray, fontSize = 14.sp)
                    Spacer(Modifier.height(20.dp))
                }
            }

            val groupedFoods = state.foods.groupBy { it.category }
            groupedFoods.forEach { (category, foodsInCategory) ->
                item {
                    Text(
                        category,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(foodsInCategory.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { food ->
                            Box(Modifier.weight(1f)) {
                                FoodItem(food = food, onFoodClicked = {
                                    selectedFood = it
                                    scope.launch { sheetState.show() }
                                })
                            }
                        }
                        if (rowItems.size < 2) Spacer(Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (selectedFood != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedFood = null },
                sheetState = sheetState
            ) {
                FoodDetailsSheet(
                    food = selectedFood!!,
                    onAddToCart = { qty ->
                        onAddToBag(selectedFood!!, qty)
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            selectedFood = null
                        }
                    }
                )
            }
        }
    }
}
