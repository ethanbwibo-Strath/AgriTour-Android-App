package com.example.agritour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.agritour.data.Booking
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onLearnClick: () -> Unit,
    onProfileClick: () -> Unit,
    onBookingClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val bookings by viewModel.myBookings.collectAsState()

    // Tab State
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming Trips", "Booking History")

    // Filter Logic
    val filteredBookings = remember(selectedTab, bookings) {
        if (selectedTab == 0) {
            // Upcoming: Pending or Confirmed
            bookings.filter { it.status == "Pending" || it.status == "Confirmed" }
        } else {
            // History: Cancelled or Completed
            bookings.filter { it.status == "Cancelled" || it.status == "Completed" }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserBookings()
    }

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Trips", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AgriBackground,
                    titleContentColor = TextBlack
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onHomeClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = false,
                    onClick = onExploreClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = onLearnClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = true, // Highlight Profile since this is a sub-section of Profile
                    onClick = onProfileClick,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriGreen, indicatorColor = Color.White)
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // 1. Custom Tab Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    val isSelected = selectedTab == index
                    Button(
                        onClick = { selectedTab = index },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) AgriGreen else Color.White,
                            contentColor = if (isSelected) Color.White else TextBlack
                        ),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(text = title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. The List
            if (filteredBookings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (selectedTab == 0) "No upcoming trips" else "No booking history",
                        color = TextGrey
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredBookings) { booking ->
                        BookingItemCard(
                            booking = booking,
                            onClick = { onBookingClick(booking.id) } // <--- PASS ID
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onClick: () -> Unit
    ) {
    val statusColor = when(booking.status) {
        "Confirmed" -> AgriGreen
        "Cancelled" -> Color.Red
        else -> Color(0xFFF39C12)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable{ onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Farm Image
            AsyncImage(
                model = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854", // Placeholder
                contentDescription = booking.farmName,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = booking.farmName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    )

                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50),
                    ) {
                        Text(
                            text = booking.status,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = "${booking.date} | ${booking.time}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey
                )

                Text(
                    text = "${booking.groupSize} Participants",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey
                )
            }
        }
    }
}