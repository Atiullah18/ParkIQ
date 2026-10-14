package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun ConfirmBookingScreen(
    parkingName: String,
    location: String,
    totalPrice: String,
    hourlyPrice: Int,
    duration: Int,
    slot: String,
    date: String,
    time: String,
    onBack: () -> Unit,
    onConfirm: () -> Unit
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
            text = "Confirm Booking",
            color = White,
            fontSize = 27.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
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
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "📍 $location",
                color = Gray,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            BookingRow(
                label = "Parking Slot",
                value = slot
            )

            BookingRow(
                label = "Date",
                value = date
            )

            BookingRow(
                label = "Time",
                value = time
            )

            BookingRow(
                label = "Duration",
                value =
                    if (duration == 1) {
                        "1 Hour"
                    } else {
                        "$duration Hours"
                    }
            )

            BookingRow(
                label = "Hourly Rate",
                value = "₹$hourlyPrice"
            )

            BookingRow(
                label = "Total Price",
                value = totalPrice
            )

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text =
                    "Calculation: ₹$hourlyPrice × $duration = $totalPrice",

                color = Green,
                fontSize = 11.sp,

                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // =====================================================
        // PROCEED TO PAYMENT
        // =====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Green,
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    onConfirm()
                }
                .padding(
                    vertical = 17.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "Proceed to Payment →",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun BookingRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp
            ),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            color = Gray,
            fontSize = 12.sp
        )

        Text(
            text = value,
            color = White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}