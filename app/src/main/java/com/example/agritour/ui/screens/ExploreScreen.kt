package com.example.agritour.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
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

    val typeOptions by viewModel.availableTypes.collectAsState()
    val locationOptions by viewModel.availableLocations.collectAsState()
    val priceRange by viewModel.priceRange.collectAsState()
    val maxDbPrice by viewModel.maxPriceInDb.collectAsState()
    val minRating by viewModel.minRating.collectAsState()

    var showTypeSheet by remember { mutableStateOf(false) }
    var showLocationSheet by remember { mutableStateOf(false) }
    var showPriceSheet by remember { mutableStateOf(false) }
    var showRatingSheet by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("All") }
    var selectedLocation by remember { mutableStateOf("All") }

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            // FIX 1: Smaller, Left-Aligned Title
            TopAppBar(
                title = {
                    Text(
                        "Explore Farms",
                        style = MaterialTheme.typography.headlineSmall, // Reduced Size
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onProfileClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {

            // FIX 2: Added Price and Rating Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Type
                item {
                    DropdownFilterChip("Farm Type", if (selectedType == "All") null else selectedType) { showTypeSheet = true }
                }
                // Location
                item {
                    DropdownFilterChip("Location", if (selectedLocation == "All") null else selectedLocation) { showLocationSheet = true }
                }
                // Price (Visual Placeholder)
                item {
                    val isPriceFiltered = priceRange.start > 0f || priceRange.endInclusive < maxDbPrice
                    DropdownFilterChip(
                        label = "Price",
                        selectedValue = if (isPriceFiltered) "Ksh ${priceRange.start.toInt()}-${priceRange.endInclusive.toInt()}" else null,
                        onClick = { showPriceSheet = true }
                    )
                }
                // Rating (Visual Placeholder)
                item {
                    DropdownFilterChip(
                        label = "Rating",
                        selectedValue = if (minRating > 0.0) "${minRating.toInt()}+ Stars" else null,
                        onClick = { showRatingSheet = true }
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
                            price = farm.price,
                            actionText = "View Details",
                            onBookClick = { onFarmClick(farm.id) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        // 3. BOTTOM SHEETS
        if (showTypeSheet) {
            FilterBottomSheet(
                title = "Select Farm Type",
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

        if (showPriceSheet) {
            PriceFilterSheet(
                currentRange = priceRange,
                maxPrice = maxDbPrice,
                onDismiss = { showPriceSheet = false },
                onRangeSelected = { viewModel.setPriceRange(it) }
            )
        }

        if (showRatingSheet) {
            RatingFilterSheet(
                currentRating = minRating,
                onDismiss = { showRatingSheet = false },
                onRatingSelected = { viewModel.setMinRating(it) }
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
                text = selectedValue ?: label,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextBlack)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextBlack)
                }
            }

            Divider(color = AgriGreen)

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

// --- PRICE FILTER SHEET ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceFilterSheet(
    currentRange: ClosedFloatingPointRange<Float>,
    maxPrice: Float,
    onDismiss: () -> Unit,
    onRangeSelected: (ClosedFloatingPointRange<Float>) -> Unit
) {
    // Local state for slider interaction
    var sliderPosition by remember { mutableStateOf(currentRange) }

    // Preset Options
    val presets = listOf(
        "0 - 500" to 0f..500f,
        "501 - 1000" to 501f..1000f,
        "1001 - 2000" to 1001f..2000f,
        "2000+" to 2001f..maxPrice
    )

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FIX: Added color = TextBlack
                Text("Price Range", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextBlack)

                TextButton(onClick = { onRangeSelected(0f..maxPrice); onDismiss() }) {
                    // FIX: Added color = TextGrey (Dark Grey)
                    Text("Reset", color = TextGrey)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 16.dp), color = AgriGreen)

            // 1. Presets
            // FIX: Added color = TextBlack
            Text("Quick Select", style = MaterialTheme.typography.labelLarge, color = TextBlack)
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { (label, range) ->
                    val isSelected = sliderPosition.start == range.start && sliderPosition.endInclusive >= range.endInclusive

                    FilterChip(
                        selected = isSelected,
                        onClick = { sliderPosition = range },
                        label = {
                            // FIX: Ensure label is readable based on selection
                            Text(label, color = if (isSelected) Color.White else TextBlack)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AgriGreen,
                            containerColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) AgriGreen else BorderColor,
                            borderWidth = 1.dp,
                            selected = isSelected,
                            enabled = true
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Slider
            // FIX: Custom Range text uses AgriGreen, which is dark enough
            Text(
                text = "Custom Range: Ksh ${sliderPosition.start.toInt()} - Ksh ${sliderPosition.endInclusive.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AgriGreen
            )

            RangeSlider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..maxPrice,
                colors = SliderDefaults.colors(thumbColor = AgriGreen, activeTrackColor = AgriGreen, inactiveTrackColor = AgriBackground)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Apply Button
            Button(
                onClick = { onRangeSelected(sliderPosition); onDismiss() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Apply Price Filter", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// --- RATING FILTER SHEET ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingFilterSheet(
    currentRating: Double,
    onDismiss: () -> Unit,
    onRatingSelected: (Double) -> Unit
) {
    val ratings = listOf(4.0, 3.0, 2.0, 1.0)

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(modifier = Modifier.padding(24.dp)) {
            // FIX: Added color = TextBlack
            Text("Filter by Rating", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextBlack)
            Divider(modifier = Modifier.padding(vertical = 16.dp), color = AgriGreen)

            // "All Ratings" Option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRatingSelected(0.0); onDismiss() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // FIX: Added color = TextBlack
                Text("Show All", style = MaterialTheme.typography.bodyLarge, color = TextBlack)
                if (currentRating == 0.0) Icon(Icons.Default.Check, null, tint = AgriGreen)
            }

            // Star Options
            ratings.forEach { rating ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRatingSelected(rating); onDismiss() }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stars Display
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // FIX: Added color = TextBlack (or TextGrey)
                    Text("& up", style = MaterialTheme.typography.bodyMedium, color = TextBlack)

                    Spacer(modifier = Modifier.weight(1f))

                    if (currentRating == rating) Icon(Icons.Default.Check, null, tint = AgriGreen)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}