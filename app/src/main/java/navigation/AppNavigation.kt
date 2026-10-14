package navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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

import ui.screens.AiAssistantScreen
import ui.screens.BookingsScreen
import ui.screens.ConfirmBookingScreen
import ui.screens.DateTimeScreen
import ui.screens.ExploreParkingPlace
import ui.screens.ExploreScreen
import ui.screens.HomeScreen
import ui.screens.MyParkingSpacesScreen
import ui.screens.OwnerParkingManagementScreen
import ui.screens.ParkingBooking
import ui.screens.ParkingDetailsScreen
import ui.screens.PaymentScreen
import ui.screens.ProfileScreen
import ui.screens.QrConfirmationScreen
import ui.screens.SavedScreen
import ui.screens.SlotSelectionScreen
import com.example.parkiq.R
import data.db.AppDatabase
import data.db.ParkingSpaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val AppBackground = Color(0xFF06111F)
private val BottomBarBackground = Color(0xFF15141C)
private val SelectedPill = Color(0xFF302B3B)
private val SelectedColor = Color(0xFFFF4F9A)
private val UnselectedColor = Color(0xFF9B99A5)

// ============================================================
// BOOKING STORE
// ============================================================

private object BookingStore {
    val bookings = mutableListOf<ParkingBooking>()
}

// ============================================================
// APP NAVIGATION
// ============================================================

