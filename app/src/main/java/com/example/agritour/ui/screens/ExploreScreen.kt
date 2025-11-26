package com.example.agritour.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.ui.components.ExploreFarmCard
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.BorderColor
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onHomeClick: () -> Unit,
    onFarmClick: (String) -> Unit,
    onLearnClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val farms by viewModel.farms.collectAsState()

    // State to control which bottom sheet is open
    var showTypeSheet by remember { mutableStateOf(false) }
    var showLocationSheet by remember { mutableStateOf(false) }

    // Filter Options (Hardcoded for now, or fetch from DB unique values)
    val typeOptions = listOf("All", "Coffee", "Vegetables", "Herbs", "Fruits")
    val locationOptions = listOf("All", "Nairobi", "Limuru", "Naivasha", "Mombasa", "Nakuru")

    // State to track selected values for display on chips
    var selectedType by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Explore Farms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = AgriBackground, titleContentColor = TextBlack)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onHomeClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGrey,
                        unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AgriGreen,
                        selectedTextColor = AgriGreen,
                        indicatorColor = Color.White)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = onLearnClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGrey,
                        unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onProfileClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGrey,
                        unselectedTextColor = TextGrey)
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // 1. FILTER CHIPS ROW (Opens Sheets)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // TYPE CHIP
                item {
                    DropdownFilterChip(
                        label = "Farm Type",
                        selectedValue = if (selectedType == "All") null else selectedType,
                        onClick = { showTypeSheet = true }
                    )
                }
                // LOCATION CHIP
                item {
                    DropdownFilterChip(
                        label = "Location",
                        selectedValue = if (selectedLocation == "All") null else selectedLocation,
                        onClick = { showLocationSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 2. FARM LIST
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                if (farms.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No farms found matching these filters", color = TextGrey)
                        }
                    }
                } else {
                    items(farms) { farm ->
                        ExploreFarmCard(
                            farmName = farm.name,
                            farmType = farm.type,
                            location = farm.location,
                            rating = farm.rating,
                            imageUrl = farm.imageUrl,
                            onBookClick = { onFarmClick(farm.id) }
                        )
                    }
                }
            }
        }

        // 3. BOTTOM SHEETS (The Dropdowns)

        if (showTypeSheet) {
            FilterBottomSheet(
                title = "Select Plant Type",
                options = typeOptions,
                currentSelection = selectedType,
                onDismiss = { showTypeSheet = false },
                onOptionSelected = { option ->
                    selectedType = option
                    viewModel.setTypeFilter(if (option == "All") null else option)
                    showTypeSheet = false
                }
            )
        }

        if (showLocationSheet) {
            FilterBottomSheet(
                title = "Select Location",
                options = locationOptions,
                currentSelection = selectedLocation,
                onDismiss = { showLocationSheet = false },
                onOptionSelected = { option ->
                    selectedLocation = option
                    viewModel.setLocationFilter(if (option == "All") null else option)
                    showLocationSheet = false
                }
            )
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun DropdownFilterChip(
    label: String,
    selectedValue: String?,
    onClick: () -> Unit
) {
    val isSelected = selectedValue != null
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = selectedValue ?: label, // Show "Coffee" or "Plant Type"
                color = if (isSelected) AgriGreen else TextBlack,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = if (isSelected) AgriGreen else TextGrey,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(containerColor = Color.White),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) AgriGreen else BorderColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    title: String,
    options: List<String>,
    currentSelection: String,
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Divider(color = AgriBackground)

            // Options List
            LazyColumn {
                items(options) { option ->
                    val isSelected = option == currentSelection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isSelected) AgriGreen else TextBlack,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AgriGreen)
                        }
                    }
                }
            }
        }
    }
}