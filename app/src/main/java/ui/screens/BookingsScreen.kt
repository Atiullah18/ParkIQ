package ui.screens

import android.content.Intent
import android.net.Uri

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.parkiq.R

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// ============================================================
// COLORS
// ============================================================

private val Background = Color(0xFFF7FAFF)
private val White = Color.White
private val Navy = Color(0xFF10254A)
private val Gray = Color(0xFF73829B)

private val Blue = Color(0xFF2477E8)
private val LightBlue = Color(0xFFEAF4FF)

private val Green = Color(0xFF19D98A)
private val DarkGreen = Color(0xFF07935A)
private val LightGreen = Color(0xFFE9FFF5)

private val Pink = Color(0xFFFF4F9A)
private val LightPink = Color(0xFFFFEEF7)

private val PurpleLight = Color(0xFFF1EDFF)
private val Border = Color(0xFFE6EDF7)
private val TipBackground = Color(0xFFFFF8E7)

// ============================================================
// BOOKING MODEL
// ============================================================

data class ParkingBooking(
    val parkingName: String,
    val location: String,
    val vehicle: String,
    val slot: String,
    val date: String,
    val time: String,
    val status: String,
    val image: Int,
    val lat: Double = 12.9716,
    val lng: Double = 77.5946,
    val duration: String = "1 Hour",
    val totalPrice: String = "₹0"
)

// ============================================================
// BOOKINGS SCREEN
// ============================================================

@Composable
fun BookingsScreen(
    bookings: List<ParkingBooking>,
    onBack: () -> Unit = {},
    onBookingClick: (ParkingBooking) -> Unit = {}
) {

    var selectedTab by remember {
        mutableStateOf("Active")
    }

    var selectedDetailBooking by remember {
        mutableStateOf<ParkingBooking?>(null)
    }

    val displayBookings = if (bookings.isNotEmpty()) {
        bookings
    } else {
        listOf(
            ParkingBooking(
                parkingName = "Green Park Residency",
                location = "Koramangala, Bengaluru",
                vehicle = "🚗 KA-01-MJ-2024",
                slot = "A-01",
                date = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Calendar.getInstance().time),
                time = "10:00 AM",
                status = "Confirmed",
                image = R.drawable.parking_car,
                lat = 12.9352,
                lng = 77.6245,
                duration = "2 Hours",
                totalPrice = "₹80"
            )
        )
    }

    val activeBookings = displayBookings.filter { booking ->
        isToday(booking.date) || (booking.status == "Confirmed" && !isPast(booking.date) && !isFuture(booking.date))
    }

    val upcomingBookings = displayBookings.filter { booking ->
        isFuture(booking.date)
    }

    val pastBookings = displayBookings.filter { booking ->
        isPast(booking.date) && !isToday(booking.date)
    }

    if (selectedDetailBooking != null) {
        BookingDetailDialog(
            booking = selectedDetailBooking!!,
            onDismiss = {
                selectedDetailBooking = null
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),

        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 10.dp,
            bottom = 30.dp
        ),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ====================================================
        // BACK BUTTON
        // ====================================================

        item {

            Box(
                modifier = Modifier
                    .padding(
                        start = 4.dp,
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
                    color = Color(0xFF00E676),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ====================================================
        // TITLE + ILLUSTRATION
        // ====================================================

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "My Bookings",
                        color = Navy,
                        fontSize = 29.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Text(
                        text = "View and manage your parking reservations",
                        color = Gray,
                        fontSize = 11.sp
                    )
                }

                Image(
                    painter = painterResource(
                        id = R.drawable.booking_confirmation_illustration
                    ),
                    contentDescription = "Booking illustration",
                    modifier = Modifier
                        .width(125.dp)
                        .height(88.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // ====================================================
        // TABS
        // ====================================================

        item {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        White,
                        RoundedCornerShape(18.dp)
                    )
                    .padding(5.dp)
            ) {

                BookingTab(
                    title = "◷\nActive",
                    selected = selectedTab == "Active",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedTab = "Active"
                    }
                )

                BookingTab(
                    title = "▣\nUpcoming",
                    selected = selectedTab == "Upcoming",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedTab = "Upcoming"
                    }
                )

                BookingTab(
                    title = "◴\nPast",
                    selected = selectedTab == "Past",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        selectedTab = "Past"
                    }
                )
            }
        }

        // ====================================================
        // ACTIVE
        // ====================================================

        if (selectedTab == "Active") {

            if (activeBookings.isEmpty()) {

                item {

                    EmptyBookingState(
                        icon = "◷",
                        title = "No active booking",
                        message = "A booking for today will appear here."
                    )
                }

            } else {

                items(
                    items = activeBookings,
                    key = {
                        "active-${it.parkingName}-${it.slot}-${it.date}-${it.time}"
                    }
                ) { booking ->

                    ActiveBookingCard(
                        booking = booking,

                        onViewDetails = {
                            selectedDetailBooking = booking
                            onBookingClick(booking)
                        }
                    )
                }
            }
        }

        // ====================================================
        // UPCOMING
        // ====================================================

        if (selectedTab == "Upcoming") {

            if (upcomingBookings.isEmpty()) {

                item {

                    EmptyBookingState(
                        icon = "▣",
                        title = "No upcoming bookings",
                        message = "A booking for a future date will appear here."
                    )
                }

            } else {

                items(
                    items = upcomingBookings,
                    key = {
                        "upcoming-${it.parkingName}-${it.slot}-${it.date}-${it.time}"
                    }
                ) { booking ->

                    NormalBookingCard(
                        booking = booking,
                        statusText = "Upcoming",
                        statusColor = Pink,
                        statusBackground = LightPink,
                        onClick = {
                            selectedDetailBooking = booking
                            onBookingClick(booking)
                        }
                    )
                }
            }
        }

        // ====================================================
        // PAST
        // ====================================================

        if (selectedTab == "Past") {

            if (pastBookings.isEmpty()) {

                item {

                    EmptyBookingState(
                        icon = "◴",
                        title = "No past bookings",
                        message = "Older parking reservations will appear here."
                    )
                }

            } else {

                items(
                    items = pastBookings,
                    key = {
                        "past-${it.parkingName}-${it.slot}-${it.date}-${it.time}"
                    }
                ) { booking ->

                    NormalBookingCard(
                        booking = booking,
                        statusText = "Completed",
                        statusColor = Blue,
                        statusBackground = LightBlue,
                        onClick = {
                            selectedDetailBooking = booking
                            onBookingClick(booking)
                        }
                    )
                }

                item {
                    TipCard()
                }
            }
        }

        // ====================================================
        // STRESS FREE BANNER
        // ====================================================

        item {

            StressFreeCard()
        }
    }
}

