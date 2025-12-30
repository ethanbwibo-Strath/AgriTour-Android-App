package com.example.agritour.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.agritour.ui.components.AgriAvatar
import com.example.agritour.ui.navigation.AppScreens
import com.example.agritour.ui.theme.AgriBackground
import com.example.agritour.ui.theme.AgriGreen
import com.example.agritour.ui.theme.TextBlack
import com.example.agritour.ui.theme.TextGrey
import com.example.agritour.ui.viewmodel.HomeViewModel

@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onExploreClick: () -> Unit,
    onLearnClick: () -> Unit,
    onMyBookingsClick: () -> Unit,
    onMyListingsClick: () -> Unit,
    onRevenueClick: () -> Unit,
    onConversationClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
    onLogoutClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUser()
    }

    val userProfile by viewModel.currentUser.collectAsState()
    val isFarmer = userProfile?.role == "farmer"

    Scaffold(
        containerColor = AgriBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = false,
                    onClick = onHomeClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGrey,
                        unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = false,
                    onClick = onExploreClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGrey,
                        unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    label = { Text("Learn") },
                    selected = false,
                    onClick = onLearnClick,
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = TextGrey,
                        unselectedTextColor = TextGrey)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") },
                    selected = true,
                    onClick = { /* Already here */ },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AgriGreen,
                        selectedTextColor = AgriGreen,
                        indicatorColor = Color.White)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AgriAvatar(
                        name = userProfile?.name ?: "",
                        size = 100.dp,
                        fontSize = 36.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userProfile?.name ?: "Loading Name...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack
                    )
                    Text(
                        text = userProfile?.email ?: "Loading Email...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Menu Options
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                Text(
                    text = if (isFarmer) "Farm Management" else "My Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )

                MenuCard {
                    if (isFarmer) {
                        // Farmer Menu
                        ProfileMenuItem(
                            icon = Icons.Outlined.DateRange,
                            title = "My Bookings",
                            onClick = onMyBookingsClick
                        )
                        Divider(color = AgriBackground)

                        ProfileMenuItem(
                            icon = Icons.Outlined.List,
                            title = "My Farm Listings",
                            onClick = onMyListingsClick
                        )
                        Divider(color = AgriBackground)

                        ProfileMenuItem(
                            icon = Icons.Outlined.BarChart,
                            title = "Revenue Analytics",
                            onClick = onRevenueClick
                        )
                        Divider(color = AgriBackground)

                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Outlined.Message,
                            title = "Inquiries & Chats",
                            onClick = onConversationClick
                        )
                    } else {
                        // Visitor Menu
                        ProfileMenuItem(
                            icon = Icons.Outlined.DateRange,
                            title = "My Bookings",
                            onClick = onMyBookingsClick
                        )
                        Divider(color = AgriBackground)

                        ProfileMenuItem(
                            icon = Icons.Outlined.FavoriteBorder,
                            title = "Saved Farms",
                            onClick = {}
                        )
                        Divider(color = AgriBackground)

                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Outlined.Message,
                            title = "Chats & Messages",
                            onClick = onConversationClick
                        )
                        Divider(color = AgriBackground)

                        ProfileMenuItem(
                            icon = Icons.Outlined.CreditCard,
                            title = "Payment Methods",
                            onClick = {}
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. General Section
                Text(
                    text = "General",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
                )

                MenuCard {
                    ProfileMenuItem(icon = Icons.Outlined.Settings, title = "Settings", onClick = {})
                    Divider(color = AgriBackground)
                    ProfileMenuItem(icon = Icons.AutoMirrored.Outlined.Help, title = "Help & Support", onClick = {})
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Logout Button (Now Functional)
                Button(
                    onClick = {
                        viewModel.signOut() // 1. Clear Data
                        onLogoutClick()     // 2. Navigate to Auth
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.ExitToApp, null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", color = Color.Red, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- HELPER COMPONENTS ---

fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
)

@Composable
fun MenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(content = content)
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextGrey)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = TextBlack,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Outlined.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}