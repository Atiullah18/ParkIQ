package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
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
import data.db.ParkingSpaceEntity

private val DashboardBackground = Color(0xFF06111F)
private val DashboardCard = Color(0xFF102337)
private val DashboardBlue = Color(0xFF162D43)
private val Green = Color(0xFF00E676)
private val Gray = Color(0xFF687386)
private val Pink = Color(0xFFFF4F9A)

@Composable
fun MyParkingSpacesScreen(
    rentalSpaces: List<ExploreParkingPlace>,
    savedEntities: List<ParkingSpaceEntity>,
    onBack: () -> Unit,
    onManageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
    ) {
        // HEADER WITH GREEN BACK BUTTON
        Box(
            modifier = Modifier
                .padding(start = 20.dp, top = 22.dp, bottom = 4.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clickable { onBack() },
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
            text = "My Parking Spaces",
            color = Color.White,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp).padding(top = 4.dp, bottom = 2.dp)
        )
        Text(
            text = "Manage your rented parking spaces",
            color = Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 14.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(rentalSpaces) { parking ->
                val spaceId = getIdForParkingSpace(parking.name)
                val entity = savedEntities.find { it.id == spaceId }
                val availableHours = entity?.availableHours?.ifBlank { "6 AM - 10 PM" } ?: "6 AM - 10 PM"
                val slotsText = if (parking.slots.contains("slot", ignoreCase = true)) parking.slots else "${parking.slots} available"
                val ratingText = parking.rating.ifBlank { "4.8" }
                val rentText = "₹${parking.hourlyPrice}/hr"

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DashboardCard)
                        .clickable { onManageClick(spaceId) }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = parking.image),
                            contentDescription = parking.name,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = parking.name,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Pink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = parking.location,
                                    color = Gray,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rentText,
                                    color = Green,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB000),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = ratingText,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DashboardBlue, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Slots: $slotsText",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Hours: $availableHours",
                                color = Gray,
                                fontSize = 10.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Green, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Manage / AI Flow  ›",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getIdForParkingSpace(name: String): String {
    return when {
        name.contains("1st Block", ignoreCase = true) -> "jayanagar_1st_block"
        name.contains("2nd Block", ignoreCase = true) -> "jayanagar_2nd_block"
        name.contains("Jain", ignoreCase = true) -> "jain_university"
        name.contains("BTM Central", ignoreCase = true) -> "btm_central"
        name.contains("BTM Metro", ignoreCase = true) -> "btm_metro"
        else -> "jayanagar_1st_block"
    }
}
