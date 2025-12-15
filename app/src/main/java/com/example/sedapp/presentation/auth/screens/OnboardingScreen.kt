package com.example.sedapp.presentation.auth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.sedapp.ui.theme.SedAppOrange
import com.example.sedapp.ui.theme.WarmWhite


@Composable
fun OnboardingScreen(
    onSignInClicked: () -> Unit = {},
    onSignUpClicked: () -> Unit = {}
) {
    // val composition by rememberLottieComposition(LottieCompositionSpec.Asset("onboarding_animation.json"))
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("raw/onboarding_animation.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )

    Surface(
        color = WarmWhite,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.85f) // Restrict width slightly
            ) {

                Spacer(modifier = Modifier.height(32.dp))
                LottieAnimation(
                    composition = composition,
                    progress = {
                        progress
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 3. Title Text
                Text(
                    text = "Set your table up",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Subtitle/Description Text
                Text(
                    text = "Get all your loved foods in one once place,\nyou just place the order we do the rest",
                    fontSize = 15.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(48.dp))

                // 5. Primary Button ("SIGN IN")
                Button(
                    onClick = onSignInClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = SedAppOrange),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "SIGN IN",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6. Secondary Button ("Sign Up")
                TextButton(
                    onClick = onSignUpClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sign Up",
                        color = SedAppOrange, // Reusing the primary color for the text
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Pushes content up
        }
    }
}

// --- Preview Function ---
@Preview(showBackground = true)
@Composable
fun PreviewOnboardingScreen() {
    MaterialTheme {
        OnboardingScreen()
    }
}