@Composable
fun AppNavigation() {

    var selectedScreen by remember {
        mutableStateOf("home")
    }

    var bookingSourceScreen by remember {
        mutableStateOf("explore")
    }

    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.parkingSpaceDao() }
    val savedEntities by dao.getAllParkingSpaces().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val existing = dao.getParkingSpaceById("jayanagar_1st_block")
            if (existing == null) {
                dao.insertAll(
                    listOf(
                        ParkingSpaceEntity(
                            id = "jayanagar_1st_block",
                            parkingName = "",
                            location = "",
                            rating = "",
                            availableSlots = "",
                            availableHours = "",
                            hourlyRent = "",
                            parkingType = "",
                            description = "",
                            imageUri = "",
                            drawableResName = "jayanagar_1st_block_parking",
                            hasPhoto = false,
                            updatedAt = System.currentTimeMillis()
                        ),
                        ParkingSpaceEntity(
                            id = "jayanagar_2nd_block",
                            parkingName = "",
                            location = "",
                            rating = "",
                            availableSlots = "",
                            availableHours = "",
                            hourlyRent = "",
                            parkingType = "",
                            description = "",
                            imageUri = "",
                            drawableResName = "jayanagar_2nd_block_parking",
                            hasPhoto = false,
                            updatedAt = System.currentTimeMillis()
                        ),
                        ParkingSpaceEntity(
                            id = "jain_university",
                            parkingName = "",
                            location = "",
                            rating = "",
                            availableSlots = "",
                            availableHours = "",
                            hourlyRent = "",
                            parkingType = "",
                            description = "",
                            imageUri = "",
                            drawableResName = "jain_university_area_parking",
                            hasPhoto = false,
                            updatedAt = System.currentTimeMillis()
                        ),
                        ParkingSpaceEntity(
                            id = "btm_central",
                            parkingName = "",
                            location = "",
                            rating = "",
                            availableSlots = "",
                            availableHours = "",
                            hourlyRent = "",
                            parkingType = "",
                            description = "",
                            imageUri = "",
                            drawableResName = "btm_central_parking",
                            hasPhoto = false,
                            updatedAt = System.currentTimeMillis()
                        ),
                        ParkingSpaceEntity(
                            id = "btm_metro",
                            parkingName = "",
                            location = "",
                            rating = "",
                            availableSlots = "",
                            availableHours = "",
                            hourlyRent = "",
                            parkingType = "",
                            description = "",
                            imageUri = "",
                            drawableResName = "btm_metro_side_parking",
                            hasPhoto = false,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                )
            }
        }
    }

    val displayRentalSpaces = remember(savedEntities) {
        if (savedEntities.isNotEmpty()) {
            savedEntities.map { entity ->
                if (entity.hasPhoto) {
                    ExploreParkingPlace(
                        name = entity.parkingName.ifBlank { getInitialNameForId(entity.id) },
                        location = entity.location.ifBlank { getLocationForSpace(entity.id) },
                        hourlyPrice = entity.hourlyRent.replace("₹", "").replace("/hour", "").replace("/hr", "").trim().toIntOrNull() ?: 40,
                        rating = entity.rating.ifBlank { "4.8" },
                        slots = entity.availableSlots.ifBlank { "Available" },
                        image = getDrawableForId(entity.id),
                        availableDurations = listOf(1, 2, 3, 4)
                    )
                } else {
                    getDefaultParkingForId(entity.id)
                }
            }
        } else {
            defaultRentalSpacesList()
        }
    }

    var selectedOwnerParkingId by remember {
        mutableStateOf<String?>(null)
    }

    var selectedParking by remember {
        mutableStateOf<ExploreParkingPlace?>(null)
    }

    var selectedLocation by remember {
        mutableStateOf<String?>(null)
    }

    var selectedSlot by remember {
        mutableStateOf("")
    }

    var selectedDate by remember {
        mutableStateOf("")
    }

    var selectedTime by remember {
        mutableStateOf("")
    }

    var selectedDuration by remember {
        mutableStateOf(1)
    }

    var pendingBooking by remember {
        mutableStateOf<ParkingBooking?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            when (selectedScreen) {

                // ====================================================
                // HOME
                // ====================================================

                "home" -> {

                    HomeScreen(
                        rentalSpaces = displayRentalSpaces,

                        onNavigate = { destination ->
                            selectedScreen = destination
                        },

                        onLocationSelected = { location ->
                            selectedLocation = location
                            selectedScreen = "explore"
                        },

                        onRentalParkingClick = { parking ->
                            selectedOwnerParkingId = getIdForName(parking.name)
                            selectedScreen = "owner_management"
                        }
                    )
                }

                // ====================================================
                // MY PARKING SPACES (RENTAL SPACES LISTING)
                // ====================================================

                "my_parking_spaces" -> {
                    MyParkingSpacesScreen(
                        rentalSpaces = displayRentalSpaces,
                        savedEntities = savedEntities,
                        onBack = {
                            selectedScreen = "home"
                        },
                        onManageClick = { spaceId ->
                            selectedOwnerParkingId = spaceId
                            selectedScreen = "owner_management"
                        }
                    )
                }

                // ====================================================
                // OWNER PARKING MANAGEMENT (CAMERAX + AI + ROOM DB)
                // ====================================================

                "owner_management" -> {

                    selectedOwnerParkingId?.let { spaceId ->

                        val entity = savedEntities.find { it.id == spaceId }

                        OwnerParkingManagementScreen(

                            parkingId = spaceId,

                            parkingNameInitial = getInitialNameForId(spaceId),

                            drawableRes = getDrawableForId(spaceId),

                            existingEntity = entity,

                            onBack = {
                                selectedScreen = "my_parking_spaces"
                            },

                            onSaveSuccess = {
                                selectedScreen = "my_parking_spaces"
                            }
                        )
                    }
                }

                // ====================================================
                // EXPLORE
                // ====================================================

                "explore" -> {

                    ExploreScreen(
                        initialLocation = selectedLocation,

                        onBack = {
                            selectedLocation = null
                            selectedScreen = "home"
                        },

                        onParkingClick = { parking ->

                            bookingSourceScreen = "explore"
                            selectedParking = parking

                            selectedSlot = ""
                            selectedDate = ""
                            selectedTime = ""
                            selectedDuration = 1

                            selectedScreen = "details"
                        }
                    )
                }

                // ====================================================
                // PARKING DETAILS
                // ====================================================

                "details" -> {

                    selectedParking?.let { parking ->

                        ParkingDetailsScreen(

                            parking = parking.name,

                            location = parking.location,

                            price = parking.hourlyPrice.toString(),

                            rating = parking.rating,

                            slots = parking.slots,

                            image = parking.image,

                            bookedSlots = BookingStore.bookings
                                .filter {
                                    it.parkingName == parking.name
                                }
                                .map {
                                    it.slot
                                }
                                .toSet(),

                            onBack = {
                                selectedScreen = bookingSourceScreen
                            },

                            onBookNow = { slot ->

                                selectedSlot = slot
                                selectedDate = ""
                                selectedTime = ""
                                selectedDuration = 1

                                selectedScreen = "date_time"
                            },

                            onSlotSelected = { slot ->

                                selectedSlot = slot
                                selectedDate = ""
                                selectedTime = ""
                                selectedDuration = 1

                                selectedScreen = "date_time"
                            }
                        )
                    }
                }

                // ====================================================
                // PARKING SLOT
                // ====================================================

                "slot_selection" -> {

                    selectedParking?.let { parking ->

                        SlotSelectionScreen(

                            parkingName = parking.name,

                            bookedSlots = BookingStore.bookings
                                .filter {
                                    it.parkingName == parking.name
                                }
                                .map {
                                    it.slot
                                }
                                .toSet(),

                            onBack = {
                                selectedScreen = bookingSourceScreen
                            },

                            onSlotSelected = { slot ->

                                selectedSlot = slot

                                selectedScreen = "date_time"
                            }
                        )
                    }
                }

                // ====================================================
                // DATE + TIME + DURATION
                // ====================================================

                "date_time" -> {

                    selectedParking?.let { parking ->

                        DateTimeScreen(

                            parkingName = parking.name,

                            slot = selectedSlot,

                            onBack = {
                                selectedScreen = bookingSourceScreen
                            },

                            onContinue = {
                                    date,
                                    time,
                                    duration ->

                                selectedDate = date
                                selectedTime = time
                                selectedDuration = duration

                                selectedScreen = "confirm_booking"
                            }
                        )
                    }
                }

                // ====================================================
                // CONFIRM BOOKING
                // ====================================================

                "confirm_booking" -> {

                    selectedParking?.let { parking ->

                        val total =
                            parking.hourlyPrice * selectedDuration

                        ConfirmBookingScreen(

                            parkingName = parking.name,

                            location = parking.location,

                            totalPrice = "₹$total",

                            hourlyPrice = parking.hourlyPrice,

                            duration = selectedDuration,

                            slot = selectedSlot,

                            date = selectedDate,

                            time = selectedTime,

                            onBack = {
                                selectedScreen = bookingSourceScreen
                            },

                            onConfirm = {

                                val newBooking =
                                    ParkingBooking(

                                        parkingName = parking.name,

                                        location = parking.location,

                                        vehicle = "🚗 My Vehicle",

                                        slot = selectedSlot,

                                        date = selectedDate,

                                        time = selectedTime,

                                        status = "Pending Payment",

                                        image = parking.image,

                                        lat = parking.lat,

                                        lng = parking.lng,

                                        duration =
                                            if (
                                                selectedDuration == 1
                                            ) {
                                                "1 Hour"
                                            } else {
                                                "$selectedDuration Hours"
                                            },

                                        totalPrice = "₹$total"
                                    )

                                BookingStore.bookings.removeAll {

                                    it.parkingName ==
                                            newBooking.parkingName &&
                                            it.slot ==
                                            newBooking.slot &&
                                            it.date ==
                                            newBooking.date &&
                                            it.time ==
                                            newBooking.time
                                }

                                BookingStore.bookings.add(
                                    newBooking
                                )

                                pendingBooking =
                                    newBooking

                                selectedScreen =
                                    "payment"
                            }
                        )
                    }
                }

                // ====================================================
                // PAYMENT
                // ====================================================

                "payment" -> {

                    pendingBooking?.let { booking ->

                        PaymentScreen(

                            parkingName =
                                booking.parkingName,

                            amount =
                                booking.totalPrice,

                            onBack = {
                                selectedScreen = bookingSourceScreen
                            },

                            onPaymentSuccess = {

                                val confirmedBooking =
                                    booking.copy(
                                        status = "Confirmed"
                                    )

                                BookingStore.bookings.replaceAll {

                                    if (
                                        it.parkingName ==
                                        booking.parkingName &&
                                        it.slot ==
                                        booking.slot &&
                                        it.date ==
                                        booking.date &&
                                        it.time ==
                                        booking.time
                                    ) {
                                        confirmedBooking
                                    } else {
                                        it
                                    }
                                }

                                pendingBooking =
                                    confirmedBooking

                                selectedScreen =
                                    "qr"
                            }
                        )
                    }
                }

                // ====================================================
                // BOOKING CONFIRMED
                // ====================================================

                "qr" -> {

                    pendingBooking?.let { booking ->

                        QrConfirmationScreen(

                            parkingName =
                                booking.parkingName,

                            slot =
                                booking.slot,

                            date =
                                booking.date,

                            time =
                                booking.time,

                            duration =
                                booking.duration,

                            onDone = {
                                selectedScreen =
                                    "bookings"
                            }
                        )
                    }
                }

                // ====================================================
                // SAVED
                // ====================================================

                "saved" -> {

                    SavedScreen(

                        bookings =
                            BookingStore.bookings.toList(),

                        onBack = {
                            selectedScreen =
                                "profile"
                        },

                        onParkingClick = { parking ->

                            bookingSourceScreen = "explore"
                            selectedParking =
                                defaultParkingForSaved(
                                    parking
                                )

                            selectedSlot = ""
                            selectedDate = ""
                            selectedTime = ""
                            selectedDuration = 1

                            selectedScreen =
                                "details"
                        }
                    )
                }

                // ====================================================
                // MY BOOKINGS
                // ====================================================

                "bookings" -> {

                    BookingsScreen(

                        bookings =
                            BookingStore.bookings.toList(),

                        onBack = {
                            selectedScreen =
                                "profile"
                        },

                        onBookingClick = {
                            // Existing booking click
                        }
                    )
                }

                // ====================================================
                // PROFILE
                // ====================================================

                "profile" -> {

                    ProfileScreen(

                        rentalSpaces = displayRentalSpaces,

                        bookings =
                            BookingStore.bookings.toList(),

                        onBookingsClick = {

                            selectedScreen =
                                "bookings"
                        },

                        onSavedClick = {

                            selectedScreen =
                                "saved"
                        },

                        onSettingsClick = {
                            // handled inside ProfileScreen
                        },

                        onOwnerClick = {
                            selectedScreen = "my_parking_spaces"
                        },

                        onRentalParkingClick = { parking ->
                            selectedOwnerParkingId = getIdForName(parking.name)
                            selectedScreen = "owner_management"
                        }
                    )
                }

                // ====================================================
                // AI ASSISTANT
                // ====================================================

                "ai" -> {

                    AiAssistantScreen(

                        onBack = {

                            selectedScreen =
                                "home"
                        }
                    )
                }

                else -> {

                    selectedScreen =
                        "home"
                }
            }
        }

        // ========================================================
        // BOTTOM BAR
        // ========================================================

        if (
            selectedScreen == "home" ||
            selectedScreen == "explore" ||
            selectedScreen == "ai" ||
            selectedScreen == "profile"
        ) {

            BottomNavigationBar(

                selectedScreen =
                    selectedScreen,

                onScreenSelected = { screen ->

                    selectedLocation = null

                    selectedScreen =
                        screen
                }
            )
        }
    }
}

