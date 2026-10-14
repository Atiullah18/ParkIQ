package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF06111F)
private val Card = Color(0xFF102337)
private val Card2 = Color(0xFF132A40)
private val Green = Color(0xFF00E676)
private val Red = Color(0xFF532431)
private val White = Color(0xFFF5F7FA)
private val Gray = Color(0xFF9EABBD)

@Composable
fun ParkingDetailsScreen(
    parking: String,
    location: String,
    price: String,
    rating: String,
    slots: String,
    image: Int,
    bookedSlots: Set<String> = emptySet(),
    onBack: () -> Unit = {},
    onBookNow: (String) -> Unit = {},
    onSlotSelected: (String) -> Unit = {}
) {

    var selectedSlot by remember {
        mutableStateOf<String?>(null)
    }

    val slotList = listOf(
        "A-01", "A-02", "A-03", "A-04", "A-05", "A-06",
        "B-01", "B-02", "B-03", "B-04", "B-05", "B-06"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // SAME BACK POSITION
        Box(
            modifier = Modifier
                .padding(
                    start = 20.dp,
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {

            item {

                Image(
                    painter = painterResource(image),

                    contentDescription = parking,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),

                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.padding(
                        horizontal = 20.dp,
                        vertical = 18.dp
                    )
                ) {

                    Text(
                        text = parking,
                        color = White,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "📍 $location",
                        color = Gray,
                        fontSize = 13.sp
                    )

                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )

                    Row(
                        horizontalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        InfoChip(
                            text = "Rating $rating"
                        )

                        InfoChip(
                            text = "₹$price/hr"
                        )

                        InfoChip(
                            text = "Available $slots"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(25.dp)
                    )

                    ParkingSafetySection()

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    HorizontalDivider(
                        color = Card2
                    )

                    Spacer(
                        modifier = Modifier.height(22.dp)
                    )

                    Text(
                        text = "Select Parking Slot",
                        color = White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Green = Available • Red = Booked",
                        color = Gray,
                        fontSize = 12.sp
                    )

                    Spacer(
                        modifier = Modifier.height(18.dp)
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        slotList.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                row.forEach { slot ->
                                    val isBooked = slot in bookedSlots
                                    val isSelected = selectedSlot == slot

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .background(
                                                when {
                                                    isSelected -> Green
                                                    isBooked -> Red
                                                    else -> Card
                                                },
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable(enabled = !isBooked) {
                                                selectedSlot = slot
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "[ $slot ]",
                                            color = if (isSelected) Color.Black else White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }

                                repeat(3 - row.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    Text(
                        text = if (selectedSlot == null) {
                            "Please select a parking slot"
                        } else {
                            "Selected Slot: $selectedSlot"
                        },
                        color = if (selectedSlot == null) Gray else Green,
                        fontSize = 13.sp,
                        fontWeight = if (selectedSlot == null) FontWeight.Normal else FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(15.dp)
                            )
                            .background(
                                if (selectedSlot != null) Green else Card
                            )
                            .clickable(enabled = selectedSlot != null) {
                                val slot = selectedSlot!!
                                onSlotSelected(slot)
                                onBookNow(slot)
                            }
                            .padding(
                                vertical = 16.dp
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = if (selectedSlot == null) "[ Select a Slot ]" else "Continue →",
                            color = if (selectedSlot != null) Color.Black else Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    text: String
) {

    Box(
        modifier = Modifier
            .border(
                1.dp,
                Card2,
                RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 7.dp
            )
    ) {

        Text(
            text = "[ $text ]",
            color = White,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun ParkingSafetySection() {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "🛡",
                fontSize = 16.sp
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "PARKING SAFETY",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "Safety Score",
                color = Gray,
                fontSize = 13.sp
            )

            Text(
                text = "92/100",
                color = White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(20.dp)
        ) {

            Text(
                text = "📹 CCTV",
                color = White,
                fontSize = 12.sp
            )

            Text(
                text = "💡 Well Lit",
                color = White,
                fontSize = 12.sp
            )

            Text(
                text = "👮 Security",
                color = White,
                fontSize = 12.sp
            )
        }
    }
}