package com.example.sedapp.core.navigation.graphs


import DashboardScaffold
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.OrderType
import com.example.sedapp.domain.model.PaymentMethod
import com.example.sedapp.presentation.dashboard.bag.BagScreen
import com.example.sedapp.presentation.dashboard.bag.payment.PaymentScreen
import com.example.sedapp.presentation.dashboard.bag.payment.PaymentSuccessScreen
import com.example.sedapp.presentation.dashboard.home.HomeScreen
import com.example.sedapp.presentation.dashboard.home.HomeViewModel
import com.example.sedapp.presentation.dashboard.home.foods.FoodScreen
import com.example.sedapp.presentation.dashboard.home.foods.SavedFoodsScreen
import com.example.sedapp.presentation.dashboard.home.restaurants.RestaurantDetailsScreen
import com.example.sedapp.presentation.dashboard.home.restaurants.RestaurantScreen
import com.example.sedapp.presentation.dashboard.home.search.SearchScreen
import com.example.sedapp.presentation.dashboard.orders.OrdersScreen
import com.example.sedapp.presentation.dashboard.profile.ProfileScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.dashboardGraph(
    rootNavController: NavHostController
) {
    navigation(
        route = Routes.DASHBOARD_GRAPH,
        startDestination = Routes.HOME
    ) {
        composable(
            Routes.HOME,
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            DashboardScaffold(
                navController = rootNavController,
            ) { padding, listState ->
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    modifier = Modifier.padding(padding),
                    viewModel = viewModel,
                    scrollState = listState,
                    onSearchClicked = {
                        rootNavController.navigate(Routes.SEARCH)
                    },
                    onRestaurantClicked = { restaurantName ->
                        rootNavController.navigate("${Routes.RESTAURANT_DETAILS}/$restaurantName")
                    },
                    onAllCategoriesClicked = {
                        rootNavController.navigate(Routes.FOODS)
                    },
                    onAllRestaurantsClicked = {
                        rootNavController.navigate(Routes.RESTAURANT)
                    },
                    onCategoryClicked = { category ->
                        rootNavController.navigate("${Routes.FOODS}/${category.name}")
                    },
                    onSavedFoodsClicked = {
                        rootNavController.navigate(Routes.SAVED_FOODS)
                    }
                )
            }
        }
        composable(
            Routes.BAG,
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            DashboardScaffold(
                navController = rootNavController,
            ) { padding, _ ->
                BagScreen(
                    modifier = Modifier.padding(padding),
                    onNavigateToPayment = { order ->
                        rootNavController.currentBackStackEntry?.savedStateHandle?.set(
                            "order",
                            order
                        )
                        rootNavController.navigate(Routes.PAYMENT)
                    }
                )
            }
        }
        composable(
            Routes.ORDERS,
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            DashboardScaffold(
                navController = rootNavController
            ) { padding, listState ->
                OrdersScreen(
                    modifier = Modifier.padding(padding),
                    state = listState,
                    onNavigateToPayment = { order ->
                        rootNavController.currentBackStackEntry?.savedStateHandle?.set(
                            "order",
                            order
                        )
                        rootNavController.navigate(Routes.PAYMENT)
                    }
                )
            }
        }
        composable(
            Routes.PROFILE,
            enterTransition = { fadeIn(animationSpec = tween(700)) },
            exitTransition = { fadeOut(animationSpec = tween(700)) }
        ) {
            DashboardScaffold(
                navController = rootNavController
            ) { padding, scrollState ->
                ProfileScreen(
                    modifier = Modifier.padding(padding),
                    state = scrollState,
                    logOut = {

                        rootNavController.navigate(Routes.AUTH_GRAPH)

                    }
                )
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
        composable(
            route = "${Routes.FOODS}/{categoryName}",
            arguments = listOf(navArgument("categoryName") {
                type = NavType.StringType; defaultValue = ""
            })
        ) {
            FoodScreen(onBackClicked = {
                rootNavController.popBackStack()
            }, restaurantClicked = {
                rootNavController.navigate("${Routes.RESTAURANT_DETAILS}/$it")
            })
        }

        composable(Routes.FOODS) {
            FoodScreen(
                onBackClicked = {
                    rootNavController.popBackStack()
                },
                restaurantClicked = {
                    rootNavController.navigate("${Routes.RESTAURANT_DETAILS}/$it")
                })
        }
        composable(Routes.SAVED_FOODS) {
            SavedFoodsScreen(
                onBackClicked = {
                    rootNavController.popBackStack()
                }
            )
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

        composable(Routes.PAYMENT) { backStackEntry ->
            val order =
                rootNavController.previousBackStackEntry?.savedStateHandle?.get<Order>("order")
                    ?: Order(
                        orderId = "",
                        orderItem = emptyList(),
                        restaurantId = "",
                        status = com.example.sedapp.domain.model.OrderStatus.PENDING,
                        paymentMethod = PaymentMethod.CASH,
                        createdAt = 0,
                        userId = "",
                        totalPrice = 0.0,
                        orderType = OrderType.PRE_ORDER,


                        )
            PaymentScreen(
                onBack = {
                    rootNavController.popBackStack()
                },
                onPaymentSuccess = {
                    rootNavController.navigate(Routes.PAYMENT_PAID) {
                        popUpTo(Routes.BAG) { inclusive = true }
                        popUpTo(Routes.ORDERS) { inclusive = false } // Keep orders screen in stack if needed, or pop it too
                    }
                },
                order = order
            )
        }
        composable(Routes.PAYMENT_PAID) {
            PaymentSuccessScreen(
                onBack = {
                    rootNavController.navigate(Routes.HOME) {
                        popUpTo(Routes.DASHBOARD_GRAPH) { inclusive = true }
                    }
                },
                paymentCompleted = {
                    rootNavController.navigate(Routes.HOME) {
                        popUpTo(Routes.DASHBOARD_GRAPH) { inclusive = true }
                    }
                }
            )
        }

    }
}
