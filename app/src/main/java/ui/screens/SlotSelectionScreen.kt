package ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF06111F)
private val Card = Color(0xFF102337)
private val Green = Color(0xFF00E676)
private val Red = Color(0xFF532431)
private val White = Color(0xFFF5F7FA)
private val Gray = Color(0xFF9EABBD)

@Composable
fun SlotSelectionScreen(
    parkingName: String,
    bookedSlots: Set<String> = emptySet(),
    onBack: () -> Unit,
    onSlotSelected: (String) -> Unit
) {

    var selectedSlot by remember {
        mutableStateOf<String?>(null)
    }

    val slots = listOf(
        "A-01",
        "A-02",
        "A-03",
        "A-04",
        "A-05",
        "A-06",
        "B-01",
        "B-02",
        "B-03",
        "B-04",
        "C-01",
        "C-02",
        "C-03",
        "C-04"
    )

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
            text = "Select Parking Slot",
            color = White,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = parkingName,
            color = Green,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Row(
            horizontalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            LegendBox(
                color = Card,
                text = "Available"
            )

            LegendBox(
                color = Green,
                text = "Selected"
            )

            LegendBox(
                color = Red,
                text = "Booked"
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            slots.chunked(3).forEach { row ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(10.dp)
                ) {

                    row.forEach { slot ->

                        val isBooked =
                            slot in bookedSlots

                        val isSelected =
                            selectedSlot == slot

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .background(
                                    when {
                                        isSelected ->
                                            Green

                                        isBooked ->
                                            Red

                                        else ->
                                            Card
                                    },

                                    RoundedCornerShape(10.dp)
                                )
                                .clickable(
                                    enabled = !isBooked
                                ) {

                                    selectedSlot = slot
                                },

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "[ $slot ]",

                                color =
                                    if (isSelected) {
                                        Color.Black
                                    } else {
                                        White
                                    },

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize = 13.sp
                            )
                        }
                    }

                    repeat(
                        3 - row.size
                    ) {

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Text(
            text =
                if (selectedSlot == null) {
                    "Please select a parking slot"
                } else {
                    "Selected Slot: $selectedSlot"
                },

            color = Gray,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (selectedSlot != null) {
                        Green
                    } else {
                        Card
                    },

                    RoundedCornerShape(14.dp)
                )
                .clickable(
                    enabled = selectedSlot != null
                ) {

                    onSlotSelected(
                        selectedSlot!!
                    )
                }
                .padding(
                    vertical = 16.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    if (selectedSlot == null) {
                        "[ Select a Slot ]"
                    } else {
                        "Continue with $selectedSlot →"
                    },

                color =
                    if (selectedSlot != null) {
                        Color.Black
                    } else {
                        Gray
                    },

                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
private fun LegendBox(
    color: Color,
    text: String
) {

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .height(10.dp)
                .background(
                    color,
                    RoundedCornerShape(3.dp)
                )
                .padding(horizontal = 7.dp)
        )

        Spacer(
            modifier = Modifier
                .height(1.dp)
        )

        Text(
            text = text,
            color = Gray,
            fontSize = 10.sp,
            modifier = Modifier.padding(
                start = 5.dp
            )
        )
    }
}