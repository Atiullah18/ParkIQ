package ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.parkiq.R
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import data.db.AiChatMessageEntity
import data.db.AppDatabase
import data.db.ParkingSpaceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val Background = Color(0xFFF7F9FC)

private val Navy = Color(0xFF10203A)
private val Gray = Color(0xFF687386)

private val DashboardBackground = Color(0xFF06111F)
private val DashboardCard = Color(0xFF102337)
private val DashboardBlue = Color(0xFF162D43)

private val Green = Color(0xFF00E676)
private val Pink = Color(0xFFFF4F9A)

private val EditBackground = Color(0xFFF5F7FA)
private val EditHeader = Color(0xFF2E3A59)

// ============================================================
// BANGALORE AREAS
// ============================================================

private val bangaloreAreas = listOf(
    "Indiranagar", "BTM Layout", "Jayanagar", "Koramangala", "HSR Layout",
    "JP Nagar", "Banashankari", "Whitefield", "Marathahalli", "Bellandur",
    "Electronic City", "MG Road", "Hebbal", "Yelahanka", "Rajajinagar",
    "Malleshwaram", "Vijayanagar", "CV Raman Nagar", "Frazer Town", "Shivajinagar", "Ulsoor"
)

// ============================================================
// PARKIQ DATABASE GROUNDING QUERY
// ============================================================

private fun queryParkIQDatabase(
    queryText: String,
    selectedLoc: String? = null,
    selectedDate: String? = null,
    selectedTime: String? = null,
    dbEntities: List<ParkingSpaceEntity> = emptyList()
): String? {
    val lowerQuery = queryText.lowercase()

    // 0. Filter normal greetings so Gemini responds naturally without searching parking database
    val cleanQuery = lowerQuery.replace(Regex("[^a-z0-9\\s]"), "").trim()
    val greetingsList = listOf(
        "hi", "hello", "hey", "hi there", "hello there", "hey there",
        "good morning", "good afternoon", "good evening", "good day",
        "howdy", "greetings", "sup", "whats up", "what's up", "how are you", "hola"
    )
    val isGreeting = greetingsList.any { cleanQuery == it || cleanQuery.startsWith("$it ") || cleanQuery.endsWith(" $it") }
    val hasExplicitParkingKeyword = listOf("parking", "slot", "space", "book", "find", "available", "rent", "rate", "price", "fee", "cost").any { cleanQuery.contains(it) }
    val hasAreaKeyword = bangaloreAreas.any { area -> cleanQuery.contains(area.lowercase()) }

    if (isGreeting && !hasExplicitParkingKeyword && !hasAreaKeyword) {
        return null // Let Gemini handle normal greetings naturally
    }

    // 1. Check if non-Bangalore location
    val nonBangaloreCities = listOf("mumbai", "delhi", "chennai", "hyderabad", "pune", "kolkata", "ahmedabad", "jaipur", "london", "new york", "dubai", "california")
    if (nonBangaloreCities.any { lowerQuery.contains(it) } || (selectedLoc != null && nonBangaloreCities.any { selectedLoc.lowercase().contains(it) })) {
        return "ParkIQ parking availability currently supports Bangalore only."
    }

    // 2. Identify target area
    val targetArea = selectedLoc ?: bangaloreAreas.firstOrNull { area ->
        lowerQuery.contains(area.lowercase()) ||
        (area == "BTM Layout" && (lowerQuery.contains("btm") || lowerQuery.contains("btm layout"))) ||
        (area == "Jayanagar" && lowerQuery.contains("jayanagar")) ||
        (area == "MG Road" && (lowerQuery.contains("mg road") || lowerQuery.contains("mgroad")))
    } ?: if (lowerQuery.contains("parking") || lowerQuery.contains("available") || lowerQuery.contains("slot")) {
        null
    } else {
        null
    }

    if (targetArea == null) {
        return null // Let Gemini handle general questions
    }

    // 3. Search in ParkIQ Database (defaultParkingPlaces + Room DB)
    val allSpaces = defaultParkingPlaces + dbEntities.map { entity ->
        ExploreParkingPlace(
            name = entity.parkingName.ifBlank { "Parking Space" },
            location = entity.location.ifBlank { "Bengaluru" },
            hourlyPrice = entity.hourlyRent.replace("₹", "").replace("/hour", "").replace("/hr", "").trim().toIntOrNull() ?: 40,
            rating = entity.rating.ifBlank { "4.8" },
            slots = entity.availableSlots.ifBlank { "Available" },
            image = R.drawable.jayanagar_1st_block_parking,
            availableDurations = listOf(1, 2, 3)
        )
    }

    val matches = allSpaces.filter { space ->
        space.location.contains(targetArea, ignoreCase = true) ||
        space.name.contains(targetArea, ignoreCase = true) ||
        (targetArea.equals("BTM Layout", ignoreCase = true) && space.location.contains("BTM", ignoreCase = true)) ||
        (targetArea.equals("Jayanagar", ignoreCase = true) && space.location.contains("Jayanagar", ignoreCase = true))
    }

    val dateInfo = if (!selectedDate.isNullOrBlank()) " on $selectedDate" else ""
    val timeInfo = if (!selectedTime.isNullOrBlank()) " ($selectedTime)" else ""

    if (matches.isEmpty()) {
        return "No parking areas found in this location in ParkIQ."
    }

    val sb = StringBuilder()
    sb.append("Parking availability in $targetArea$dateInfo$timeInfo:\n\n")
    matches.forEach { space ->
        val slotsText = if (space.slots.contains("slot", ignoreCase = true)) space.slots else "${space.slots} available"
        sb.append("🅿️ ${space.name} — $slotsText\n")
    }

    return sb.toString().trim()
}