// ============================================================
// BOOKING DETAIL DIALOG
// ============================================================

@Composable
private fun BookingDetailDialog(
    booking: ParkingBooking,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Booking Details",
                    color = Navy,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✕",
                    color = Gray,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = booking.image),
                    contentDescription = booking.parkingName,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = booking.parkingName,
                        color = Navy,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📍 ${booking.location}",
                        color = Gray,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(label = "Status", value = booking.status)
            DetailRow(label = "Slot", value = booking.slot)
            DetailRow(label = "Vehicle", value = booking.vehicle)
            DetailRow(label = "Date", value = booking.date)
            DetailRow(label = "Time", value = booking.time)
            DetailRow(label = "Duration", value = booking.duration)
            DetailRow(label = "Amount", value = booking.totalPrice)
            DetailRow(label = "Coordinates", value = "${booking.lat}, ${booking.lng}")

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Blue, RoundedCornerShape(12.dp))
                    .clickable { onDismiss() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Close",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Gray,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Navy,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================
// TAB
// ============================================================

@Composable
private fun BookingTab(
    title: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .background(
                color = if (selected) {
                    LightPink
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(15.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                vertical = 11.dp
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = title,
            color = if (selected) {
                Pink
            } else {
                Navy
            },
            fontSize = 12.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ============================================================
// ACTIVE BOOKING CARD
// ============================================================

@Composable
private fun ActiveBookingCard(
    booking: ParkingBooking,
    onViewDetails: () -> Unit
) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                LightGreen,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                Color(0xFFD7F7E7),
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {

        // ====================================================
        // ACTIVE TITLE
        // ====================================================

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(DarkGreen)
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Active Parking",
                color = DarkGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(14.dp)
        )

        // ====================================================
        // PARKING DETAILS
        // ====================================================

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(
                    id = booking.image
                ),
                contentDescription = booking.parkingName,
                modifier = Modifier
                    .size(88.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = booking.parkingName,
                    color = Navy,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "📍 ${booking.location}",
                    color = Gray,
                    fontSize = 11.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(5.dp)
                ) {

                    InfoPill(
                        text = "Slot ${booking.slot}",
                        background = LightBlue
                    )

                    InfoPill(
                        text = booking.duration,
                        background = PurpleLight
                    )

                    InfoPill(
                        text = booking.totalPrice,
                        background = LightGreen
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        // ====================================================
        // TIME REMAINING
        // ====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFFDDF9EC),
                    RoundedCornerShape(14.dp)
                )
                .padding(11.dp),
            horizontalArrangement =
                Arrangement.Center,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "◷  Time Remaining",
                color = DarkGreen,
                fontSize = 12.sp
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = "1h 25m",
                color = DarkGreen,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ====================================================
        // VEHICLE
        // ====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    LightBlue,
                    RoundedCornerShape(12.dp)
                )
                .padding(11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "🚗",
                fontSize = 18.sp
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                text = "Vehicle: ${
                    booking.vehicle.replace(
                        "🚗 ",
                        ""
                    )
                }",
                color = Gray,
                fontSize = 12.sp
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ====================================================
        // DATE + TIME
        // ====================================================

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Text(
                text = "📅 ${booking.date}",
                color = Navy,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "🕐 ${booking.time}",
                color = Navy,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ====================================================
        // VIEW DETAILS
        // ====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    LightBlue,
                    RoundedCornerShape(13.dp)
                )
                .clickable {
                    onViewDetails()
                }
                .padding(
                    vertical = 13.dp
                ),
            horizontalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "View Details  ›",
                color = Blue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        // ====================================================
        // FIND MY VEHICLE
        // ACTIVE ONLY
        // DIRECT GOOGLE MAPS
        // ====================================================

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Green,
                    RoundedCornerShape(13.dp)
                )
                .clickable {

                    val googleMapsUri =
                        Uri.parse(
                            "google.navigation:q=${booking.lat},${booking.lng}"
                        )

                    val googleMapsIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            googleMapsUri
                        ).apply {

                            setPackage(
                                "com.google.android.apps.maps"
                            )
                        }

                    try {

                        context.startActivity(
                            googleMapsIntent
                        )

                    } catch (_: Exception) {

                        val browserUri =
                            Uri.parse(
                                "https://www.google.com/maps/dir/?api=1&destination=${booking.lat},${booking.lng}"
                            )

                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                browserUri
                            )
                        )
                    }
                }
                .padding(
                    vertical = 15.dp
                ),
            horizontalArrangement =
                Arrangement.Center,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "📍  Find My Vehicle",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================================
// NORMAL BOOKING CARD
// ============================================================

@Composable
private fun NormalBookingCard(
    booking: ParkingBooking,
    statusText: String,
    statusColor: Color,
    statusBackground: Color,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                White,
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                Border,
                RoundedCornerShape(18.dp)
            )
            .clickable {
                onClick()
            }
            .padding(11.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        // ====================================================
        // IMAGE
        // ====================================================

        Image(
            painter = painterResource(
                id = booking.image
            ),
            contentDescription =
                booking.parkingName,
            modifier = Modifier
                .size(68.dp)
                .clip(
                    RoundedCornerShape(11.dp)
                ),
            contentScale =
                ContentScale.Crop
        )

        Spacer(
            modifier = Modifier.width(11.dp)
        )

        // ====================================================
        // DETAILS
        // ====================================================

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = booking.parkingName,
                color = Navy,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "📍 ${booking.location}",
                color = Gray,
                fontSize = 9.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Slot ${booking.slot}",
                color = Blue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "📅 ${booking.date}",
                color = Navy,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "🕐 ${booking.time}",
                color = Gray,
                fontSize = 10.sp
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text =
                    "${booking.duration}  •  ${booking.totalPrice}",
                color = Gray,
                fontSize = 9.sp
            )
        }

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        // ====================================================
        // STATUS
        // ====================================================

        Column(
            horizontalAlignment =
                Alignment.End
        ) {

            Box(
                modifier = Modifier
                    .background(
                        statusBackground,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 7.dp
                    )
            ) {

                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "›",
                color = Blue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================================
// INFO PILL
// ============================================================

@Composable
private fun InfoPill(
    text: String,
    background: Color
) {

    Box(
        modifier = Modifier
            .background(
                background,
                RoundedCornerShape(9.dp)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 5.dp
            )
    ) {

        Text(
            text = text,
            color = Blue,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================
// STRESS FREE CARD
// ============================================================

@Composable
private fun StressFreeCard() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                LightBlue,
                RoundedCornerShape(18.dp)
            )
            .padding(
                horizontal = 8.dp,
                vertical = 7.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(
                id =
                    R.drawable.booking_stress_free_illustration
            ),
            contentDescription =
                "Park Stress Free illustration",
            modifier = Modifier
                .width(105.dp)
                .height(80.dp),
            contentScale =
                ContentScale.Fit
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Park Stress Free!",
                color = Navy,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = "All your bookings in one place.",
                color = Gray,
                fontSize = 11.sp
            )
        }
    }
}

// ============================================================
// TIP CARD
// ============================================================

@Composable
private fun TipCard() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                TipBackground,
                RoundedCornerShape(17.dp)
            )
            .padding(14.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = "💡",
            fontSize = 23.sp
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "Tip",
                color = Navy,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text =
                    "Use Find My Vehicle to locate your parked vehicle easily.",
                color = Gray,
                fontSize = 10.sp
            )
        }
    }
}

