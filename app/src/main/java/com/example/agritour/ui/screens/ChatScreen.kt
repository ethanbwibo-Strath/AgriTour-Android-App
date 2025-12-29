package com.example.agritour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.data.ChatMessage
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    ownerId: String,
    ownerName: String,
    viewModel: HomeViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.chatMessages.collectAsState() // Observe Real Data
    val currentUser = FirebaseAuth.getInstance().currentUser

    // 1. Load Messages when screen opens
    LaunchedEffect(ownerId) {
        if (ownerId.isNotEmpty()) {
            viewModel.loadMessages(ownerId)
        }
    }

    Scaffold(
        containerColor = AgriBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.Person, null, tint = Color.White) }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(ownerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextBlack)
                            Text("Online", style = MaterialTheme.typography.bodySmall, color = AgriGreen)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextBlack,
                        focusedBorderColor = AgriGreen,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                FloatingActionButton(
                    onClick = {
                        // 2. Send Real Message
                        if (messageText.isNotBlank()) {
                            viewModel.sendMessage(messageText, ownerId)
                            messageText = "" // Clear input
                        }
                    },
                    containerColor = AgriGreen,
                    contentColor = Color.White,
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp))
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Bottom // Keep at bottom
        ) {
            // 3. Display Real Messages
            items(messages) { msg ->
                val isMe = msg.senderId == currentUser?.uid
                ChatBubble(message = msg.text, isMe = isMe)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ChatBubble(message: String, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 16.dp
            ),
            color = if (isMe) AgriGreen else Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp).padding(vertical = 4.dp)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isMe) Color.White else TextBlack,
                fontSize = 15.sp
            )
        }
    }
}