// ============================================================
// USER PROFILE
// ============================================================

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val mobile: String,
    val email: String,
    val birthday: String,
    val gender: String,
    val hasPhoto: Boolean = false
)

// ============================================================
// PROFILE SCREEN
// ============================================================

@Composable
fun ProfileScreen(
    rentalSpaces: List<ExploreParkingPlace> = emptyList(),
    bookings: List<ParkingBooking> = emptyList(),
    onBookingsClick: () -> Unit = {},
    onSavedClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onOwnerClick: () -> Unit = {},
    onRentalParkingClick: (ExploreParkingPlace) -> Unit = {}
) {

    var userProfile by remember {
        mutableStateOf(
            UserProfile(
                firstName = "Md",
                lastName = "Atiullah",
                mobile = "+91 9142281082",
                email = "atiullah@parkiq.com",
                birthday = "20 Aug 2000",
                gender = "Man"
            )
        )
    }

    var page by remember {
        mutableStateOf("profile")
    }

    var editingProfile by remember {
        mutableStateOf(false)
    }

    if (editingProfile) {
        EditProfileScreen(
            currentProfile = userProfile,
            onBack = { editingProfile = false },
            onSave = {
                userProfile = it
                editingProfile = false
            }
        )
        return
    }

    when (page) {
        "settings" -> {
            SettingsScreen(onBack = { page = "profile" })
            return
        }

        "owner" -> {
            OwnerDashboardScreen(
                rentalSpaces = rentalSpaces,
                onBack = { page = "profile" },
                onParkingClick = { parking -> onRentalParkingClick(parking) }
            )
            return
        }

        "rewards" -> {
            RewardsScreen(onBack = { page = "profile" })
            return
        }

        "help" -> {
            HelpSupportScreen(onBack = { page = "profile" })
            return
        }

        "feedback" -> {
            FeedbackScreen(onBack = { page = "profile" })
            return
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        ScreenHeader(
            title = "My Profile",
            subtitle = "Manage your account with ease",
            imageRes = R.drawable.profile_parking_illustration,
            imageDescription = "Profile illustration",
            titleColor = Navy,
            subtitleColor = Gray
        )

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9AADE0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Md",
                            color = Color.White,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${userProfile.firstName} ${userProfile.lastName}",
                            color = Navy,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = userProfile.mobile, color = Gray, fontSize = 10.sp)
                        Text(text = userProfile.email, color = Gray, fontSize = 10.sp)
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFEAF3FF), RoundedCornerShape(10.dp))
                            .clickable { editingProfile = true }
                            .padding(horizontal = 11.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "✎ Edit",
                            color = Color(0xFF2878F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE8ECF2))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat(icon = "♥", value = "3", label = "Saved Spots", iconColor = Pink)
                    ProfileStat(icon = "▣", value = bookings.size.toString(), label = "Total Bookings", iconColor = Color(0xFF2878F0))
                    ProfileStat(icon = "★", value = "250", label = "Reward Points", iconColor = Color(0xFFFFB900))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ProfileMenuItem(icon = "🎫", title = "My Bookings", subtitle = "View and manage your reservations") { onBookingsClick() }
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(icon = "♥", title = "Saved Parking", subtitle = "Your favourite parking spots") { onSavedClick() }
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(icon = "🏆", title = "ParkIQ Rewards", subtitle = "View your points and offers") { page = "rewards" }
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(icon = "⚙", title = "Settings", subtitle = "App preferences") { onSettingsClick(); page = "settings" }
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(icon = "🏢", title = "Parking Owner", subtitle = "List and manage your parking space") { onOwnerClick(); page = "owner" }
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(icon = "?", title = "Help & Support", subtitle = "FAQs and get help") { page = "help" }
            Spacer(modifier = Modifier.height(8.dp))
            ProfileMenuItem(icon = "▤", title = "Give Feedback", subtitle = "Help us improve ParkIQ") { page = "feedback" }
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFEEF4), RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(vertical = 11.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "⇥", color = Pink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Logout", color = Pink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

// ============================================================
// OWNER DASHBOARD
// ============================================================

@Composable
private fun OwnerDashboardScreen(
    rentalSpaces: List<ExploreParkingPlace>,
    onBack: () -> Unit,
    onParkingClick: (ExploreParkingPlace) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
    ) {
        ProfileSubpageHeader(title = "Parking Owner", onBack = onBack)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Your 5 Rental Parking Spaces", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Select any parking space to manage with Live CameraX & AI Analysis.", color = Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        rentalSpaces.forEach { space ->
            OwnerParkingSpaceItem(parking = space, onClick = { onParkingClick(space) })
            Spacer(modifier = Modifier.height(10.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun OwnerParkingSpaceItem(
    parking: ExploreParkingPlace,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DashboardCard, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = parking.image),
            contentDescription = parking.name,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = parking.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = "📍 ${parking.location}", color = Gray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "₹${parking.hourlyPrice}/hr • ${parking.slots}", color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = "AI Flow ›", color = Green, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ============================================================
// PROFILE SUBPAGE HEADER
// ============================================================

@Composable
private fun ProfileSubpageHeader(title: String, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(DashboardBackground)) {
        Box(
            modifier = Modifier
                .padding(start = 20.dp, top = 22.dp, bottom = 8.dp)
                .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                .clickable { onBack() },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = "‹ Back", color = Green, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = title, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 20.dp, bottom = 12.dp))
    }
}

// ============================================================
// EDIT PROFILE, SETTINGS, REWARDS, HELP, FEEDBACK
// ============================================================

@Composable
private fun EditProfileScreen(currentProfile: UserProfile, onBack: () -> Unit, onSave: (UserProfile) -> Unit) {
    var firstName by remember { mutableStateOf(currentProfile.firstName) }
    var lastName by remember { mutableStateOf(currentProfile.lastName) }
    var mobile by remember { mutableStateOf(currentProfile.mobile) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var birthday by remember { mutableStateOf(currentProfile.birthday) }
    var gender by remember { mutableStateOf(currentProfile.gender) }

    Column(modifier = Modifier.fillMaxSize().background(EditBackground).verticalScroll(rememberScrollState())) {
        ProfileSubpageHeader(title = "Edit Profile", onBack = onBack)
        Column(modifier = Modifier.padding(18.dp)) {
            EditField(label = "First Name", value = firstName, onValueChange = { firstName = it })
            EditField(label = "Last Name", value = lastName, onValueChange = { lastName = it })
            EditField(label = "Mobile Number", value = mobile, onValueChange = { mobile = it })
            EditField(label = "Email Address", value = email, onValueChange = { email = it })
            EditField(label = "Birthday", value = birthday, onValueChange = { birthday = it })
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onSave(UserProfile(firstName, lastName, mobile, email, birthday, gender)) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = Pink)) {
                Text(text = "Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Text(text = label, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 12.dp))
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth().padding(top = 5.dp), singleLine = true, shape = RoundedCornerShape(9.dp))
}

@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }
    var biometricEnabled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(DashboardBackground).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ProfileSubpageHeader(title = "Settings", onBack = onBack)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "App Preferences", color = Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(title = "Push Notifications", subtitle = "Receive booking updates and alerts", checked = notificationsEnabled, onCheckedChange = { notificationsEnabled = it })
        Spacer(modifier = Modifier.height(10.dp))
        SettingsToggleItem(title = "Location Services", subtitle = "Find nearby parking automatically", checked = locationEnabled, onCheckedChange = { locationEnabled = it })
        Spacer(modifier = Modifier.height(10.dp))
        SettingsToggleItem(title = "Dark Theme", subtitle = "Use ParkIQ dark mode interface", checked = darkModeEnabled, onCheckedChange = { darkModeEnabled = it })

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Security & Privacy", color = Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        SettingsToggleItem(title = "Biometric Lock", subtitle = "Secure app access with fingerprint/face ID", checked = biometricEnabled, onCheckedChange = { biometricEnabled = it })
        Spacer(modifier = Modifier.height(10.dp))
        SettingsActionItem(title = "Privacy Policy", subtitle = "Read our data & security terms", onClick = {})
        Spacer(modifier = Modifier.height(10.dp))
        SettingsActionItem(title = "Terms of Service", subtitle = "User agreement and booking policies", onClick = {})

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "About ParkIQ", color = Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        SettingsActionItem(title = "App Version", subtitle = "v2.4.0 (Build 420 • Stable)", onClick = {})
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun SettingsToggleItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DashboardCard, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = Gray, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = Green,
                uncheckedThumbColor = Gray,
                uncheckedTrackColor = DashboardBlue
            )
        )
    }
}

