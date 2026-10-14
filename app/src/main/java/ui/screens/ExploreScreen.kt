package ui.screens

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.parkiq.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

// ============================================================
// COLORS
// ============================================================

private val Background = Color(0xFFF8FAFF)
private val Navy = Color(0xFF142747)
private val Gray = Color(0xFF68758A)
private val Blue = Color(0xFF2878E8)
private val Green = Color(0xFF08B878)
private val Pink = Color(0xFFE94B91)
private val Yellow = Color(0xFFFFB900)

// ============================================================
// PARKING DATA MODEL
// ============================================================

data class ExploreParkingPlace(
    val name: String,
    val location: String,
    val hourlyPrice: Int,
    val rating: String,
    val slots: String,
    val image: Int,
    val availableDurations: List<Int>,
    val lat: Double = 12.9716,
    val lng: Double = 77.5946
)

// ============================================================
// LOCATION SUGGESTION
// ============================================================

private data class LocationSuggestion(
    val title: String,
    val address: String,
    val lat: Double,
    val lng: Double
)

// ============================================================
// 10 PARKING AREAS
// ============================================================

val defaultParkingPlaces = listOf(

    ExploreParkingPlace(
        name = "Green Park Residency",
        location = "Koramangala, Bengaluru",
        hourlyPrice = 40,
        rating = "4.8",
        slots = "8 slots",
        image = R.drawable.parking_car,
        availableDurations = listOf(1, 2, 3),
        lat = 12.9345,
        lng = 77.6266
    ),

    ExploreParkingPlace(
        name = "City Center Parking",
        location = "Indiranagar, Bengaluru",
        hourlyPrice = 50,
        rating = "4.7",
        slots = "5 slots",
        image = R.drawable.parking_bike,
        availableDurations = listOf(2, 3, 4),
        lat = 12.9784,
        lng = 77.6408
    ),

    ExploreParkingPlace(
        name = "MG Road Smart Parking",
        location = "MG Road, Bengaluru",
        hourlyPrice = 60,
        rating = "4.9",
        slots = "12 slots",
        image = R.drawable.parking_corporate,
        availableDurations = listOf(1, 2, 3, 4, 5),
        lat = 12.9738,
        lng = 77.6119
    ),

    ExploreParkingPlace(
        name = "Metro View Parking",
        location = "BTM Layout, Bengaluru",
        hourlyPrice = 35,
        rating = "4.6",
        slots = "10 slots",
        image = R.drawable.parking_owner,
        availableDurations = listOf(1, 2),
        lat = 12.9166,
        lng = 77.6101
    ),

    ExploreParkingPlace(
        name = "Urban Secure Parking",
        location = "HSR Layout, Bengaluru",
        hourlyPrice = 45,
        rating = "4.8",
        slots = "7 slots",
        image = R.drawable.parking_car,
        availableDurations = listOf(3, 4, 5),
        lat = 12.9141,
        lng = 77.6411
    ),

    ExploreParkingPlace(
        name = "Bangalore Central Parking",
        location = "Shivajinagar, Bengaluru",
        hourlyPrice = 55,
        rating = "4.7",
        slots = "9 slots",
        image = R.drawable.parking_bike,
        availableDurations = listOf(2, 4),
        lat = 12.9857,
        lng = 77.5971
    ),

    ExploreParkingPlace(
        name = "Smart Square Parking",
        location = "Jayanagar, Bengaluru",
        hourlyPrice = 30,
        rating = "4.5",
        slots = "6 slots",
        image = R.drawable.parking_corporate,
        availableDurations = listOf(1, 3, 5),
        lat = 12.9300,
        lng = 77.5800
    ),

    ExploreParkingPlace(
        name = "Lake View Parking",
        location = "Ulsoor, Bengaluru",
        hourlyPrice = 35,
        rating = "4.6",
        slots = "11 slots",
        image = R.drawable.parking_owner,
        availableDurations = listOf(1, 2, 3, 4),
        lat = 12.9817,
        lng = 77.6284
    ),

    ExploreParkingPlace(
        name = "Tech Park Parking",
        location = "Whitefield, Bengaluru",
        hourlyPrice = 50,
        rating = "4.8",
        slots = "15 slots",
        image = R.drawable.parking_car,
        availableDurations = listOf(4, 5),
        lat = 12.9698,
        lng = 77.7500
    ),

    ExploreParkingPlace(
        name = "Airport Secure Parking",
        location = "Hebbal, Bengaluru",
        hourlyPrice = 45,
        rating = "4.7",
        slots = "20 slots",
        image = R.drawable.parking_bike,
        availableDurations = listOf(1, 2, 3, 4, 5),
        lat = 13.0354,
        lng = 77.5988
    )
)

// ============================================================
// EXPLORE SCREEN
// ============================================================

