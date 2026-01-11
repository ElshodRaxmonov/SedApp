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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
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
        ), listOf(/*...foods...*/)
    )
    FoodScreenContent(
        uiState = uiStateMock,
        onBackClicked = {},
        onCategorySelected = {},
        onHalalFilterChanged = {},
        onAddToBag = { _, _ -> })
}


@Composable
fun FoodScreen(
    onBackClicked: () -> Unit, viewModel: FoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    FoodScreenContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onCategorySelected = viewModel::onCategorySelected,
        onHalalFilterChanged = viewModel::onHalalFilterChanged,
        onAddToBag = viewModel::addToBag
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreenContent(
    uiState: FoodUiState,
    onBackClicked: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onHalalFilterChanged: (Boolean) -> Unit,
    onAddToBag: (Food, Int) -> Unit
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
            })
        }
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
                    CategoriesSection(
                        categories = uiState.availableCategories,
                        selectedCategories = uiState.selectedCategoryNames,
                        onCategorySelected = onCategorySelected,
                        isHalalOnly = uiState.isHalalOnly,
                        onHalalFilterChanged = onHalalFilterChanged
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                }

                val groupedFoods = uiState.displayedFoods.groupBy { it.food.category }
                groupedFoods.forEach { (category, foodsInCategory) ->
                    item {
                        Text(
                            category,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(foodsInCategory.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            rowItems.forEach { itemState ->
                                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    FoodItem(food = itemState.food, onFoodClicked = {
                                        selectedFood = it
                                        scope.launch { sheetState.show() }
                                    })
                                }
                            }
                            if (rowItems.size < 2) {
                                Spacer(Modifier.weight(1f))
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

@Composable
fun CategoriesSection(
    categories: List<Category>,
    selectedCategories: Set<String>,
    onCategorySelected: (String) -> Unit,
    isHalalOnly: Boolean,
    onHalalFilterChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = category.name in selectedCategories
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category.name) },
                    label = { Text(category.name) },
                    leadingIcon = if (isSelected) {
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
            checked = isHalalOnly,
            onCheckedChange = onHalalFilterChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = WarmWhite,
                checkedTrackColor = SedAppOrange,
                uncheckedThumbColor = WarmWhite,
                uncheckedTrackColor = SoftGold
            ),
            thumbContent = {
                Icon(
                    painter = painterResource(
                        if (isHalalOnly) R.drawable.halal else R.drawable.not_halal
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            })
    }
}

@Composable
fun FoodDetailsSheet(
    food: Food,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(18.dp))
        ) {
            AsyncImage(
                model = food.image,
                contentDescription = food.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = food.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Icon(
                        painter = painterResource(R.drawable.restaurant_icon),
                        contentDescription = "Restaurant Icon",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        text = food.restaurant,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rating_star),
                    contentDescription = "rating",
                    tint = SedAppOrange
                )
                Text(
                    text = food.rating.toString(),
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.halal_or_not),
                    contentDescription = "Halal",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = if (food.isHalal) {
                        "Halal"
                    } else {
                        "Not Halal"
                    }, modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ready_time),
                    contentDescription = "time",
                    tint = SedAppOrange
                )
                Text(text = "${food.time} min", modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = food.description,
                fontSize = 14.sp,
                color = Color(0xFFA0A5BA),
                fontFamily = FontFamily.SansSerif
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp, 16.dp))
                .background(
                    WarmWhite
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$${food.price}",
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
                            Icon(
                                Icons.Default.Remove,
                                contentDescription = "Remove",
                                tint = Color.White
                            )
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
                    onClick = { onAddToCart(quantity) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SedAppOrange,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "ADD TO CART", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