private fun getLocationForSpace(id: String): String {
    return when (id) {
        "jayanagar_1st_block" -> "Jayanagar 1st Block, Bengaluru"
        "jayanagar_2nd_block" -> "Jayanagar 2nd Block, Bengaluru"
        "jain_university" -> "Jayanagar / V.V. Puram, Bengaluru"
        "btm_central" -> "BTM 2nd Stage, Bengaluru"
        "btm_metro" -> "BTM Layout, Bengaluru"
        else -> "Bengaluru"
    }
}

private fun getDrawableForId(id: String): Int {
    return when (id) {
        "jayanagar_1st_block" -> R.drawable.jayanagar_1st_block_parking
        "jayanagar_2nd_block" -> R.drawable.jayanagar_2nd_block_parking
        "jain_university" -> R.drawable.jain_university_area_parking
        "btm_central" -> R.drawable.btm_central_parking
        "btm_metro" -> R.drawable.btm_metro_side_parking
        else -> R.drawable.jayanagar_1st_block_parking
    }
}

private fun getInitialNameForId(id: String): String {
    return when (id) {
        "jayanagar_1st_block" -> "Jayanagar 1st Block Parking"
        "jayanagar_2nd_block" -> "Jayanagar 2nd Block Parking"
        "jain_university" -> "Jain University Area Parking"
        "btm_central" -> "BTM Central Parking"
        "btm_metro" -> "BTM Metro Side Parking"
        else -> "Parking Space"
    }
}