@Composable
fun ExploreScreen(
    initialLocation: String? = null,
    onBack: () -> Unit = {},
    onParkingClick: (ExploreParkingPlace) -> Unit = {}
) {

    val context = LocalContext.current

    var searchQuery by remember {
        mutableStateOf(initialLocation ?: "")
    }

    var selectedFilter by remember {
        mutableStateOf("Car")
    }

    var suggestions by remember {
        mutableStateOf<List<LocationSuggestion>>(emptyList())
    }

    var isSearching by remember {
        mutableStateOf(false)
    }

    var searchError by remember {
        mutableStateOf<String?>(null)
    }

    // ========================================================
    // LOCATION SEARCH - SAME STYLE AS HOME
    // ========================================================

    LaunchedEffect(searchQuery) {

        if (searchQuery.trim().length < 2) {

            suggestions = emptyList()
            searchError = null
            isSearching = false

            return@LaunchedEffect
        }

        delay(350)

        val query = searchQuery.trim()

        if (query.isEmpty()) {
            return@LaunchedEffect
        }

        isSearching = true
        searchError = null

        val results = withContext(Dispatchers.IO) {

            try {

                val geocoder = Geocoder(context)

                @Suppress("DEPRECATION")
                val addresses =
                    geocoder.getFromLocationName(
                        query,
                        10
                    )

                addresses
                    ?.mapNotNull { address ->

                        val title =
                            address.featureName
                                ?: address.locality
                                ?: address.subAdminArea
                                ?: return@mapNotNull null

                        val addressText =
                            listOfNotNull(
                                address.subLocality,
                                address.locality,
                                address.adminArea,
                                address.countryName
                            )
                                .distinct()
                                .joinToString(", ")

                        LocationSuggestion(
                            title = title,
                            address = addressText,
                            lat = address.latitude,
                            lng = address.longitude
                        )
                    }
                    ?.filter {
                        it.title.contains(
                            query,
                            ignoreCase = true
                        ) ||
                                it.address.contains(
                                    query,
                                    ignoreCase = true
                                )
                    }
                    ?.distinctBy {
                        "${it.title}|${it.address}"
                    }
                    ?: emptyList()

            } catch (_: Exception) {

                emptyList()
            }
        }

        suggestions = results.take(5)
        isSearching = false

        if (results.isEmpty()) {
            searchError = null
        }
    }

    // ========================================================
    // PARKING FILTER
    // ========================================================

    val filteredParking = remember(searchQuery) {

        if (searchQuery.isBlank()) {

            defaultParkingPlaces

        } else {

            defaultParkingPlaces.filter {

                it.name.contains(
                    searchQuery,
                    ignoreCase = true
                ) ||
                        it.location.contains(
                            searchQuery,
                            ignoreCase = true
                        )
            }
        }
    }

    // ========================================================
    // MAIN UI
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        LazyColumn(

            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 18.dp,
                bottom = 110.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            // ====================================================
            // EXPLORE HEADER
            // ====================================================

            item {
                ScreenHeader(
                    title = "Explore Parking",
                    subtitle = "Find the perfect parking space near you",
                    imageRes = R.drawable.explore_parking_header,
                    imageDescription = "Explore Parking",
                    paddingValues = PaddingValues(start = 2.dp, end = 2.dp, top = 0.dp, bottom = 0.dp),
                    titleColor = Navy,
                    subtitleColor = Gray
                )
            }

            // ====================================================
            // SEARCH BAR
            // ====================================================

            item {

                Column {

                    OutlinedTextField(

                        value =
                            searchQuery,

                        onValueChange = {
                            searchQuery = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        singleLine = true,

                        placeholder = {

                            Text(
                                text = "Where to Park?",
                                color = Gray
                            )
                        },

                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Search,
                                contentDescription =
                                    "Search",
                                tint = Blue
                            )
                        },

                        trailingIcon = {

                            if (isSearching) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        },

                        shape =
                            RoundedCornerShape(18.dp),

                        colors =
                            OutlinedTextFieldDefaults.colors(

                                focusedBorderColor =
                                    Blue,

                                unfocusedBorderColor =
                                    Color(0xFFDDE3ED),

                                focusedContainerColor =
                                    Color.White,

                                unfocusedContainerColor =
                                    Color.White,

                                focusedTextColor =
                                    Navy,

                                unfocusedTextColor =
                                    Navy,

                                cursorColor =
                                    Blue
                            )
                    )

                    // =================================================
                    // LOCATION SUGGESTIONS
                    // =================================================

                    if (suggestions.isNotEmpty()) {

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Column(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(16.dp)
                                    )
                                    .background(
                                        Color.White
                                    )
                        ) {

                            suggestions.forEach { suggestion ->

                                Row(

                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .clickable {

                                                searchQuery =
                                                    suggestion.title

                                                suggestions =
                                                    emptyList()

                                                // ============================
                                                // OPEN GOOGLE MAPS NAVIGATION
                                                // ============================

                                                openGoogleMapsNavigation(
                                                    context =
                                                        context,

                                                    lat =
                                                        suggestion.lat,

                                                    lng =
                                                        suggestion.lng
                                                )
                                            }
                                            .padding(14.dp),

                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Icon(

                                        imageVector =
                                            Icons.Default.LocationOn,

                                        contentDescription =
                                            null,

                                        tint = Blue
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(10.dp)
                                    )

                                    Column {

                                        Text(

                                            text =
                                                suggestion.title,

                                            fontSize =
                                                15.sp,

                                            fontWeight =
                                                FontWeight.SemiBold,

                                            color = Navy
                                        )

                                        if (
                                            suggestion.address.isNotBlank()
                                        ) {

                                            Text(

                                                text =
                                                    suggestion.address,

                                                fontSize =
                                                    12.sp,

                                                color = Gray
                                            )
                                        }
                                    }
                                }

                                HorizontalDivider(
                                    color =
                                        Color(0xFFEEF1F6)
                                )
                            }
                        }
                    }

                    if (searchError != null) {

                        Text(

                            text =
                                searchError!!,

                            color =
                                Color.Red,

                            fontSize =
                                12.sp,

                            modifier =
                                Modifier.padding(
                                    top = 5.dp
                                )
                        )
                    }
                }
            }

            // ====================================================
            // FILTERS
            // ====================================================

            item {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .horizontalScroll(
                                rememberScrollState()
                            ),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    ExploreFilterChip(
                        icon = "🚗",
                        title = "Car",
                        selected =
                            selectedFilter == "Car",
                        onClick = {
                            selectedFilter = "Car"
                        }
                    )

                    ExploreFilterChip(
                        icon = "🏍️",
                        title = "Bike",
                        selected =
                            selectedFilter == "Bike",
                        onClick = {
                            selectedFilter = "Bike"
                        }
                    )

                    ExploreFilterChip(
                        icon = "⚡",
                        title = "EV",
                        selected =
                            selectedFilter == "EV",
                        onClick = {
                            selectedFilter = "EV"
                        }
                    )

                    ExploreFilterChip(
                        icon = "📅",
                        title = "Month",
                        selected =
                            selectedFilter == "Month",
                        onClick = {
                            selectedFilter = "Month"
                        }
                    )
                }
            }

            // ====================================================
            // POPULAR NEAR YOU
            // ====================================================

            item {

                Text(
                    text =
                        "Popular Near You",

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = Navy
                )
            }

            item {

                LazyRow(

                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    items(
                        defaultParkingPlaces.take(5)
                    ) { parking ->

                        PopularParkingCard(

                            parking =
                                parking,

                            onClick = {
                                onParkingClick(
                                    parking
                                )
                            }
                        )
                    }
                }
            }

            // ====================================================
            // NEARBY PARKING
            // ====================================================

            item {

                Text(
                    text =
                        "Nearby Parking",

                    fontSize =
                        21.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color = Navy
                )
            }

            items(
                filteredParking.take(5)
            ) { parking ->

                NearbyParkingCard(

                    parking =
                        parking,

                    onClick = {
                        onParkingClick(
                            parking
                        )
                    }
                )
            }

            // ====================================================
            // ECO BANNER
            // ====================================================

            item {
                EcoParkingBanner()
            }
        }
    }
}

