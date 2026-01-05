package com.example.sedapp.core.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.sedapp.core.navigation.Routes
import com.example.sedapp.presentation.auth.screens.OnboardingScreen

fun NavGraphBuilder.onboardingGraph(
    navController: NavController
) {
    navigation(
        route = Routes.ONBOARDING_GRAPH,
        startDestination = Routes.ONBOARDING
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onSignUpClicked = {
                    navController.navigate(Routes.SIGN_UP) {
                        popUpTo(Routes.ONBOARDING_GRAPH) { inclusive = true }
                    }
                },
                onSignInClicked = {
                    navController.navigate(Routes.SIGN_IN) {
                        popUpTo(Routes.ONBOARDING_GRAPH) { inclusive = true }
                    }
                }
            )
        }
    }
}
