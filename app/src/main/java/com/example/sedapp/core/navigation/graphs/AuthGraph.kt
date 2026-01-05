package com.example.sedapp.core.navigation.graphs

import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.sedapp.core.navigation.Routes
import com.example.sedapp.presentation.auth.screens.SignInScreen
import com.example.sedapp.presentation.auth.screens.SignUpScreen


fun NavGraphBuilder.authGraph(
    navController: NavController
) {
    navigation(
        route = Routes.AUTH_GRAPH,
        startDestination = Routes.SIGN_IN
    ) {
        composable(Routes.SIGN_IN) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Routes.DASHBOARD_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                },
                onSignUpClicked = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.SIGN_IN) {
                            inclusive = true
                        }
                    }
                },
                onGoogleSignInClick = {},
                viewModel = hiltViewModel()
            )
        }
        composable(Routes.SIGN_UP) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Routes.DASHBOARD_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                },
                onBackClicked = {
                    navController.navigateUp()
                },
                viewModel = hiltViewModel()
            )
        }
    }
}
