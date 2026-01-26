package com.example.sedapp.presentation.dashboard.bag.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sedapp.R
import com.example.sedapp.core.ui.theme.Charcoal
import com.example.sedapp.core.ui.theme.DeepOrange
import com.example.sedapp.core.ui.theme.SedAppOrange
import com.example.sedapp.core.ui.theme.WarmWhite
import com.example.sedapp.core.util.getFormattedPrice
import com.example.sedapp.domain.model.Currency
import com.example.sedapp.domain.model.Order
import com.example.sedapp.domain.model.PayMethod
import com.example.sedapp.domain.model.PaymentMethod
import com.example.sedapp.presentation.dashboard.component.SedAppTopBar

@Preview(showBackground = true)
@Composable
fun PreviewPaymentScreen() {

    val state = PaymentUiState(
        selectedMethod = PaymentMethod.MASTER_CARD,
        paymentMethods = listOf(
            PayMethod(PaymentMethod.MASTER_CARD, label = "Master Card", R.drawable.mastercard),
            PayMethod(PaymentMethod.VISA, label = "Visa", R.drawable.visa),
            PayMethod(PaymentMethod.TOUCH_N_GO, label = "Paypal", R.drawable.toucngo)
        )
    )
    PaymentScreenContent(
        state = state,
        onBack = {},
        selectedMethod = {},
        onPayClicked = {})
}

@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    order: Order
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    viewModel.setOrder(order)
    LaunchedEffect(state.paymentSuccess) {
        if (state.paymentSuccess) {
            onPaymentSuccess()
        }
    }

    PaymentScreenContent(
        state = state,
        onBack = onBack,
        selectedMethod = viewModel::selectMethod,
        onPayClicked = viewModel::onPayClicked
    )
}


@Composable
fun PaymentScreenContent(
    state: PaymentUiState,
    onBack: () -> Unit = {},
    selectedMethod: (PaymentMethod) -> Unit = {},
    onPayClicked: () -> Unit = {}
) {
    Scaffold(
        topBar = {

            SedAppTopBar(
                title = "Payment",
                onBackClicked = onBack
            )
        },
        containerColor = Charcoal
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // 1. Header

                // 2. Payment Methods List
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(state.paymentMethods) { method ->
                        PaymentMethodItem(
                            method = method,
                            isSelected = state.selectedMethod == method.type,
                            onClick = { selectedMethod(method.type) }
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // 3. Card/Details Box
                CardDetailSection()

                Spacer(Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .border(
                            2.dp,
                            SedAppOrange,
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                        },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Absolute.Center
                    )
                    {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = SedAppOrange
                        )
                        Text(
                            text = "ADD NEW",
                            fontSize = 14.sp,
                            color = SedAppOrange,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Spacer(Modifier.weight(1f))


                // 4. Footer
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("TOTAL:", color = Color.LightGray, fontWeight = FontWeight.Bold)
                    Text(
                        getFormattedPrice(Currency.RM, state.totalAmount),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WarmWhite
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onPayClicked,
                    enabled = !state.isProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE76F00)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (state.isProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("PAY & CONFIRM", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }

    }
}

@Composable
fun CardDetailSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WarmWhite),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Image(
            painterResource(R.drawable.added_card),
            contentDescription = null
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "No card added",
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "You can add a card and save it for later use",
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun PaymentMethodItem(method: PayMethod, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    2.dp,
                    if (isSelected) DeepOrange else Color.Transparent,
                    RoundedCornerShape(16.dp)
                )
                .background(Color(0xFFF6F6F6))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(method.iconRes), null, Modifier.size(40.dp))
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = DeepOrange,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(18.dp)
                )
            }
        }
        Text(
            method.label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