// ============================================================
// GOOGLE MAPS NAVIGATION
// ============================================================

private fun openGoogleMapsNavigation(
    context: Context,
    lat: Double,
    lng: Double
) {

    val googleMapsUri =
        Uri.parse(
            "google.navigation:q=$lat,$lng"
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
                "https://www.google.com/maps/dir/?api=1" +
                        "&destination=$lat,$lng"
            )

        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                browserUri
            )
        )
    }
}

// ============================================================
// FILTER CHIP
// ============================================================

@Composable
private fun ExploreFilterChip(
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .clip(
                    RoundedCornerShape(16.dp)
                )
                .background(

                    if (selected)
                        Color(0xFFE8F1FF)
                    else
                        Color.White
                )
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = 15.dp,
                    vertical = 11.dp
                ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text = icon,
            fontSize = 17.sp
        )

        Spacer(
            modifier =
                Modifier.width(7.dp)
        )

        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight =
                FontWeight.Medium,
            color = Navy
        )
    }
}

// ============================================================
// POPULAR PARKING CARD
// ============================================================

@Composable
private fun PopularParkingCard(
    parking: ExploreParkingPlace,
    onClick: () -> Unit
) {

    Column(

        modifier =
            Modifier
                .width(215.dp)
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(Color.White)
                .clickable {
                    onClick()
                }
    ) {

        Box {

            Image(

                painter =
                    painterResource(
                        id = parking.image
                    ),

                contentDescription =
                    parking.name,

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(125.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        ),

                contentScale =
                    ContentScale.Crop
            )

            Box(

                modifier =
                    Modifier
                        .padding(8.dp)
                        .size(34.dp)
                        .align(
                            Alignment.TopEnd
                        )
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(
                                alpha = 0.92f
                            )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        Icons.Default.FavoriteBorder,

                    contentDescription =
                        "Save",

                    tint = Pink,

                    modifier =
                        Modifier.size(19.dp)
                )
            }
        }

        Column(
            modifier =
                Modifier.padding(12.dp)
        ) {

            Text(
                text =
                    parking.name,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Navy,

                maxLines =
                    1
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    imageVector =
                        Icons.Default.LocationOn,

                    contentDescription =
                        null,

                    tint =
                        Color.Red,

                    modifier =
                        Modifier.size(15.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(2.dp)
                )

                Text(

                    text =
                        parking.location,

                    fontSize =
                        11.sp,

                    color =
                        Gray,

                    maxLines =
                        1
                )
            }

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    imageVector =
                        Icons.Default.Star,

                    contentDescription =
                        null,

                    tint =
                        Yellow,

                    modifier =
                        Modifier.size(15.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(3.dp)
                )

                Text(

                    text =
                        parking.rating,

                    fontSize =
                        12.sp,

                    color =
                        Navy
                )

                Text(

                    text =
                        " • ${parking.slots}",

                    fontSize =
                        11.sp,

                    color =
                        Gray
                )
            }

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(

                text =
                    "₹${parking.hourlyPrice}/hr",

                fontSize =
                    17.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Green
            )
        }
    }
}

