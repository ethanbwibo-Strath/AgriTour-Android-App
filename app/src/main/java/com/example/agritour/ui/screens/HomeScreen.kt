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
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
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
import com.example.agritour.ui.theme.TextGrey

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import com.example.agritour.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFarmClick: () -> Unit,
    onExploreClick: () -> Unit,
    onLearnClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},

    viewModel: HomeViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val farms by viewModel.farms.collectAsState()

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Area
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(AgriGreen, shape = RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
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
                    onClick = { /* Stay on Home */ },
                    colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AgriGreen,
                            selectedTextColor = AgriGreen,
                            indicatorColor = Color.White,
                            unselectedIconColor = TextBlack,
                            unselectedTextColor = TextBlack
                        )
                )

                // --- THE FIX IS HERE ---
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = false,
                    onClick = onExploreClick // <--- Now it triggers the navigation!
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
                    selected = false,
                    onClick = onProfileClick
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

            HomeHeroSection(onBookClick = onExploreClick) // Link the "Book Now" banner too!

            Spacer(modifier = Modifier.height(24.dp))

            // Search Filter Cards
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
                        onClick = onExploreClick, // Link this button too
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F7F5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Search Farms", color = TextBlack)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Top Rated Farms",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(farms) { farm ->
                    FarmCard(
                        farmName = farm.name,
                        location = farm.location,
                        price = "Ksh ${farm.price}",
                        rating = farm.rating,
                        imageUrl = farm.imageUrl, // Pass the URL from Firestore
                        onClick = onFarmClick
                    )
                }
            }
        }
    }
}

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