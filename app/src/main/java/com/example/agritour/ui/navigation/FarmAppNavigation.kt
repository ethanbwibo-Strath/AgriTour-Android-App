package com.example.agritour.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.agritour.ui.screens.FarmDetailScreen
import com.example.agritour.ui.screens.HomeScreen

@Composable
fun FarmAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.HomeScreen.name
    ) {
        // 1. Home Screen Route
        composable(route = AppScreens.HomeScreen.name) {
            HomeScreen(
                onFarmClick = {
                    // Navigate to details (We will implement the real link later)
                    navController.navigate(AppScreens.FarmDetailScreen.name)
                }
            )
        }

        // 2. Explore Screen Route
        composable(route = AppScreens.ExploreScreen.name) {
            PlaceholderScreen("Explore Farms")
        }

        // 3. Farm Details Route
        composable(route = AppScreens.FarmDetailScreen.name) {
            FarmDetailScreen(
                onBackClick = {
                    navController.popBackStack() // Go back to Home
                },
                onBookClick = {
                    navController.navigate(AppScreens.BookingScreen.name)
                }
            )
        }

        // 4. Booking Screen Route (Still a placeholder for now)
        composable(route = AppScreens.BookingScreen.name) {
            PlaceholderScreen("Booking Flow - Select Date & Group")
        }

        // ... We will add the other routes as we build the screens
    }
}

// A temporary helper to visualize screens before we design them
@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = name)
    }
}