package com.example.agritour.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
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
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsScreen(
    onBackClick: () -> Unit,
    onAddFarmClick: () -> Unit,
    onFarmClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val myFarms by viewModel.myFarms.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchMyFarms()
    }

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            TopAppBar(
                title = { Text("My Farm Listings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AgriBackground,
                    titleContentColor = TextBlack,
                    navigationIconContentColor = TextBlack
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddFarmClick,
                containerColor = AgriGreen,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Farm")
            }
        }
    ) { paddingValues ->
        if (myFarms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("You haven't listed any farms yet.", color = TextGrey)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tap the + button to start!", color = AgriGreen, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.padding(paddingValues).fillMaxSize()
            ) {
                items(myFarms) { farm ->
                    // Reuse our Explore Card for consistency
                    ExploreFarmCard(
                        farmName = farm.name,
                        farmType = farm.type,
                        location = farm.location,
                        rating = farm.rating,
                        price = farm.price,
                        imageUrl = farm.imageUrl,
                        actionText = "Edit Listing",
                        onBookClick = { onFarmClick(farm.id) }
                    )
                }
            }
        }
    }
}