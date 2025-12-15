package com.example.sedapp.ui.navigation

import android.provider.Settings.Global.getString
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sedapp.R
import com.example.sedapp.presentation.auth.screens.OnboardingScreen
import com.example.sedapp.presentation.auth.screens.SignInScreen
import com.example.sedapp.presentation.auth.screens.SignUpScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Screen.OnboardingScreen.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.OnboardingScreen.route
        ) {
            OnboardingScreen(
                onSignInClicked = {
                    navController.navigate(Screen.SignInScreen.route)
                },
                onSignUpClicked = {
                    navController.navigate(Screen.SignUpScreen.route)
                }
            )
        }

        composable(Screen.SignUpScreen.route) {
            SignUpScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    // Navigate to home/main screen after successful sign up
                    // For now, navigate back to onboarding
                    // You can add a HomeScreen route later
                    navController.popBackStack(Screen.OnboardingScreen.route, inclusive = false)
                }
            )
        }
        composable(Screen.SignInScreen.route) {
            SignInScreen(
                onSignUpClicked = {
                    navController.navigate(Screen.SignUpScreen.route)
                },
                onSignInSuccess = {
                    // Navigate to home/main screen after successful sign in
                    // For now, navigate back to onboarding
                    // You can add a HomeScreen route later
                    navController.popBackStack(Screen.OnboardingScreen.route, inclusive = false)
                },
                onGoogleSignInClick = {

                }
            )
        }
    }
}