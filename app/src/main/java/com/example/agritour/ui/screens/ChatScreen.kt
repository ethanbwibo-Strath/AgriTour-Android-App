package com.example.agritour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.data.ChatMessage
import com.example.agritour.ui.components.AgriAvatar
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth


val timeFormatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBackClick: () -> Unit,
    ownerId: String,
    ownerName: String,
    ownerImageUrl: String?,

    viewModel: HomeViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.chatMessages.collectAsState()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Load messages and handle auto-scroll
    LaunchedEffect(ownerId) {
        if (ownerId.isNotEmpty()) {
            viewModel.loadMessages(ownerId)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = AgriBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AgriAvatar(
                            name = ownerName,
                            imageUrl = ownerImageUrl,
                            size = 40.dp,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = ownerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextBlack
                            )
                            Text("Online", style = MaterialTheme.typography.bodySmall, color = AgriGreen)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextBlack)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Menu */ }) {
                        Icon(Icons.Default.MoreVert, null, tint = TextBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    // FIX: Moves only the bottom bar up with the keyboard
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        // FIX: Handles system navigation bar space
                        .windowInsetsPadding(WindowInsets.navigationBars),
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
                            unfocusedTextColor = TextBlack,
                            focusedBorderColor = AgriGreen,
                            unfocusedBorderColor = Color.LightGray,
                            cursorColor = AgriGreen
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText, ownerId)
                                messageText = ""
                            }
                        },
                        containerColor = AgriGreen,
                        contentColor = Color.White, // Ensures white icon
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            items(messages) { msg ->
                val isMe = msg.senderId == currentUser?.uid
                ChatBubble(message = msg, isMe = isMe)
            }

            item { Spacer(modifier = Modifier.height(6.dp)) }
        }
    }
}
@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    val timeString = timeFormatter.format(java.util.Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(), // Removed .padding(vertical = 10.dp)
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
            modifier = Modifier.widthIn(max = 280.dp)
        ) {

            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text(
                    text = message.text,
                    color = if (isMe) Color.White else TextBlack,
                    fontSize = 15.sp
                )

                Row(
                    modifier = Modifier.align(Alignment.End).padding(top = 1.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeString,
                        color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        fontSize = 10.sp
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}