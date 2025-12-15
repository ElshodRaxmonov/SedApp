package com.example.sedapp.ui.navigation

sealed class Screen(val route: String) {

    // Authentication
    object OnboardingScreen : Screen("onboarding_screen")
    object SignInScreen : Screen("sign_in_screen")
    object SignUpScreen : Screen("sign_up_screen")

    // Dashboard
    object HomeScreen : Screen("home_screen")
    object BagScreen : Screen("bag_screen")
    object OrdersScreen : Screen("orders_screen")
    object ProfileScreen : Screen("profile_screen")

    // Functional Screens

    object SearchScreen : Screen("search_screen")
    object FoodsScreen : Screen("foods_screen")
    object RestaurantScreen : Screen("restaurant_screen")
    object RestaurantDetailsScreen : Screen("restaurant_details_screen")
    object PaymentScreen : Screen("payment_screen")



}