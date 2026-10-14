package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF06111F)
private val Card = Color(0xFF102337)
private val Green = Color(0xFF00E676)
private val White = Color(0xFFF5F7FA)
private val Gray = Color(0xFF9EABBD)

@Composable
fun PaymentScreen(
    parkingName: String,
    amount: String,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp)
    ) {

        // SAME BACK POSITION
        Box(
            modifier = Modifier
                .padding(
                    top = 22.dp,
                    bottom = 8.dp
                )
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clickable {
                    onBack()
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "‹ Back",
                color = Green,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Payment",
            color = White,
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Complete your parking reservation",
            color = Gray,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Card,
                    RoundedCornerShape(18.dp)
                )
                .padding(18.dp)
        ) {

            Text(
                text = parkingName,
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Amount to Pay",
                color = Gray,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = amount,
                color = Green,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Card,
                    RoundedCornerShape(14.dp)
                )
                .clickable { }
                .padding(17.dp)
        ) {

            Text(
                text = "💳 UPI / Card / Wallet",
                color = White,
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Green,
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    onPaymentSuccess()
                }
                .padding(
                    vertical = 17.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "Pay Now →",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}