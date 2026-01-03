package com.example.agritour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.ui.components.AgriAvatar
import com.example.agritour.ui.components.ShimmerConversationItem
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onLearnClick: () -> Unit,
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit,
    onChatClick: (String, String, String?) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val conversations by viewModel.conversations.collectAsState()

    val filteredConversations = remember(searchQuery, conversations) {
        conversations.filter {
            it.peerName.contains(searchQuery, ignoreCase = true) ||
                    it.lastMessage.contains(searchQuery, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadConversations()
    }

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = Color.White,
                modifier = Modifier.statusBarsPadding() // Ensures it starts below the clock
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 16.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextBlack)
                        }
                        Text(
                            "Messages",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search messages...", fontSize = 14.sp, color = Color.Black) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp, top = 4.dp)
                            .height(52.dp),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Black, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null, tint = Color.Black)
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = AgriBackground,
                            focusedContainerColor = AgriBackground,
                            focusedBorderColor = AgriGreen,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextBlack,
                            unfocusedTextColor = TextBlack
                        )
                    )
                }
            }
        },
        containerColor = AgriBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onHomeClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = false,
                    onClick = onExploreClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = onLearnClick,
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGrey, unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = true,
                    onClick = onProfileClick,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AgriGreen,
                        selectedTextColor = AgriGreen,
                        indicatorColor = Color.White)
                    )
            }
        }
    ) { padding ->
        if (conversations.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No messages yet", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                val isLoading = conversations.any { it.peerName == "Loading..." }

                if (isLoading && conversations.isEmpty()) {
                    items(6) { ShimmerConversationItem() }
                } else if (filteredConversations.isEmpty() && searchQuery.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillParentMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No messages found for \"$searchQuery\"", color = Color.Gray)
                        }
                    }
                } else {
                    itemsIndexed(filteredConversations) { index, chat ->
                        ConversationItem(
                            name = chat.peerName,
                            imageUrl = chat.peerImageUrl,
                            lastMessage = chat.lastMessage,
                            timestamp = chat.timestamp,
                            onClick = {
                                val encodedUrl = java.net.URLEncoder.encode(chat.peerImageUrl ?: "", "UTF-8")
                                onChatClick(chat.peerId, chat.peerName, encodedUrl)
                            }
                        )

                        if (index < filteredConversations.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 84.dp, end = 25.dp),
                                thickness = 1.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationItem(
    name: String,
    imageUrl: String?,
    lastMessage: String,
    timestamp: Long,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AgriAvatar(
            name = name,
            imageUrl = imageUrl,
            size = 56.dp
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Text(
                    text = formatTimestamp(timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    val date = Date(timestamp)
    return sdf.format(date)
}