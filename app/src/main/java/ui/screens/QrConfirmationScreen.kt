package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
fun QrConfirmationScreen(
    parkingName: String,
    slot: String,
    date: String,
    time: String,
    duration: String,
    onDone: () -> Unit
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
                    // Keep booking confirmed page stable.
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

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "✓",
            color = Green,
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,

            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Text(
            text = "Booking Confirmed!",
            color = White,
            fontSize = 23.sp,
            fontWeight = FontWeight.ExtraBold,

            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = "Your parking space has been reserved.",
            color = Gray,
            fontSize = 12.sp,

            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Card,
                    RoundedCornerShape(18.dp)
                )
                .padding(20.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "Parking QR",
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Box(
                modifier = Modifier
                    .background(
                        Color.White,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(18.dp),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "▦",
                    color = Color.Black,
                    fontSize = 70.sp
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = parkingName,
                color = White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "Slot: $slot",
                color = Gray,
                fontSize = 12.sp
            )

            Text(
                text = "Date: $date",
                color = Gray,
                fontSize = 12.sp
            )

            Text(
                text = "Time: $time",
                color = Gray,
                fontSize = 12.sp
            )

            Text(
                text = "Duration: $duration",
                color = Gray,
                fontSize = 12.sp
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
                    onDone()
                }
                .padding(
                    vertical = 17.dp
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "Go to My Bookings →",
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