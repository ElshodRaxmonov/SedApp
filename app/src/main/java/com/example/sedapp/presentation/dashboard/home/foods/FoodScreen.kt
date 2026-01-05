package com.example.sedapp.presentation.dashboard.home.foods

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.SoftGold
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.domain.model.Category
import com.example.sedapp.domain.model.Food
import com.example.sedapp.presentation.dashboard.component.FoodItem
import kotlinx.coroutines.launch


@Preview
@Composable
fun FoodScreenPreview() {

    val uiStateMock = FoodUiState(
        false, listOf(
            Category(1, "Meal", false),
            Category(2, "Drink", false),
            Category(3, "Bake", false),
            Category(4, "Snack", false)
        ), listOf(
            Food(
                "1",
                "Burger",
                "Delicious burger",
                10.0,
                "https://example.com/burger.jpg",
                true,
                "Meal",
                true
            ), Food(
                "2",
                "Burger",
                "Delicious burger",
                10.0,
                "https://example.com/burger.jpg",
                true,
                "Meal",
                true
            ), Food(
                "3",
                "Burger",
                "Delicious burger",
                10.0,
                "https://example.com/burger.jpg",
                true,
                "Drink",
                true
            ),Food(
                "2",
                "Burger",
                "Delicious burger",
                10.0,
                "https://example.com/burger.jpg",
                true,
                "Meal",
                true
            ),Food(
                "2",
                "Burger",
                "Delicious burger",
                10.0,
                "https://example.com/burger.jpg",
                true,
                "Meal",
                true
            ),Food(
                "2",
                "Burger",
                "Delicious burger",
                10.0,
                "https://example.com/burger.jpg",
                true,
                "Meal",
                true
            )
        )
    )
    FoodScreenContent(uiState = uiStateMock, onBackClicked = {})
}


@Composable
fun FoodScreen(
    onBackClicked: () -> Unit, viewModel: FoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    FoodScreenContent(
        uiState = uiState, onBackClicked = onBackClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreenContent(
    uiState: FoodUiState, onBackClicked: () -> Unit
) {
    var selectedFood by remember { mutableStateOf<Food?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Foods") }, navigationIcon = {
                IconButton(onClick = onBackClicked) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }, actions = {
                Image(
                    painter = painterResource(id = R.drawable.sedapp_logo_grey),
                    contentDescription = "SedApp icon",
                    modifier = Modifier.padding(end = 16.dp)
                )
            },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp))

        },

        ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = uiState.errorMessage)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Text(
                        "Categories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CategoriesSection(categories = uiState.categories, onCategorySelected = {})
                    Spacer(modifier = Modifier.height(24.dp))
                }

                val groupedFoods = uiState.foods.groupBy { it.category }
                groupedFoods.forEach { (category, foodsInCategory) ->
                    item {
                        Text(
                            category,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            items(foodsInCategory.chunked(2)) { columnItems ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    columnItems.forEach { food ->
                                        FoodItem(food = food, onFoodClicked = {
                                            selectedFood = it
                                            scope.launch { sheetState.show() }
                                        })
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }

        if (selectedFood != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedFood = null }, sheetState = sheetState
            ) {
                FoodDetailsSheet(food = selectedFood!!)
            }
        }
    }
}

@Composable
fun CategoriesSection(categories: List<Category>, onCategorySelected: (Category) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var checked by remember { mutableStateOf(true) }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->

                FilterChip(
                    selected = category.isCategorySelected,
                    onClick = {
                        category.isCategorySelected = !category.isCategorySelected
                    },
                    label = { Text(category.name) },
                    leadingIcon = if (category.isCategorySelected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    })
            }
        }

        Switch(
            checked = checked, onCheckedChange = { checked = it }, colors = SwitchDefaults.colors(
                checkedThumbColor = WarmWhite,
                checkedTrackColor = SedAppOrange,
                uncheckedThumbColor = WarmWhite,
                uncheckedTrackColor = SoftGold
            ), thumbContent = {
                Icon(
                    painter = painterResource(
                        if (checked) R.drawable.halal else R.drawable.not_halal
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            })
    }
}



@Composable
fun FoodDetailsSheet(food: Food) {
    var quantity by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color.Red)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Burger Bistro",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "rating"
            )
            Text(text = "4.7", modifier = Modifier.padding(start = 4.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = R.drawable.sedapp_logo),
                contentDescription = "Halal",
                modifier = Modifier.size(24.dp)
            )
            Text(text = "Halal", modifier = Modifier.padding(start = 4.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "time"
            )
            Text(text = "20 min", modifier = Modifier.padding(start = 4.dp))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "Astana", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Maecenas sed diam eget risus varius blandit sit amet non magna. Integer posuere erat a ante venenatis dapibus posuere velit aliquet.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$${food.price}", // Using the food object's price
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        Color.DarkGray, RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = { if (quantity > 1) quantity-- }) {
                    Icon(Icons.Default.Remove, contentDescription = "Remove", tint = Color.White)
                }
                Text(
                    text = "$quantity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = Color.White
                )
                IconButton(onClick = { quantity++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: Add to cart logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "ADD TO CART", style = MaterialTheme.typography.titleMedium)
        }
    }
}