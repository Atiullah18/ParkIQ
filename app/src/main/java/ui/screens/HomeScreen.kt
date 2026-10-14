package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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

import com.example.parkiq.R

// ========================================================
// COLORS
// ========================================================

private val HomeBackground = Color(0xFFF7F9FC)
private val DarkText = Color(0xFF10203A)
private val GrayText = Color(0xFF687386)

private val Blue = Color(0xFF2878F0)
private val Green = Color(0xFF08B77A)
private val Pink = Color(0xFFE94B83)
private val Yellow = Color(0xFFFFB000)

private val LightGreen = Color(0xFFE8F9F1)
private val LightBlue = Color(0xFFEAF3FF)
private val LightPink = Color(0xFFFFEEF4)
private val LightPurple = Color(0xFFF1EDFF)

// ========================================================
// HOME SCREEN
// ========================================================

@Composable
fun HomeScreen(
    rentalSpaces: List<ExploreParkingPlace> = emptyList(),
    onNavigate: (String) -> Unit = {},
    onLocationSelected: (String) -> Unit = {},
    onRentalParkingClick: (ExploreParkingPlace) -> Unit = {}
) {

    var showWhyParkIQDetails by remember {
        mutableStateOf(false)
    }

    if (showWhyParkIQDetails) {
        WhyParkIQDetailsScreen(
            onBack = {
                showWhyParkIQDetails = false
            }
        )
        return
    }

    // ====================================================
    // HOME
    // ====================================================

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
    ) {

        // ==================================================
        // HEADER
        // ==================================================

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 18.dp,
                        bottom = 4.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Park",
                            color = DarkText,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "IQ",
                            color = Blue,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Smart Parking, Smarter You",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clickable {
                            onNavigate("notifications")
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notifications",
                        tint = DarkText,
                        modifier = Modifier.size(30.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Pink)
                            .align(Alignment.TopEnd)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF9AADE0))
                        .clickable {
                            onNavigate("profile")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Md",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ==================================================
        // GREETING + HERO IMAGE
        // ==================================================

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Hi, Md!",
                        color = DarkText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Find and book parking easily.",
                        color = GrayText,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(85.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.home_hero),
                        contentDescription = "Parking illustration",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // ==================================================
        // PARK WITH PEACE OF MIND BANNER
        // ==================================================

        item {
            Image(
                painter = painterResource(
                    id = R.drawable.monthly_parking
                ),
                contentDescription = "Park with Peace of Mind",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 22.dp,
                        vertical = 14.dp
                    )
                    .clip(RoundedCornerShape(25.dp))
                    .height(190.dp)
                    .clickable {
                        onNavigate("explore")
                    },
                contentScale = ContentScale.FillBounds
            )
        }

        // ==================================================
        // WHY PARKIQ HEADER
        // ==================================================

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 10.dp,
                        bottom = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Why ParkIQ?",
                    color = DarkText,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable {
                            showWhyParkIQDetails = true
                        },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "See all  ›",
                        color = Blue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ==================================================
        // WHY PARKIQ
        // ==================================================

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                FeatureCard(
                    icon = Icons.Default.Security,
                    title = "Safe & Secure",
                    subtitle = "Verified parking\nspaces",
                    iconColor = Green,
                    background = LightGreen
                )

                FeatureCard(
                    icon = Icons.Default.Speed,
                    title = "Quick Booking",
                    subtitle = "Reserve in\nseconds",
                    iconColor = Blue,
                    background = LightBlue
                )

                FeatureCard(
                    icon = Icons.Default.Star,
                    title = "Affordable",
                    subtitle = "Best rates\nnear you",
                    iconColor = Pink,
                    background = LightPink
                )

                FeatureCard(
                    icon = Icons.Default.Eco,
                    title = "Eco Friendly",
                    subtitle = "Less time\non roads",
                    iconColor = Color(0xFF7B5CC7),
                    background = LightPurple
                )
            }
        }

        // ==================================================
        // RENT YOUR PARKING SPACE HEADER (+ ADD SPACE REMOVED)
        // ==================================================

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        top = 28.dp,
                        bottom = 12.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Rent Your Parking Space",
                        color = DarkText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Earn from your empty spots",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable {
                            onNavigate("my_parking_spaces")
                        },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "See all  ›",
                        color = Blue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ==================================================
        // 5 RENTAL PARKING SPACES (HORIZONTAL ROW)
        // ==================================================

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(
                        start = 22.dp,
                        end = 22.dp,
                        bottom = 30.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                rentalSpaces.forEach { parking ->
                    RentalParkingCard(
                        parking = parking,
                        onClick = {
                            onRentalParkingClick(parking)
                        }
                    )
                }
            }
        }
    }
}

// ========================================================
// RENTAL PARKING CARD
// ========================================================

@Composable
private fun RentalParkingCard(
    parking: ExploreParkingPlace,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(285.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .clickable { onClick() }
    ) {

        Image(
            painter = painterResource(id = parking.image),
            contentDescription = parking.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 22.dp,
                        topEnd = 22.dp
                    )
                ),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Text(
                text = parking.name,
                color = DarkText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Pink,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = parking.location,
                    color = GrayText,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Yellow,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = parking.rating,
                    color = GrayText,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = parking.slots,
                    color = GrayText,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "₹${parking.hourlyPrice}/hr",
                    color = Green,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlue, RoundedCornerShape(12.dp))
                    .clickable { onClick() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Manage / AI Flow  ›",
                    color = Blue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ========================================================
// WHY PARKIQ DETAILS SCREEN
// ========================================================

@Composable
private fun WhyParkIQDetailsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HomeBackground)
            .verticalScroll(rememberScrollState())
            .padding(22.dp)
    ) {

        Box(
            modifier = Modifier
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

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Why ParkIQ?",
            color = DarkText,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Discover why ParkIQ is your ultimate smart parking solution.",
            color = GrayText,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        WhyParkIQDetailCard(
            icon = Icons.Default.Security,
            title = "Safe & Secure",
            description = "Every parking space on ParkIQ is thoroughly verified with 24/7 CCTV surveillance, security guards, and insured premises so you can park with complete peace of mind.",
            iconColor = Green,
            background = LightGreen
        )

        Spacer(modifier = Modifier.height(14.dp))

        WhyParkIQDetailCard(
            icon = Icons.Default.Speed,
            title = "Quick Booking",
            description = "Skip the endless searching. Reserve your preferred parking slot in just a few seconds with our instant booking flow and seamless QR code entry.",
            iconColor = Blue,
            background = LightBlue
        )

        Spacer(modifier = Modifier.height(14.dp))

        WhyParkIQDetailCard(
            icon = Icons.Default.Star,
            title = "Affordable Pricing",
            description = "Enjoy transparent hourly and monthly rates with zero hidden fees, exclusive discounts, and reward points on every single booking.",
            iconColor = Pink,
            background = LightPink
        )

        Spacer(modifier = Modifier.height(14.dp))

        WhyParkIQDetailCard(
            icon = Icons.Default.Eco,
            title = "Eco Friendly",
            description = "Reduce fuel consumption and carbon emissions by heading directly to your pre-booked slot without circling around looking for parking.",
            iconColor = Color(0xFF7B5CC7),
            background = LightPurple
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun WhyParkIQDetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    iconColor: Color,
    background: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = DarkText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = GrayText,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

// ========================================================
// FEATURE CARD
// ========================================================

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    background: Color
) {

    Column(
        modifier = Modifier
            .width(145.dp)
            .height(205.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(background)
            .padding(
                horizontal = 10.dp,
                vertical = 18.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(35.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            color = DarkText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            color = GrayText,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}
