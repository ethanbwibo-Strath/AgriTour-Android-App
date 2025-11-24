package com.example.agritour.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agritour.ui.components.LearningCard
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningHubScreen(
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onArticleClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // Local state for the filter
    var selectedFilter by remember { mutableStateOf("All") }

    val filters = listOf("All", "Articles", "Videos")

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            TopAppBar(
                title = { Text("Learning Hub", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AgriBackground)
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
                    selected = false,
                    onClick = onExploreClick // Linked!
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = true,
                    onClick = { /* Stay here */ },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = AgriGreen, indicatorColor = Color.White)
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
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // 1. Categories Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters.size) { index ->
                    val filterName = filters[index]
                    val isSelected = selectedFilter == filterName

                    // Determine colors for chips
                    val (textColor, borderColor) = when(filterName) {
                        "Videos" -> Color(0xFF9B59B6) to Color(0xFF9B59B6) // Purple
                        "Articles" -> Color(0xFF3498DB) to Color(0xFF3498DB) // Blue
                        else -> AgriGreen to AgriGreen
                    }

                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filterName },
                        label = {
                            Text(
                                text = filterName,
                                color = if (isSelected) Color.White else textColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = textColor, // Use the specific color when selected
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = borderColor.copy(alpha = 0.5f),
                            borderWidth = 1.dp
                        )
                    )
                }
            }

            // 2. Content List
            LazyColumn(
                contentPadding = PaddingValues(16.dp)
            ) {
                // We show items based on the filter
                if (selectedFilter == "All" || selectedFilter == "Articles") {
                    item {
                        LearningCard(
                            type = "Article",
                            title = "Sustainable Coffee Processing Techniques",
                            description = "Explore environmentally friendly methods for coffee bean processing.",
                            duration = "10 min read",
                            date = "Oct 26, 2023",
                            onClick = onArticleClick
                        )
                    }
                }

                if (selectedFilter == "All" || selectedFilter == "Videos") {
                    item {
                        LearningCard(
                            type = "Video",
                            title = "Introduction to Permaculture Design",
                            description = "Learn the foundational concepts of permaculture.",
                            duration = "25 min watch",
                            date = "Sep 12, 2023",
                            onClick = onArticleClick
                        )
                    }
                }

                if (selectedFilter == "All" || selectedFilter == "Articles") {
                    item {
                        LearningCard(
                            type = "Article",
                            title = "The Role of Mycorrhizal Fungi",
                            description = "Delve into the fascinating symbiotic relationship between plants and fungi.",
                            duration = "8 min read",
                            date = "Aug 01, 2023",
                            onClick = onArticleClick
                        )
                    }
                }
            }
        }
    }
}