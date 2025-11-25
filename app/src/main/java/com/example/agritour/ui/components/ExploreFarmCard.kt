package com.example.agritour.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // Ensure Coil is imported
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey

@Composable
fun ExploreFarmCard(
    farmName: String,
    farmType: String,
    location: String,
    rating: Double,
    imageUrl: String, // <--- NEW PARAMETER
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 1. Image Box with overlay text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray)
            ) {
                // Real Image using Coil
                AsyncImage(
                    model = imageUrl,
                    contentDescription = farmName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient for readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 100f
                            )
                        )
                )

                // Overlay Content (Name and Rating)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = farmName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Rating Pill
                    Surface(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(rating.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 2. Details and Button section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "$farmType • $location",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBookClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Book Visit", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}