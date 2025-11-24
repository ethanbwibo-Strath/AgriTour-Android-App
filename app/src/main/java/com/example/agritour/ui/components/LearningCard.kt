package com.example.agritour.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey

@Composable
fun LearningCard(
    type: String, // "Article", "Video", "Tutorial"
    title: String,
    description: String,
    duration: String,
    date: String,
    onClick: () -> Unit
) {
    // 1. Determine Color based on Type
    val tagColor = when (type) {
        "Video" -> Color(0xFF9B59B6)   // Amethyst Purple
        "Article" -> Color(0xFF3498DB) // Peter River Blue
        else -> AgriGreen              // Default Green
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray)
            ) {
                // 2. Apply Dynamic Color to the Tag Surface
                Surface(
                    color = tagColor,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = type,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Content Area
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGrey,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Metadata Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccessTime, null, tint = TextGrey, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(duration, style = MaterialTheme.typography.labelMedium, color = TextGrey)

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(Icons.Outlined.CalendarToday, null, tint = TextGrey, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(date, style = MaterialTheme.typography.labelMedium, color = TextGrey)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Link (Colored)
                Text(
                    text = if (type == "Video") "Watch Video" else "Read Article",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tagColor, // Match the tag color
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}