// ============================================================
// EMPTY STATE
// ============================================================

@Composable
private fun EmptyBookingState(
    icon: String,
    title: String,
    message: String
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 70.dp,
                bottom = 70.dp
            ),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text = icon,
            color = Blue,
            fontSize = 48.sp
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = title,
            color = Navy,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = message,
            color = Gray,
            fontSize = 12.sp
        )
    }
}

// ============================================================
// DATE PARSER
// ============================================================

private fun parseBookingDate(
    date: String
): Calendar? {

    for (locale in listOf(Locale.ENGLISH, Locale.getDefault())) {
        try {
            val formatter = SimpleDateFormat(
                "dd MMM yyyy",
                locale
            )
            formatter.isLenient = false
            val parsedDate = formatter.parse(date)
            if (parsedDate != null) {
                return Calendar.getInstance().apply {
                    time = parsedDate
                }
            }
        } catch (_: Exception) {
        }
    }

    try {
        val formatter = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.ENGLISH
        )
        formatter.isLenient = false
        val parsedDate = formatter.parse(date)
        if (parsedDate != null) {
            return Calendar.getInstance().apply {
                time = parsedDate
            }
        }
    } catch (_: Exception) {
    }

    return null
}

// ============================================================
// TODAY
// ============================================================

private fun isToday(
    date: String
): Boolean {

    val bookingDate =
        parseBookingDate(date)
            ?: return false

    val today =
        Calendar.getInstance()

    return bookingDate.get(
        Calendar.YEAR
    ) ==
            today.get(
                Calendar.YEAR
            ) &&

            bookingDate.get(
                Calendar.DAY_OF_YEAR
            ) ==
            today.get(
                Calendar.DAY_OF_YEAR
            )
}

// ============================================================
// FUTURE
// ============================================================

private fun isFuture(
    date: String
): Boolean {

    val bookingDate =
        parseBookingDate(date)
            ?: return false

    val today =
        Calendar.getInstance()

    clearTime(today)
    clearTime(bookingDate)

    return bookingDate.after(today)
}

// ============================================================
// PAST
// ============================================================

private fun isPast(
    date: String
): Boolean {

    val bookingDate =
        parseBookingDate(date)
            ?: return false

    val today =
        Calendar.getInstance()

    clearTime(today)
    clearTime(bookingDate)

    return bookingDate.before(today)
}

// ============================================================
// CLEAR TIME
// ============================================================

private fun clearTime(
    calendar: Calendar
) {

    calendar.set(
        Calendar.HOUR_OF_DAY,
        0
    )

    calendar.set(
        Calendar.MINUTE,
        0
    )

    calendar.set(
        Calendar.SECOND,
        0
    )

    calendar.set(
        Calendar.MILLISECOND,
        0
    )
}
