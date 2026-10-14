package ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val Background = Color(0xFF06111F)
private val Card = Color(0xFF102337)
private val Green = Color(0xFF00E676)
private val White = Color(0xFFF5F7FA)
private val Gray = Color(0xFF9EABBD)

@Composable
fun DateTimeScreen(
    parkingName: String,
    slot: String,
    onBack: () -> Unit,
    onContinue: (String, String, Int) -> Unit
) {

    val context = LocalContext.current

    var selectedDate by remember {
        mutableStateOf("")
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    var selectedDuration by remember {
        mutableStateOf(1)
    }

    var durationExpanded by remember {
        mutableStateOf(false)
    }

    // ========================================================
    // DATE PICKER
    // ========================================================

    fun openDatePicker() {

        val calendar =
            Calendar.getInstance()

        DatePickerDialog(
            context,

            { _, year, month, day ->

                val chosen =
                    Calendar.getInstance()

                chosen.set(
                    year,
                    month,
                    day
                )

                selectedDate =
                    SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    ).format(
                        chosen.time
                    )
            },

            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)

        ).show()
    }

    // ========================================================
    // TIME PICKER
    // ========================================================

    fun openTimePicker() {

        val calendar =
            Calendar.getInstance()

        TimePickerDialog(
            context,

            { _, hour, minute ->

                val chosen =
                    Calendar.getInstance()

                chosen.set(
                    Calendar.HOUR_OF_DAY,
                    hour
                )

                chosen.set(
                    Calendar.MINUTE,
                    minute
                )

                selectedTime =
                    SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault()
                    ).format(
                        chosen.time
                    )
            },

            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),

            false

        ).show()
    }

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
            text = "Select Date & Time",
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
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Parking Slot: $slot",
            color = Gray,
            fontSize = 12.sp
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // =====================================================
        // DATE
        // =====================================================

        Text(
            text = "Select Date",
            color = White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Card,
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    openDatePicker()
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 17.dp
                )
        ) {

            Text(
                text =
                    if (selectedDate.isEmpty()) {
                        "📅  Choose Date"
                    } else {
                        "📅  $selectedDate"
                    },

                color =
                    if (selectedDate.isEmpty()) {
                        Gray
                    } else {
                        White
                    },

                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // =====================================================
        // TIME
        // =====================================================

        Text(
            text = "Select Time",
            color = White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Card,
                    RoundedCornerShape(14.dp)
                )
                .clickable {
                    openTimePicker()
                }
                .padding(
                    horizontal = 16.dp,
                    vertical = 17.dp
                )
        ) {

            Text(
                text =
                    if (selectedTime.isEmpty()) {
                        "🕐  Choose Time"
                    } else {
                        "🕐  $selectedTime"
                    },

                color =
                    if (selectedTime.isEmpty()) {
                        Gray
                    } else {
                        White
                    },

                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // =====================================================
        // DURATION
        // =====================================================

        Text(
            text = "Select Duration",
            color = White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Box {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Card,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        durationExpanded = true
                    }
                    .padding(
                        horizontal = 16.dp,
                        vertical = 17.dp
                    )
            ) {

                Text(
                    text =
                        "⏱  $selectedDuration " +
                                if (
                                    selectedDuration == 1
                                ) {
                                    "Hour"
                                } else {
                                    "Hours"
                                } +
                                "  ▾",

                    color = White,
                    fontSize = 14.sp
                )
            }

            DropdownMenu(
                expanded = durationExpanded,

                onDismissRequest = {
                    durationExpanded = false
                }
            ) {

                listOf(
                    1,
                    2,
                    3,
                    4,
                    5
                ).forEach { duration ->

                    DropdownMenuItem(

                        text = {

                            Text(
                                text =
                                    if (
                                        duration == 1
                                    ) {
                                        "1 Hour"
                                    } else {
                                        "$duration Hours"
                                    }
                            )
                        },

                        onClick = {

                            selectedDuration =
                                duration

                            durationExpanded =
                                false
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        // =====================================================
        // CONTINUE
        // =====================================================

        val enabled =
            selectedDate.isNotEmpty() &&
                    selectedTime.isNotEmpty()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (enabled) {
                        Green
                    } else {
                        Card
                    },

                    RoundedCornerShape(14.dp)
                )
                .clickable(
                    enabled = enabled
                ) {

                    onContinue(
                        selectedDate,
                        selectedTime,
                        selectedDuration
                    )
                }
                .padding(
                    vertical = 17.dp
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "Continue →",

                color =
                    if (enabled) {
                        Color.Black
                    } else {
                        Gray
                    },

                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}