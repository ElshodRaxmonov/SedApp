//package com.example.sedapp.presentation.home.screens
//
//
//import androidx.compose.runtime.Composable
//import androidx.lifecycle.viewmodel.compose.viewModel // Import for getting ViewModel
//import com.example.sedapp.domain.auth.model.Category
//import com.example.sedapp.domain.auth.model.Restaurant
//import com.example.sedapp.presentation.home.HomeViewModel
//
//@Composable
//fun HomeScreen(
//    // Inject the ViewModel
//    viewModel: HomeViewModel = viewModel(),
//    // Navigation callbacks remain the same
//    onCategoryClicked: (Category) -> Unit = {},
//    onRestaurantClicked: (Restaurant) -> Unit = {},
//    onSearchClicked: () -> Unit = {},
//    onProfileClicked: () -> Unit = {}
//) {
//    // 1. Collect the state flow
//    val state by viewModel.uiState.collectAsState()
//
//    Scaffold(
//        bottomBar = { SedAppBottomBar() },
//        containerColor = Color.White
//    ) { paddingValues ->
//        // 2. Handle Loading and Error States
//        if (state.isLoading) {
//            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator(color = PrimaryOrange)
//            }
//        } else if (state.errorMessage != null) {
//            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
//                Text("Error: ${state.errorMessage}", color = Color.Red)
//            }
//        } else {
//            // 3. Render Content (using the fetched data)
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//            ) {
//                // Header and Search Bar (remain the same)
//                item { /* HomeHeader Composable */ }
//                item { /* HomeSearchBar Composable */ }
//
//                // All Categories Section
//                item {
//                    SectionHeader(title = "All Categories", onSeeAllClicked = {})
//                    CategoryList(
//                        categories = state.categories, // <-- Pass live data here
//                        onCategoryClicked = onCategoryClicked,
//                        modifier = Modifier.padding(bottom = 24.dp)
//                    )
//                }
//
//                // Open Restaurants Section
//                item {
//                    SectionHeader(title = "Open Restaurants", onSeeAllClicked = {})
//                }
//                items(state.restaurants) { restaurant -> // <-- Pass live data here
//                    RestaurantCard(
//                        restaurant = restaurant,
//                        onClick = { onRestaurantClicked(restaurant) },
//                        modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//// Note: The rest of the support composables (HomeHeader, HomeSearchBar, RestaurantCard, etc.)
//// from the previous response remain valid.