private fun getIdForName(name: String): String {
    return when {
        name.contains("1st Block", ignoreCase = true) -> "jayanagar_1st_block"
        name.contains("2nd Block", ignoreCase = true) -> "jayanagar_2nd_block"
        name.contains("Jain", ignoreCase = true) -> "jain_university"
        name.contains("BTM Central", ignoreCase = true) -> "btm_central"
        name.contains("BTM Metro", ignoreCase = true) -> "btm_metro"
        else -> "jayanagar_1st_block"
    }
}

private fun getDefaultParkingForId(id: String): ExploreParkingPlace {
    return when (id) {
        "jayanagar_1st_block" -> ExploreParkingPlace(
            name = "Jayanagar 1st Block Parking",
            location = "Jayanagar 1st Block, Bengaluru",
            hourlyPrice = 40,
            rating = "4.8",
            slots = "8 slots",
            image = R.drawable.jayanagar_1st_block_parking,
            availableDurations = listOf(1, 2, 3, 4)
        )
        "jayanagar_2nd_block" -> ExploreParkingPlace(
            name = "Jayanagar 2nd Block Parking",
            location = "Jayanagar 2nd Block, Bengaluru",
            hourlyPrice = 45,
            rating = "4.7",
            slots = "6 slots",
            image = R.drawable.jayanagar_2nd_block_parking,
            availableDurations = listOf(1, 2, 3, 4)
        )
        "jain_university" -> ExploreParkingPlace(
            name = "Jain University Area Parking",
            location = "Jayanagar / V.V. Puram, Bengaluru",
            hourlyPrice = 50,
            rating = "4.9",
            slots = "10 slots",
            image = R.drawable.jain_university_area_parking,
            availableDurations = listOf(1, 2, 3, 4)
        )
        "btm_central" -> ExploreParkingPlace(
            name = "BTM Central Parking",
            location = "BTM 2nd Stage, Bengaluru",
            hourlyPrice = 35,
            rating = "4.6",
            slots = "5 slots",
            image = R.drawable.btm_central_parking,
            availableDurations = listOf(1, 2, 3, 4)
        )
        "btm_metro" -> ExploreParkingPlace(
            name = "BTM Metro Side Parking",
            location = "BTM Layout, Bengaluru",
            hourlyPrice = 45,
            rating = "4.8",
            slots = "12 slots",
            image = R.drawable.btm_metro_side_parking,
            availableDurations = listOf(1, 2, 3, 4)
        )
        else -> ExploreParkingPlace(
            name = "Jayanagar 1st Block Parking",
            location = "Jayanagar 1st Block, Bengaluru",
            hourlyPrice = 40,
            rating = "4.8",
            slots = "8 slots",
            image = R.drawable.jayanagar_1st_block_parking,
            availableDurations = listOf(1, 2, 3, 4)
        )
    }
}

