package com.example.agritour.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agritour.ui.components.ExploreFarmCard
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey

import com.example.agritour.ui.viewmodel.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onHomeClick: () -> Unit,
    onFarmClick: () -> Unit,
    onLearnClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val farms by viewModel.farms.collectAsState()

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Explore Farms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AgriBackground,
                    titleContentColor = TextBlack,
                    navigationIconContentColor = TextBlack,
                    actionIconContentColor = TextBlack
                )
            )
        },
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
                    selected = true,
                    onClick = { /* Already here */ },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AgriGreen,
                        selectedTextColor = AgriGreen,
                        indicatorColor = Color.White,
                        unselectedIconColor = TextBlack,
                        unselectedTextColor = TextBlack
                    )
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = onLearnClick // <--- 2. Must use the parameter here!
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
        ) {
            // Filters Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(3) { index ->
                    val labels = listOf("Plant Type", "Experience Level", "Location")
                    FilterChipDemo(label = labels[index])
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vertical List of Farms
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                items(farms) { farm ->
                    ExploreFarmCard(
                        farmName = farm.name,
                        farmType = farm.type, // Make sure your Firestore has a 'type' field, or default to "Mixed Farm"
                        location = farm.location,
                        rating = farm.rating,
                        imageUrl = farm.imageUrl, // Pass the real image
                        onBookClick = onFarmClick
                    )
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun FilterChipDemo(label: String) {
    AssistChip(
        onClick = { /* Open filter dialog */ },
        label = { Text(text = label) },
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(20.dp))
        },
        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White),
        border = BorderStroke(width = 1.dp, color = Color(0xFFE0E0E0))
    )
}