package com.example.agritour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agritour.ui.components.FarmCard
import com.example.agritour.ui.components.HomeHeroSection
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFarmClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AgriBackground, // Light grey/green bg
        // 1. Custom Top Bar
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .statusBarsPadding(), // Avoids notch
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Area
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Simple Green Box to represent Logo
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AgriGreen, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search, // Placeholder for Leaf logo
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AgriTour",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AgriGreen
                    )
                }

                // Right Icons
                Row {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = TextBlack,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextBlack,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        // 2. Bottom Navigation Bar
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriGreen, indicatorColor = Color.White)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {

            // 3. The New Hero Section
            HomeHeroSection(onBookClick = { /* Scroll to farms */ })

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Search Filter Cards (Visual only based on screenshot)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    FakeSearchField("Search by location (e.g., Nairobi)")
                    Spacer(modifier = Modifier.height(12.dp))
                    FakeSearchField("Search by crop type (e.g., Coffee)")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F7F5)), // Light grey button
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Search Farms", color = TextBlack)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Featured Farms (Top Rated)
            Text(
                "Top Rated Farms",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp) // Add padding at bottom
            ) {
                item {
                    FarmCard("Green Valley Organic", "Kakamega, Kenya", "Ksh 500", 4.8, onFarmClick)
                }
                item {
                    FarmCard("Sunrise Dairy", "Rift Valley, Kenya", "Ksh 800", 4.9, onFarmClick)
                }
                item {
                    FarmCard("Kajiado Poultry", "Kajiado, Kenya", "Ksh 350", 4.5, onFarmClick)
                }
            }
        }
    }
}

// Helper composable for the grey search inputs
@Composable
fun FakeSearchField(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Gray)
    }
}