private fun defaultRentalSpacesList(): List<ExploreParkingPlace> {
    return listOf(
        getDefaultParkingForId("jayanagar_1st_block"),
        getDefaultParkingForId("jayanagar_2nd_block"),
        getDefaultParkingForId("jain_university"),
        getDefaultParkingForId("btm_central"),
        getDefaultParkingForId("btm_metro")
    )
}

// ============================================================
// SAVED PARKING -> EXPLORE PARKING
// ============================================================

private fun defaultParkingForSaved(
    parking: ui.screens.SavedParking
): ExploreParkingPlace {

    return ExploreParkingPlace(

        name =
            parking.name,

        location =
            parking.location,

        hourlyPrice =
            parking.price
                .replace("₹", "")
                .replace("/hr", "")
                .trim()
                .toIntOrNull()
                ?: 40,

        rating =
            parking.rating,

        slots =
            "Available",

        image =
            parking.image,

        availableDurations =
            listOf(
                1,
                2,
                3
            )
    )
}

// ============================================================
// BOTTOM NAVIGATION
// ============================================================

@Composable
private fun BottomNavigationBar(
    selectedScreen: String,
    onScreenSelected: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                BottomBarBackground
            )
            .padding(
                horizontal = 5.dp,
                vertical = 5.dp
            ),

        horizontalArrangement =
            Arrangement.SpaceEvenly,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        BottomNavigationItem(
            icon = "⌂",
            label = "Home",
            selected =
                selectedScreen == "home",

            onClick = {
                onScreenSelected("home")
            }
        )

        BottomNavigationItem(
            icon = "⌕",
            label = "Explore",
            selected =
                selectedScreen == "explore",

            onClick = {
                onScreenSelected("explore")
            }
        )

        BottomNavigationItem(
            icon = "🤖",
            label = "AI Assistant",
            selected =
                selectedScreen == "ai",

            onClick = {
                onScreenSelected("ai")
            }
        )

        BottomNavigationItem(
            icon = "♟",
            label = "Profile",
            selected =
                selectedScreen == "profile",

            onClick = {
                onScreenSelected("profile")
            }
        )
    }
}

// ============================================================
// BOTTOM NAV ITEM
// ============================================================

@Composable
private fun BottomNavigationItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .size(
                width = 88.dp,
                height = 64.dp
            )
            .background(
                if (selected) {
                    SelectedPill
                } else {
                    Color.Transparent
                },

                RoundedCornerShape(14.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                vertical = 5.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = icon,

            color =
                if (selected) {
                    SelectedColor
                } else {
                    UnselectedColor
                },

            fontSize = 20.sp,

            fontWeight =
                FontWeight.Bold
        )

        Text(
            text = label,

            color =
                if (selected) {
                    SelectedColor
                } else {
                    UnselectedColor
                },

            fontSize = 9.sp,

            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                }
        )
    }
}
