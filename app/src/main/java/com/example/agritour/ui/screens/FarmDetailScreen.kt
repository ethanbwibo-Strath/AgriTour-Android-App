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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmDetailScreen(
    onBackClick: () -> Unit,
    onBookClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = AgriBackground, // The light grey background from your wireframe

        // 1. Top Bar (Matching Wireframe)
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Green Valley Farm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                        Text("Ksh 500", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AgriGreen)
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
                    .background(Color.LightGray) // Placeholder for Image
            ) {
                // Gradient for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                                startY = 100f
                            )
                        )
                )

                // Text Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Organic Family Farm",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Green Valley Farm",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(4) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("4.8", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 4. About Farm Card
            ContentCard(title = "About Farm") {
                Text(
                    text = "Green Valley Farm is a third-generation family-owned organic farm dedicated to sustainable agriculture. We specialize in cultivating a variety of seasonal vegetables, herbs, and free-range poultry. Our commitment to ecological balance ensures fresh, nutritious produce and a rich learning environment for our visitors.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey,
                    lineHeight = 22.sp
                )
            }

            // 5. "What you will learn" Card
            ContentCard(title = "What you will learn") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    LearnItem("Understand organic farming principles and practices.")
                    LearnItem("Learn about crop rotation and soil health management.")
                    LearnItem("Participate in harvesting seasonal vegetables and herbs.")
                    LearnItem("Discover the life cycle and care of free-range chickens.")
                    LearnItem("Explore sustainable water management techniques.")
                }
            }

            // 6. Owner Profile Card
            ContentCard(title = "Owner Profile") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar Placeholder
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD1F2EB)) // Light Green bg
                    ) {
                        // Normally an Image here. Using Icon for placeholder
                        Icon(
                            imageVector = Icons.Default.MoreVert, // Replace with User Image
                            contentDescription = null,
                            tint = AgriGreen,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text("Maria Rodriguez", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Farm Owner & Educator", style = MaterialTheme.typography.bodyMedium, color = TextGrey)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Chat Button
                Button(
                    onClick = { /* Open Chat */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                    shape = RoundedCornerShape(50) // Pill shape
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chat with Owner")
                }
            }

            // Spacer to ensure content isn't hidden by bottom bar
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