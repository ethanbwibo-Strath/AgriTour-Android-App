package com.example.agritour.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agritour.ui.theme.AgriGreen

@Composable
fun AgriAvatar(name: String, size: Dp = 56.dp, fontSize: TextUnit = 20.sp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(AgriGreen.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        val initial = if (name.isNotEmpty() && name != "Loading...") {
            name.take(1).uppercase()
        } else "?"

        Text(
            text = initial,
            color = AgriGreen,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize
        )
    }
}