// ============================================================
// NEARBY PARKING CARD
// ============================================================

@Composable
private fun NearbyParkingCard(
    parking: ExploreParkingPlace,
    onClick: () -> Unit
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(18.dp)
                )
                .background(
                    Color.White
                )
                .clickable {
                    onClick()
                }
                .padding(11.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Image(

            painter =
                painterResource(
                    id = parking.image
                ),

            contentDescription =
                parking.name,

            modifier =
                Modifier
                    .size(72.dp)
                    .clip(
                        RoundedCornerShape(14.dp)
                    ),

            contentScale =
                ContentScale.Crop
        )

        Spacer(
            modifier =
                Modifier.width(11.dp)
        )

        Column(

            modifier =
                Modifier.weight(1f)
        ) {

            Text(

                text =
                    parking.name,

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Navy,

                maxLines =
                    1
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    imageVector =
                        Icons.Default.LocationOn,

                    contentDescription =
                        null,

                    tint =
                        Color.Red,

                    modifier =
                        Modifier.size(14.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(2.dp)
                )

                Text(

                    text =
                        parking.location,

                    fontSize =
                        11.sp,

                    color =
                        Gray,

                    maxLines =
                        1
                )
            }

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(

                    imageVector =
                        Icons.Default.Star,

                    contentDescription =
                        null,

                    tint =
                        Yellow,

                    modifier =
                        Modifier.size(14.dp)
                )

                Spacer(
                    modifier =
                        Modifier.width(3.dp)
                )

                Text(

                    text =
                        parking.rating,

                    fontSize =
                        11.sp,

                    color =
                        Navy
                )

                Text(

                    text =
                        " • ${parking.slots}",

                    fontSize =
                        11.sp,

                    color =
                        Gray
                )
            }
        }

        Column(
            horizontalAlignment =
                Alignment.End
        ) {

            Text(

                text =
                    "₹${parking.hourlyPrice}/hr",

                fontSize =
                    14.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Green
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Box(

                modifier =
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Color(0xFFEAF2FF)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        Icons.Default.ArrowForwardIos,

                    contentDescription =
                        "Open parking",

                    tint =
                        Blue,

                    modifier =
                        Modifier.size(14.dp)
                )
            }
        }
    }
}

// ============================================================
// ECO BANNER
// ============================================================

@Composable
private fun EcoParkingBanner() {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(20.dp)
                )
                .background(
                    Color(0xFFE8F8F1)
                )
                .padding(16.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(

            modifier =
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        Color(0xFFD3F3E5)
                    ),

            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "🌿",
                fontSize = 23.sp
            )
        }

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column {

            Text(

                text =
                    "Park Green, Drive Clean",

                fontSize =
                    16.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    Navy
            )

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            Text(

                text =
                    "Choose eco-friendly parking for a cleaner tomorrow.",

                fontSize =
                    11.sp,

                color =
                    Gray
            )
        }
    }
}