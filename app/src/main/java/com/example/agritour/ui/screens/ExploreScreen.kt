package com.example.agritour.ui.screens

import androidx.compose.foundation.BorderStroke
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onHomeClick: () -> Unit,
    onFarmClick: () -> Unit // Even though the button says "Book", it usually leads to details first
) {
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AgriBackground)
            )
        },
        bottomBar = {
            // We replicate the bottom bar here to show "Explore" as selected
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onHomeClick // Navigate back to Home
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = true, // Selected!
                    onClick = { /* Already here */ },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriGreen, indicatorColor = Color.White)
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
        ) {
            // 1. Filters Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(3) { index ->
                    val labels = listOf("Plant Type", "Experience Level", "Location")
                    FilterChipDemo(label = labels[index])
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. Vertical List of Farms
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                // Fake data mirroring wireframe
                item { ExploreFarmCard("Green Valley Gardens", "Vegetables Farm", "Nairobi, Kenya", 4.8, onFarmClick) }
                item { ExploreFarmCard("Highlands Coffee Estate", "Coffee Farm", "Limuru, Kenya", 4.5, onFarmClick) }
                item { ExploreFarmCard("Serenity Herb Farm", "Herbs Farm", "Naivasha, Kenya", 4.9, onFarmClick) }
                item { ExploreFarmCard("Tropical Fruit Oasis", "Fruits Farm", "Mombasa, Kenya", 4.6, onFarmClick) }
                // Add padding at bottom for navigation bar
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