@Composable
private fun SettingsActionItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DashboardCard, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = Gray, fontSize = 11.sp)
        }
        Text(text = "›", color = Gray, fontSize = 20.sp)
    }
}

@Composable
private fun RewardsScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DashboardBackground).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ProfileSubpageHeader(title = "ParkIQ Rewards", onBack = onBack)
        Spacer(modifier = Modifier.height(10.dp))

        // Points Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DashboardCard, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(text = "AVAILABLE BALANCE", color = Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "⭐ 250 ParkIQ Points", color = Green, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Gold Member • 50 points to Platinum Tier", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Redeemable Rewards & Offers", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        RewardItemCard(title = "Free 1 Hour Parking", points = "50 Points", description = "Valid at all Bangalore partner parking spots")
        Spacer(modifier = Modifier.height(10.dp))
        RewardItemCard(title = "20% Off Weekend Booking", points = "100 Points", description = "Applicable on Saturday & Sunday bookings")
        Spacer(modifier = Modifier.height(10.dp))
        RewardItemCard(title = "Priority VIP Slot Reservation", points = "150 Points", description = "Guaranteed slot during peak rush hours")
        Spacer(modifier = Modifier.height(10.dp))
        RewardItemCard(title = "Free Car Wash Add-on", points = "200 Points", description = "Available at select corporate parking garages")
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun RewardItemCard(title: String, points: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DashboardCard, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = description, color = Gray, fontSize = 11.sp)
        }
        Box(
            modifier = Modifier
                .background(DashboardBlue, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = points, color = Green, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HelpSupportScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DashboardBackground).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ProfileSubpageHeader(title = "Help & Support", onBack = onBack)
        Spacer(modifier = Modifier.height(10.dp))

        // Support Contact Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DashboardCard, RoundedCornerShape(18.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(text = "24/7 Customer Assistance", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Need help with a parking booking, payment, or owner space? Our team is always here for you.", color = Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Green)) {
                    Text(text = "📞 Call Support / Live Chat", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Frequently Asked Questions", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))

        FaqItem(question = "How do I book a parking spot?", answer = "Go to Explore, select your location, pick a slot and duration, and confirm payment.")
        Spacer(modifier = Modifier.height(10.dp))
        FaqItem(question = "How does CameraX & AI Analysis work?", answer = "As a parking owner, open My Parking Spaces, select your space, and use live AI scanning to detect occupancy and update slot availability.")
        Spacer(modifier = Modifier.height(10.dp))
        FaqItem(question = "What is the cancellation policy?", answer = "You can cancel any booking up to 15 minutes before the start time for a full refund.")
        Spacer(modifier = Modifier.height(10.dp))
        FaqItem(question = "How do ParkIQ points and rewards work?", answer = "Earn points on every booking and rental space management, then redeem them for free parking hours and discounts.")
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DashboardCard, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(text = question, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = answer, color = Gray, fontSize = 11.sp)
    }
}

