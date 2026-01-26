package com.example.sedapp.presentation.dashboard.bag.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.presentation.dashboard.home.search.SearchAnimationContent


@Preview(showBackground = true)
@Composable
fun PreviewPaymentSuccessScreen() {
    PaymentSuccessScreen(paymentCompleted = {

    }, onBack = {

    })
}

@Composable
fun PaymentSuccessScreen(
    onBack: () -> Unit,
    paymentCompleted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Charcoal
            )
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            SearchAnimationContent(
                animationRes = "raw/payment_done_animation.json",
                title = "Payment completed",
                subtitle = "Thank you for your order. Bon appetite!"
            )

            Button(
                onClick = { paymentCompleted() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.BottomCenter),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SedAppOrange,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "BACK TO HOME", style = MaterialTheme.typography.titleMedium)
            }
        }

    }
}