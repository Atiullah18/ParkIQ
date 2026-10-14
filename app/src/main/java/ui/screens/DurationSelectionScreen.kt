package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF06111F)
private val Card = Color(0xFF102337)
private val Green = Color(0xFF00E676)
private val White = Color(0xFFF5F7FA)
private val Gray = Color(0xFF9EABBD)

@Composable
fun DurationSelectionScreen(
    parkingName: String,
    slot: String,
    hourlyPrice: Int,
    availableDurations: List<Int>,
    onBack: () -> Unit,
    onContinue: (Int) -> Unit
) {
    var selectedDuration by remember { mutableStateOf<Int?>(null) }
    val totalPrice = selectedDuration?.let { it * hourlyPrice } ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(18.dp)
    ) {
        Text(
            text = "‹  Back",
            color = Green,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable { onBack() }
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Select Duration",
            color = White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = parkingName,
            color = Green,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Slot: $slot • ₹$hourlyPrice / hour",
            color = Gray,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Available Durations",
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            availableDurations.forEach { duration ->
                DurationOption(
                    hours = duration,
                    price = duration * hourlyPrice,
                    isSelected = selectedDuration == duration,
                    onClick = { selectedDuration = duration }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // PRICE CALCULATION SUMMARY
        if (selectedDuration != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Card, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Duration", color = Gray, fontSize = 14.sp)
                    Text("$selectedDuration Hours", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Price", color = Gray, fontSize = 14.sp)
                    Text("₹$totalPrice", color = Green, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        val canContinue = selectedDuration != null
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (canContinue) Green else Card,
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable(enabled = canContinue) {
                    onContinue(selectedDuration!!)
                }
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (canContinue) "Confirm Duration →" else "Please select duration",
                color = if (canContinue) Color.Black else Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun DurationOption(
    hours: Int,
    price: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) Green.copy(alpha = 0.1f) else Card,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Green else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$hours ${if (hours == 1) "Hour" else "Hours"}",
                color = if (isSelected) Green else White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Perfect for short visits",
                color = Gray,
                fontSize = 11.sp
            )
        }
        Text(
            text = "₹$price",
            color = if (isSelected) Green else White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
