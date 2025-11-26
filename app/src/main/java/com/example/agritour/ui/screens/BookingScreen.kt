package com.example.agritour.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    farmId: String,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {

    val context = LocalContext.current

    val farm = viewModel.getFarmById(farmId)
    val pricePerPerson = farm?.price ?: 0
    val farmName = farm?.name ?: "Unknown Farm"


    var date by remember { mutableStateOf("2024-10-26") }
    var time by remember { mutableStateOf("14:00") }
    var groupSize by remember { mutableIntStateOf(4) }
    var selectedPayment by remember { mutableStateOf("M-Pesa") }
    var isBooking by remember { mutableStateOf(false) }
    val totalAmount = groupSize * pricePerPerson

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Book Your Tour",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextBlack,
                    navigationIconContentColor = TextBlack,
                    actionIconContentColor = TextBlack
                )
            )
        },

        bottomBar = {
            Button(
                onClick = {
                    if (!isBooking) {
                        isBooking = true
                        // 4. Save to Firestore
                        viewModel.createBooking(
                            farmId = farmId,
                            farmOwnerId = farm?.ownerId ?: "",
                            farmName = farmName,
                            date = date,
                            time = time,
                            groupSize = groupSize,
                            totalPrice = totalAmount,
                            paymentMethod = selectedPayment
                        ) { success ->
                            isBooking = false
                            if (success) {
                                Toast.makeText(context, "Booking Successful!", Toast.LENGTH_LONG).show()
                                onConfirmClick() // Go back to Home
                            } else {
                                Toast.makeText(context, "Booking Failed. Try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = !isBooking // Disable while loading
            ) {
                if (isBooking) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirm (Ksh $totalAmount)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                text = "Booking for: $farmName",
                style = MaterialTheme.typography.titleMedium,
                color = AgriGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Tour Details Section
            Text(
                "Tour Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Date Field
            Label("Date")
            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true, // Prevent typing, force picker
                leadingIcon = { Icon(Icons.Default.CalendarToday, null, tint = TextGrey) },
                modifier = Modifier.fillMaxWidth().clickable { /* Show DatePicker here */ },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = AgriGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Time Field
            Label("Time")
            OutlinedTextField(
                value = time,
                onValueChange = {},
                readOnly = true,
                leadingIcon = { Icon(Icons.Default.Schedule, null, tint = TextGrey) },
                modifier = Modifier.fillMaxWidth().clickable { /* Show TimePicker here */ },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = AgriGreen
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Group Size Counter
            Label("Group Size (Min 4 people)")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Minus Button
                CircularIconButton(
                    icon = Icons.Default.Remove,
                    enabled = groupSize > 4, // Logic: Disable if 4
                    onClick = { if (groupSize > 4) groupSize-- }
                )

                Text(
                    text = groupSize.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Plus Button
                CircularIconButton(
                    icon = Icons.Default.Add,
                    enabled = true,
                    onClick = { groupSize++ }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Payment Options Section
            Text(
                "Payment Options",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // M-Pesa Option
            PaymentOptionCard(
                title = "M-Pesa",
                icon = Icons.Default.Smartphone,
                isSelected = selectedPayment == "M-Pesa",
                onClick = { selectedPayment = "M-Pesa" }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card Option
            PaymentOptionCard(
                title = "Card Payment",
                icon = Icons.Default.CreditCard,
                isSelected = selectedPayment == "Card",
                onClick = { selectedPayment = "Card" }
            )

            // Space for scrolling above bottom button
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- Helper Composables ---

@Composable
fun Label(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = TextBlack,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun CircularIconButton(
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        border = BorderStroke(1.dp, if (enabled) Color(0xFFE0E0E0) else Color(0xFFF5F5F5)),
        color = if (enabled) Color.White else Color(0xFFF9F9F9),
        modifier = Modifier.size(48.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) TextBlack else Color.LightGray
            )
        }
    }
}

@Composable
fun PaymentOptionCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) AgriGreen else Color(0xFFE0E0E0)
        ),
        color = if (isSelected) Color(0xFFF0FDF4) else Color.White, // Very light green if selected
        modifier = Modifier.fillMaxWidth().height(64.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) AgriGreen else TextGrey
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) AgriGreen else TextBlack,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            // Radio Button Visual
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = AgriGreen)
            )
        }
    }
}