package com.example.sedapp.presentation.auth.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.DeepOrange
import com.example.sedapp.core.ui.theme.PrimaryOrange
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.TextFieldBackground
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.presentation.auth.AuthUiState
import com.example.sedapp.presentation.auth.AuthViewModel


@Composable
fun SignInScreen(
    onSignUpClicked: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.state.collectAsStateWithLifecycle()

    // Handle auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthUiState.Success -> {
                onSignInSuccess()
            }

            is AuthUiState.Error -> {
                // Error is shown via Snackbar below
            }

            else -> {}
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }

    // Show error snackbar
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Error) {
            snackBarHostState.showSnackbar(
                message = (authState as AuthUiState.Error).message,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepOrange,
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // 1. Top Orange Header Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f) // Take up 40% of the screen height
                    .background(DeepOrange) // Use the dark orange color
                    .clip(
                        RoundedCornerShape(
                            40.dp
                        )
                    )

                // If you need the curve
            ) {

                Image(
                    painter = painterResource(R.drawable.ellipse_1005),
                    contentDescription = "decort",
                    modifier = Modifier.align(Alignment.TopStart)
                )
                Image(
                    alignment = Alignment.TopEnd,
                    painter = painterResource(R.drawable.decort_two),
                    contentDescription = "decort",
                    modifier = Modifier.align(Alignment.TopEnd)

                )
                // Header Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, start = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {


                    Spacer(Modifier.height(40.dp))

                    Text(
                        text = "Sign In",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Please log into your existing account",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                    )
                }
            }
            Spacer(
                Modifier.height(16.dp)
            )
            // 2. Main Content Area (White Card Look)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .weight(0.7f) // Pull the content up over the curve
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email Field

                item {
                    Spacer(
                        Modifier.height(20.dp)
                    )
                    Text(
                        text = "EMAIL",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    CustomOutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "example@gmail.com",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    Text(
                        text = "PASSWORD",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    CustomOutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "••••••••",
                        keyboardType = KeyboardType.Password,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Log In Button
                    val isLoading = authState is AuthUiState.Loading
                    Button(
                        onClick = {
                            if (!isLoading) {
                                viewModel.signInWithEmail(email, password)
                            }
                        },
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && email.endsWith(
                            "@gmail.com"
                        ),
                        colors = ButtonDefaults.buttonColors(containerColor = SedAppOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "LOG IN",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Don't have an account? Sign Up
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Don't have an account? ", color = Color.DarkGray, fontSize = 14.sp)
                        Text(
                            text = "SIGN UP",
                            color = PrimaryOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onSignUpClicked)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Or", color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

                    // Google Button
                    Button(
                        onClick = { onGoogleSignInClick() },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = WarmWhite),
                        shape = RoundedCornerShape(36.dp),
                        modifier = Modifier.size(width = 120.dp, height = 50.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Image(
                            painter = painterResource(R.drawable.google),
                            contentDescription = "icon of google",
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// Custom TextField to match the rounded, flat design
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.6f)) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = TextFieldBackground,
            unfocusedContainerColor = TextFieldBackground,
            disabledContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = SedAppOrange,
            focusedTextColor = Color.Black
        ),
        modifier = Modifier.fillMaxWidth()
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewSignInScreen() {
    MaterialTheme {
//        SignInScreen()
    }
}