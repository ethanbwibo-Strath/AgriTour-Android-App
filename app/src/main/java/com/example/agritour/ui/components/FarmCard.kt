package com.example.agritour.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agritour.R
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun FarmCard(
    farmName: String,
    location: String,
    price: String, // We'll convert Int to String in the screen
    rating: Double,
    imageUrl: String, // <--- NEW PARAMETER
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(280.dp)
            .padding(end = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // 1. Real Image using Coil
            AsyncImage(
                model = imageUrl,
                contentDescription = farmName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color.Gray), // Loading background
                contentScale = ContentScale.Crop,
                onLoading = { Log.d("Coil", "Loading image: $imageUrl") },
                onError = { error -> Log.e("Coil", "Error loading image: ${error.result.throwable}") }
            )

            // 2. Text Content (Same as before)
            Column(modifier = Modifier.padding(12.dp)) {
                // ... (Keep the rest of your text code exactly the same) ...
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = farmName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = rating.toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                // ... (Location and Price Text logic remains the same) ...
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$price / person",
                    style = MaterialTheme.typography.labelLarge,
                    color = com.example.agritour.ui.theme.AgriGreen, // Ensure correct import
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}