@Composable
private fun FeedbackScreen(onBack: () -> Unit) {
    var feedback by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().background(DashboardBackground).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
        ProfileSubpageHeader(title = "Give Feedback", onBack = onBack)
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(value = feedback, onValueChange = { feedback = it }, modifier = Modifier.fillMaxWidth().height(140.dp), placeholder = { Text("Write feedback...", color = Gray) })
        Spacer(modifier = Modifier.height(15.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Green)) {
            Text(text = "Submit Feedback", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProfileStat(icon: String, value: String, label: String, iconColor: Color) {
    Column(modifier = Modifier.width(82.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(34.dp).clip(CircleShape).background(iconColor.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
            Text(text = icon, color = iconColor, fontSize = 15.sp)
        }
        Text(text = value, color = Navy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Gray, fontSize = 8.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ProfileMenuItem(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(13.dp)).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).background(Color(0xFFEAF3FF), RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Text(text = icon, fontSize = 15.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Navy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Gray, fontSize = 8.sp)
        }
        Text(text = "›", color = Gray, fontSize = 24.sp)
    }
}

// ============================================================
// AI CHAT MESSAGE
// ============================================================

private data class AiChatMessage(
    val text: String,
    val isUser: Boolean
)

// ============================================================
// AI ASSISTANT (WITH OPTIONS, LOCATION, DATE, TIME & DB GROUNDING)
// ============================================================

@Composable
fun AiAssistantScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val aiChatDao = remember { db.aiChatDao() }
    val dbMessagesEntities by aiChatDao.getAllMessages().collectAsState(initial = emptyList())

    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var lastUserQuestion by remember { mutableStateOf("") }

    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    var showOptionsMenu by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Seed welcome message if database is empty on first launch
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val count = dbMessagesEntities.size
            if (count == 0) {
                aiChatDao.insertMessage(
                    AiChatMessageEntity(
                        text = "Hi! 👋 I'm ParkIQ AI Assistant. Ask me anything about parking, bookings or parking-owner services.",
                        isUser = false
                    )
                )
            }
        }
    }

    val messages = remember(dbMessagesEntities) {
        if (dbMessagesEntities.isNotEmpty()) {
            dbMessagesEntities.map { AiChatMessage(text = it.text, isUser = it.isUser) }
        } else {
            listOf(
                AiChatMessage(
                    text = "Hi! 👋 I'm ParkIQ AI Assistant. Ask me anything about parking, bookings or parking-owner services.",
                    isUser = false
                )
            )
        }
    }

    val model = remember {
        Firebase.ai(
            backend = GenerativeBackend.googleAI()
        ).generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are ParkIQ AI Assistant.
                    Help users with:
                    parking, parking bookings, parking prices, parking locations,
                    parking safety, navigation, payments, and parking-owner services.
                    Answer clearly, naturally and briefly.
                    You are inside the ParkIQ smart parking app.
                    """.trimIndent()
                )
            }
        )
    }

    suspend fun sendQuestion(question: String) {
        if (question.isBlank()) return
        isSending = true

        val databaseResult = queryParkIQDatabase(
            queryText = question,
            selectedLoc = selectedLocation,
            selectedDate = selectedDate,
            selectedTime = selectedTime
        )

        if (databaseResult != null) {
            withContext(Dispatchers.IO) {
                aiChatDao.insertMessage(AiChatMessageEntity(text = question, isUser = true))
                aiChatDao.insertMessage(AiChatMessageEntity(text = databaseResult, isUser = false))
            }
            isSending = false
            return
        }

        withContext(Dispatchers.IO) {
            aiChatDao.insertMessage(AiChatMessageEntity(text = question, isUser = true))
        }

        try {
            val validHistory = messages.filter { !it.text.startsWith("Sorry,") && !it.text.startsWith("Error:") }.map { msg ->
                content(role = if (msg.isUser) "user" else "model") {
                    text(msg.text)
                }
            }

            val freshChat = model.startChat(history = validHistory)
            val response = freshChat.sendMessage(question)
            val replyText = response.text?.trim().orEmpty()

            if (replyText.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    aiChatDao.insertMessage(
                        AiChatMessageEntity(
                            text = replyText,
                            isUser = false
                        )
                    )
                }
            } else {
                Log.e("ParkIQ-AI", "Gemini returned empty response")
                withContext(Dispatchers.IO) {
                    aiChatDao.insertMessage(
                        AiChatMessageEntity(
                            text = "Error: Gemini returned an empty response. Tap 'Retry Request' to try again.",
                            isUser = false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("ParkIQ-AI", "Gemini request failed: ${e.javaClass.name}: ${e.message}", e)

            val errorReason = when {
                e.message?.contains("403", ignoreCase = true) == true || e.javaClass.simpleName.contains("AppCheck", ignoreCase = true) ->
                    "App Check / Permission error. Ensure debug App Check token is added in Firebase Console."
                e.message?.contains("429", ignoreCase = true) == true || e.message?.contains("quota", ignoreCase = true) == true ->
                    "Rate limit or quota exceeded. Please wait a moment and try again."
                e.message?.contains("400", ignoreCase = true) == true || e.message?.contains("invalid", ignoreCase = true) == true ->
                    "Invalid argument or request format: ${e.localizedMessage ?: e.message}"
                else ->
                    "Error (${e.javaClass.simpleName}): ${e.localizedMessage ?: e.message ?: "Request failed"}"
            }

            withContext(Dispatchers.IO) {
                aiChatDao.insertMessage(
                    AiChatMessageEntity(
                        text = "Sorry, I couldn't get a response. $errorReason",
                        isUser = false
                    )
                )
            }
        }

        isSending = false
    }

    LaunchedEffect(messages.size, isSending) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (showOptionsMenu) {
        AiOptionsMenuDialog(
            onDismiss = { showOptionsMenu = false },
            onSelectLocation = { showOptionsMenu = false; showLocationDialog = true },
            onSelectDate = { showOptionsMenu = false; showDateDialog = true },
            onSelectTime = { showOptionsMenu = false; showTimeDialog = true }
        )
    }

    if (showLocationDialog) {
        LocationSelectionDialog(
            onDismiss = { showLocationDialog = false },
            onLocationChosen = { loc ->
                selectedLocation = loc
                showLocationDialog = false
                scope.launch {
                    sendQuestion("Parking availability in $loc")
                }
            }
        )
    }

    if (showDateDialog) {
        AiDateSelectionDialog(
            onDismiss = { showDateDialog = false },
            onDateChosen = { date ->
                selectedDate = date
                showDateDialog = false
                scope.launch {
                    sendQuestion("Check parking availability in ${selectedLocation ?: "Bengaluru"} on $date")
                }
            }
        )
    }

    if (showTimeDialog) {
        AiTimeSelectionDialog(
            onDismiss = { showTimeDialog = false },
            onTimeChosen = { time ->
                selectedTime = time
                showTimeDialog = false
                scope.launch {
                    sendQuestion("Check parking availability in ${selectedLocation ?: "Bengaluru"} at $time")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DashboardBackground)
            .imePadding()
    ) {

        // HEADER WITH BACK BUTTON & SMALL CLEAR CHAT ICON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
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
                text = "ParkIQ AI Assistant",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Small Clear Chat Option (Icon only)
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                    .clickable {
                        scope.launch {
                            withContext(Dispatchers.IO) {
                                aiChatDao.clearAllMessages()
                                aiChatDao.insertMessage(
                                    AiChatMessageEntity(
                                        text = "Hi! 👋 I'm ParkIQ AI Assistant. Ask me anything about parking, bookings or parking-owner services.",
                                        isUser = false
                                    )
                                )
                            }
                        }
                    },
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Clear Chat",
                    tint = Gray,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ask anything about parking",
                color = Gray,
                fontSize = 12.sp
            )
        }

        val hasUserSentMessage = remember(messages) { messages.any { it.isUser } }
        val lastAiIndex = remember(messages) { messages.indexOfLast { !it.isUser } }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            itemsIndexed(messages) { index, chatMessage ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (chatMessage.isUser) Alignment.End else Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(
                                if (chatMessage.isUser) Green else DashboardCard,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(13.dp)
                    ) {
                        Text(
                            text = chatMessage.text,
                            color = if (chatMessage.isUser) Color.Black else Color.White,
                            fontSize = 13.sp
                        )
                    }

                    // Show Options button ONLY directly below the latest AI response AND ONLY AFTER user sends a message
                    if (hasUserSentMessage && !chatMessage.isUser && index == lastAiIndex) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(DashboardBlue, RoundedCornerShape(8.dp))
                                    .clickable { showOptionsMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Options", tint = Green, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "Options", color = Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Retry button for failed messages
                    if (!chatMessage.isUser && chatMessage.text.contains("couldn't get a response", ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .background(DashboardBlue, RoundedCornerShape(8.dp))
                                .clickable(enabled = !isSending) {
                                    if (lastUserQuestion.isNotBlank()) {
                                        scope.launch {
                                            sendQuestion(lastUserQuestion)
                                        }
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "↻ Retry Request",
                                color = Green,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (isSending) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Box(
                            modifier = Modifier
                                .background(DashboardCard, RoundedCornerShape(16.dp))
                                .padding(13.dp)
                        ) {
                            Text(
                                text = "🤖 AI is thinking...",
                                color = Green,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = !isSending,
                placeholder = {
                    Text(
                        text = if (isSending) "AI is thinking..." else "Ask anything about parking",
                        color = Gray
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = DashboardBlue,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Green,
                    focusedContainerColor = DashboardCard,
                    unfocusedContainerColor = DashboardCard
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        if (isSending) DashboardBlue else Green,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = !isSending) {
                        val question = message.trim()
                        if (question.isEmpty()) return@clickable

                        lastUserQuestion = question
                        message = ""

                        scope.launch {
                            sendQuestion(question)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (isSending) Gray else Color.Black
                )
            }
        }
    }
}

// ============================================================
// OPTIONS MENU DIALOG
// ============================================================

@Composable
private fun AiOptionsMenuDialog(
    onDismiss: () -> Unit,
    onSelectLocation: () -> Unit,
    onSelectDate: () -> Unit,
    onSelectTime: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DashboardCard, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Options",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✕",
                    color = Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onDismiss() }.padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OptionMenuItem(icon = Icons.Default.LocationOn, title = "Location", onClick = onSelectLocation)
            Spacer(modifier = Modifier.height(10.dp))
            OptionMenuItem(icon = Icons.Default.CalendarToday, title = "Date", onClick = onSelectDate)
            Spacer(modifier = Modifier.height(10.dp))
            OptionMenuItem(icon = Icons.Default.AccessTime, title = "Time", onClick = onSelectTime)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OptionMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DashboardBlue, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = Green, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

// ============================================================
// LOCATION SELECTION DIALOG (BANGALORE AREAS)
// ============================================================

@Composable
private fun LocationSelectionDialog(
    onDismiss: () -> Unit,
    onLocationChosen: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredAreas = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            bangaloreAreas
        } else {
            bangaloreAreas.filter { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .background(DashboardCard, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Location (Bangalore)",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✕",
                    color = Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onDismiss() }.padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search location...", color = Gray, fontSize = 13.sp) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Green,
                    unfocusedBorderColor = DashboardBlue,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = DashboardBlue,
                    unfocusedContainerColor = DashboardBlue
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredAreas) { area ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DashboardBlue, RoundedCornerShape(10.dp))
                            .clickable { onLocationChosen(area) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Green, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = area, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

// ============================================================
// DATE SELECTION DIALOG
// ============================================================

@Composable
private fun AiDateSelectionDialog(
    onDismiss: () -> Unit,
    onDateChosen: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DashboardCard, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Select Date",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            listOf("Today", "Tomorrow", "Next 3 Days").forEach { dateOption ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(DashboardBlue, RoundedCornerShape(10.dp))
                        .clickable { onDateChosen(dateOption) }
                        .padding(14.dp)
                ) {
                    Text(text = "📅  $dateOption", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ============================================================
// TIME SELECTION DIALOG
// ============================================================

@Composable
private fun AiTimeSelectionDialog(
    onDismiss: () -> Unit,
    onTimeChosen: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DashboardCard, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Text(
                text = "Select Time Range",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            listOf(
                "Morning (8 AM - 12 PM)",
                "Afternoon (12 PM - 4 PM)",
                "Evening (4 PM - 8 PM)",
                "Night (8 PM - 12 AM)"
            ).forEach { timeOption ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(DashboardBlue, RoundedCornerShape(10.dp))
                        .clickable { onTimeChosen(timeOption) }
                        .padding(14.dp)
                ) {
                    Text(text = "🕐  $timeOption", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
