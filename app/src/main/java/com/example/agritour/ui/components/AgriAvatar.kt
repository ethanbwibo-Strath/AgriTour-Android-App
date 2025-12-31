package com.example.agritour.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.agritour.ui.theme.AgriGreen

@Composable
fun AgriAvatar(
    name: String,
    imageUrl: String? = null,
    size: Dp = 56.dp,
    fontSize: TextUnit = 20.sp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(AgriGreen.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            val initial = if (name.isNotBlank() && name != "Loading...") {
                name.trim().take(1).uppercase()
            } else {
                ""
            }

            Text(text = initial, color = AgriGreen, fontWeight = FontWeight.Bold, fontSize = fontSize)
        }
    }
}