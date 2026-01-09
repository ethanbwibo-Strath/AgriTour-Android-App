package com.example.agritour.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agritour.R // Ensure this matches your package
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.ui.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.agritour.ui.components.AgriAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmDetailScreen(
    farmId: String,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit,
    onChatClick: (String, String, String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val farms by viewModel.farms.collectAsState()
    val owner by viewModel.currentFarmOwner.collectAsState()

    val farm = farms.find { it.id == farmId }

    LaunchedEffect(farm) {
        if (farm != null && farm.ownerId.isNotEmpty()) {
            viewModel.fetchFarmOwner(farm.ownerId)
        }
    }

    if (farm == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = com.example.agritour.ui.theme.AgriGreen)
        }
        return // Stop here until data arrives
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AgriBackground,

        // 1. Top Bar
        topBar = {
            TopAppBar(
                title = { Text(farm.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share logic */ }) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share")
                    }
                    IconButton(onClick = { /* Menu logic */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
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

        // 2. Sticky Bottom Booking Bar (Retained per request)
        bottomBar = {
            Surface(
                shadowElevation = 10.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Price per person", style = MaterialTheme.typography.labelMedium, color = TextGrey)
                        Text("Ksh ${farm.price}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AgriGreen)
                    }
                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Book Visit", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp), // Screen padding
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 3. Hero Image Section with Overlay Text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(Color.LightGray)
            ) {
                // REAL IMAGE
                AsyncImage(
                    model = farm.imageUrl,
                    contentDescription = farm.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "${farm.type} Farm", // e.g., "Coffee Farm"
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )

                    // --- ADD THIS SPACER ---
                    Spacer(modifier = Modifier.height(2.dp))
                    // -----------------------

                    Text(
                        text = farm.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(farm.rating.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 4. About Farm Card
            ContentCard(title = "About Farm") {
                Text(
                    text = farm.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey,
                    lineHeight = 22.sp
                )
            }

            // 5. "What you will learn" Card
            ContentCard(title = "What you will learn") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LearnItem("Understand ${farm.type} farming principles.")
                    LearnItem("Learn about crop rotation and soil health.")
                    LearnItem("Participate in harvesting activities.")
                }
            }

            // 6. Owner Profile Card
            ContentCard(title = "Owner Profile") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AgriAvatar(
                        name = owner?.name ?: "Owner",
                        imageUrl = owner?.profileImageUrl,
                        size = 56.dp,
                        fontSize = 20.sp,
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        // DYNAMIC NAME
                        Text(
                            text = owner?.name ?: "Farm Owner's Name",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreen
                        )
                        // Role/Email
                        Text(
                            text = owner?.email ?: "Farm Owner's Email",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGrey
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chat Button
                Button(
                    onClick = {onChatClick(
                        owner?.uid ?: "",
                        owner?.name ?: "Farm Owner",
                        owner?.profileImageUrl ?: ""
                        )
                  },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat with ${owner?.name ?: "Farm Owner"}")
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- Helper Components for Clean Code ---

@Composable
fun ContentCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat style like wireframe
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun LearnItem(text: String) {
    // Change 'crossAxisAlignment' to 'verticalAlignment'
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Outlined.Eco,
            contentDescription = null,
            tint = AgriGreen,
            modifier = Modifier
                .size(20.dp)
                .offset(y = 2.dp) // Slight offset to align with text cap-height
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextGrey
        )
    }
}