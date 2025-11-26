package com.example.agritour.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    // 1. WATCH the specific booking state (The New Way)
    val bookingState by viewModel.currentBooking.collectAsState()

    // 2. TRIGGER the fetch when screen opens
    LaunchedEffect(bookingId) {
        viewModel.loadSingleBooking(bookingId)
    }

    // 3. Define Context and State ONCE
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // 4. Loading Check
    if (bookingState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AgriGreen)
        }
        return // Stop here if loading
    }

    // 5. Force non-null for the rest of the UI (Safety step)
    val booking = bookingState!!
    val isCancellable = booking.status == "Pending" || booking.status == "Confirmed"

    Scaffold(
        containerColor = com.example.agritour.ui.theme.AgriBackground,
        topBar = {
            TopAppBar(
                title = { Text("Ticket Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = com.example.agritour.ui.theme.AgriBackground)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // 1. TICKET CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Image Header
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        // Status Badge
                        Surface(
                            color = if (isCancellable) AgriGreen else Color.Red,
                            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = booking.status,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(booking.farmName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = TextGrey, modifier = Modifier.size(16.dp))
                            Text("Kenya", color = TextGrey) // Placeholder for location
                        }

                        Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color(0xFFEEEEEE))

                        // Grid Details
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailItem(Icons.Default.CalendarToday, "Date", booking.date)
                            DetailItem(Icons.Default.Schedule, "Time", booking.time)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailItem(Icons.Default.Group, "Guests", "${booking.groupSize} People")
                            DetailItem(Icons.Default.CreditCard, "Price", "Ksh ${booking.totalPrice}")
                        }

                        Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color(0xFFEEEEEE))

                        // QR Code Placeholder
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode2,
                                contentDescription = "QR Code",
                                modifier = Modifier.size(100.dp),
                                tint = TextBlack
                            )
                            Text("Scan at entry", style = MaterialTheme.typography.bodySmall, color = TextGrey)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. CANCEL BUTTON
            if (isCancellable) {
                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)), // Light Red
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Cancel Booking", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cancel Booking?") },
            text = { Text("Are you sure you want to cancel this trip? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelBooking(bookingId)
                        Toast.makeText(context, "Booking Cancelled", Toast.LENGTH_SHORT).show()
                        showDialog = false
                        onBackClick() // Go back to list
                    }
                ) { Text("Yes, Cancel", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Keep it") }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(modifier = Modifier.width(100.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextGrey)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = AgriGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}