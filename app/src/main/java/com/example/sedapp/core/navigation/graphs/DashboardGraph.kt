package com.example.sedapp.core.navigation.graphs

import DashboardScaffold
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.sedapp.core.navigation.Routes
import com.example.sedapp.presentation.dashboard.bag.BagScreen
import com.example.sedapp.presentation.dashboard.home.HomeScreen
import com.example.sedapp.presentation.dashboard.home.HomeViewModel
import com.example.sedapp.presentation.dashboard.home.foods.FoodScreen
import com.example.sedapp.presentation.dashboard.home.restaurants.RestaurantDetailsScreen
import com.example.sedapp.presentation.dashboard.home.restaurants.RestaurantScreen
import com.example.sedapp.presentation.dashboard.orders.OrdersScreen
import com.example.sedapp.presentation.dashboard.profile.ProfileScreen
import com.example.sedapp.presentation.dashboard.home.search.SearchScreen


fun NavGraphBuilder.dashboardGraph(
    rootNavController: NavHostController
) {
    navigation(
        route = Routes.DASHBOARD_GRAPH,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            DashboardScaffold(
                navController = rootNavController,
            ) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    modifier = Modifier.padding(it),
                    viewModel = viewModel,
                    onSearchClicked = {
                        rootNavController.navigate(Routes.SEARCH)
                    },
                    onRestaurantClicked = { restaurantName -> // Changed from restaurantId
                        rootNavController.navigate("${Routes.RESTAURANT_DETAILS}/$restaurantName")
                    },
                    onAllCategoriesClicked = {
                        rootNavController.navigate(Routes.FOODS)
                    },
                    onAllRestaurantsClicked = {
                        rootNavController.navigate(Routes.RESTAURANT)
                    },
                    onCategoryClicked = { category ->
                        rootNavController.navigate(Routes.FOODS + "/${category.categoryId}")
                        category.isCategorySelected = true
                    }
                )
            }
        }
        composable(Routes.BAG) {
            DashboardScaffold(
                navController = rootNavController,
            ) {
                BagScreen(modifier = Modifier.padding(it))
            }
        }
        composable(Routes.ORDERS) {
            DashboardScaffold(
                navController = rootNavController
            ) {
                OrdersScreen(modifier = Modifier.padding(it))
            }
        }
        composable(Routes.PROFILE) {
            DashboardScaffold(
                navController = rootNavController
            ) {
                ProfileScreen(modifier = Modifier.padding(it))
            }
        }
        composable(Routes.SEARCH) {
            SearchScreen(
                onBackClicked = {
                    rootNavController.popBackStack()
                },
                onRestaurantClick = { name ->
                    rootNavController.navigate("${Routes.RESTAURANT_DETAILS}/$name")
                }
            )
        }
        composable(Routes.FOODS) {
            FoodScreen(onBackClicked = {
                rootNavController.popBackStack()
            })
        }

        composable(Routes.RESTAURANT) {
            RestaurantScreen(
                onBackClicked = {
                    rootNavController.popBackStack()
                },

                onRestaurantClick = { name ->
                    rootNavController.navigate("${Routes.RESTAURANT_DETAILS}/$name")
                }
            )
        }
        composable(
            route = "${Routes.RESTAURANT_DETAILS}/{restaurantName}",
            arguments = listOf(navArgument("restaurantName") { type = NavType.StringType })
        ) {
            RestaurantDetailsScreen(
                onBackClicked = {
                    rootNavController.popBackStack()
                },
                onBagClicked = { }
            )
        }

    }
}
