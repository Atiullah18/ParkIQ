package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parkiq.R

private val SavedBackground = Color(0xFFF7F9FC)
private val SavedCard = Color.White
private val SavedNavy = Color(0xFF14223D)
private val SavedGray = Color(0xFF7E899B)
private val SavedBlue = Color(0xFF2878F0)
private val SavedGreen = Color(0xFF08B878)
private val SavedPink = Color(0xFFE94B91)
private val SavedYellow = Color(0xFFFFB900)

data class SavedParking(
    val name: String,
    val location: String,
    val price: String,
    val rating: String,
    val image: Int
)

@Composable
fun SavedScreen(
    bookings: List<ParkingBooking>,
    onBack: () -> Unit = {},
    onParkingClick: (SavedParking) -> Unit = {}
) {

    val savedPlaces = bookings
        .asReversed()
        .distinctBy { it.parkingName }
        .map { booking ->
            SavedParking(
                name = booking.parkingName,
                location = booking.location,
                price = getHourlyPrice(booking.parkingName),
                rating = getParkingRating(booking.parkingName),
                image = booking.image
            )
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SavedBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 10.dp,
            bottom = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // ==========================================
        // BACK BUTTON
        // ==========================================
        item {

            androidx.compose.foundation.layout.Box(
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
                    color = Color(0xFF00E676),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ==========================================
        // TITLE + SAVED ILLUSTRATION
        // ==========================================
        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Saved Parking",
                        color = SavedNavy,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(3.dp)
                    )

                    Text(
                        text = "Your favourite parking spots",
                        color = SavedGray,
                        fontSize = 11.sp
                    )
                }

                Image(
                    painter = painterResource(
                        id = R.drawable.parking_saved_illustration
                    ),
                    contentDescription = "Saved parking illustration",
                    modifier = Modifier
                        .width(125.dp)
                        .height(90.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )
        }

        // ==========================================
        // EMPTY STATE
        // ==========================================
        if (savedPlaces.isEmpty()) {

            item {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    BoxLikeHeart()

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "No saved parking yet",
                        color = SavedNavy,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    Text(
                        text = "Book a parking area from Explore",
                        color = SavedGray,
                        fontSize = 12.sp
                    )
                }
            }

        } else {

            // ==========================================
            // SAVED PARKING CARDS
            // ==========================================
            items(
                items = savedPlaces,
                key = { it.name }
            ) { parking ->

                SavedParkingCard(
                    parking = parking,
                    onClick = {
                        onParkingClick(parking)
                    }
                )
            }
        }
    }
}

@Composable
private fun BoxLikeHeart() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(SavedPink.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "♥",
            color = SavedPink,
            fontSize = 28.sp
        )
    }
}

@Composable
private fun SavedParkingCard(
    parking: SavedParking,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = SavedCard,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onClick()
            }
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ==========================================
        // PARKING IMAGE
        // ==========================================
        Image(
            painter = painterResource(
                id = parking.image
            ),
            contentDescription = parking.name,
            modifier = Modifier
                .size(68.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                ),
            contentScale = ContentScale.Crop
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        // ==========================================
        // PARKING DETAILS
        // ==========================================
        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = parking.name,
                color = SavedNavy,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "📍 ${parking.location}",
                color = SavedGray,
                fontSize = 10.sp
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "⭐ ${parking.rating}",
                    color = SavedGray,
                    fontSize = 10.sp
                )

                Spacer(
                    modifier = Modifier.width(7.dp)
                )

                Text(
                    text = "Saved",
                    color = SavedPink,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ==========================================
        // PRICE + ARROW
        // ==========================================
        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = parking.price,
                color = SavedGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "›",
                color = SavedBlue,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==========================================
// HOURLY PRICE
// ==========================================
private fun getHourlyPrice(
    parkingName: String
): String {

    return when {

        parkingName.contains(
            "Green Park",
            ignoreCase = true
        ) -> "₹40/hr"

        parkingName.contains(
            "Smart Square",
            ignoreCase = true
        ) -> "₹30/hr"

        parkingName.contains(
            "Lake View",
            ignoreCase = true
        ) -> "₹35/hr"

        parkingName.contains(
            "City Center",
            ignoreCase = true
        ) -> "₹50/hr"

        parkingName.contains(
            "MG Road",
            ignoreCase = true
        ) -> "₹60/hr"

        parkingName.contains(
            "Metro View",
            ignoreCase = true
        ) -> "₹35/hr"

        parkingName.contains(
            "Urban Secure",
            ignoreCase = true
        ) -> "₹45/hr"

        parkingName.contains(
            "Bangalore Central",
            ignoreCase = true
        ) -> "₹55/hr"

        else -> "₹40/hr"
    }
}

// ==========================================
// RATING
// ==========================================
private fun getParkingRating(
    parkingName: String
): String {

    return when {

        parkingName.contains(
            "Green Park",
            ignoreCase = true
        ) -> "4.8"

        parkingName.contains(
            "Smart Square",
            ignoreCase = true
        ) -> "4.5"

        parkingName.contains(
            "Lake View",
            ignoreCase = true
        ) -> "4.6"

        parkingName.contains(
            "City Center",
            ignoreCase = true
        ) -> "4.7"

        parkingName.contains(
            "MG Road",
            ignoreCase = true
        ) -> "4.9"

        parkingName.contains(
            "Metro View",
            ignoreCase = true
        ) -> "4.6"

        parkingName.contains(
            "Urban Secure",
            ignoreCase = true
        ) -> "4.8"

        parkingName.contains(
            "Bangalore Central",
            ignoreCase = true
        ) -> "4.7"

        else -> "4.8"
    }
}