package com.example.agritour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onLearnClick: () -> Unit
) {
    Scaffold(
        containerColor = AgriBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onHomeClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = false,
                    onClick = onExploreClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = onLearnClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = true, // Selected!
                    onClick = { /* Already here */ },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriGreen, indicatorColor = Color.White)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 1. Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(AgriGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AgriGreen,
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "John Doe",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        text = "john.doe@student.iku.ac.ke",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Menu Options
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )

                MenuCard {
                    ProfileMenuItem(icon = Icons.Outlined.DateRange, title = "My Bookings")
                    Divider(color = AgriBackground)
                    ProfileMenuItem(icon = Icons.Outlined.FavoriteBorder, title = "Saved Farms")
                    Divider(color = AgriBackground)
                    ProfileMenuItem(icon = Icons.Outlined.CreditCard, title = "Payment Methods")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "General",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )

                MenuCard {
                    ProfileMenuItem(icon = Icons.Outlined.Settings, title = "Settings")
                    Divider(color = AgriBackground)
                    ProfileMenuItem(icon = Icons.AutoMirrored.Outlined.Help, title = "Help & Support")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = { /* Handle Logout */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)), // Light Red
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ExitToApp, null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- Helper Components ---

@Composable
fun MenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextGrey)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = TextBlack,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}