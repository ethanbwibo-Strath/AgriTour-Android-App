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

import com.example.agritour.ui.screens.BookingScreen
import com.example.agritour.ui.screens.ExploreScreen
import com.example.agritour.ui.screens.FarmDetailScreen
import com.example.agritour.ui.screens.HomeScreen
import com.example.agritour.ui.screens.LearningHubScreen
import com.example.agritour.ui.screens.ProfileScreen

@Composable
fun FarmAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.HomeScreen.name
    ) {
        // 1. Home Screen
        composable(route = AppScreens.HomeScreen.name) {
            HomeScreen(
                onFarmClick = { navController.navigate(AppScreens.FarmDetailScreen.name) },
                onExploreClick = {
                    navController.navigate(AppScreens.ExploreScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLearnClick = {
                    navController.navigate(AppScreens.LearningHubScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                // Add this if you updated HomeScreen signature, otherwise remove
                onProfileClick = {
                    navController.navigate(AppScreens.ProfileScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // 2. Explore Screen
        composable(route = AppScreens.ExploreScreen.name) {
            ExploreScreen(
                onHomeClick = {
                    navController.navigate(AppScreens.HomeScreen.name) {
                        popUpTo(AppScreens.HomeScreen.name) { inclusive = true }
                    }
                },
                onFarmClick = { navController.navigate(AppScreens.FarmDetailScreen.name) },
                onLearnClick = {
                    navController.navigate(AppScreens.LearningHubScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClick = {
                    navController.navigate(AppScreens.ProfileScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // 3. Farm Detail Screen
        composable(route = AppScreens.FarmDetailScreen.name) {
            FarmDetailScreen(
                onBackClick = { navController.popBackStack() },
                onBookClick = { navController.navigate(AppScreens.BookingScreen.name) }
            )
        }

        // 4. Booking Screen
        composable(route = AppScreens.BookingScreen.name) {
            BookingScreen(
                onBackClick = { navController.popBackStack() },
                onConfirmClick = {
                    navController.popBackStack(AppScreens.HomeScreen.name, inclusive = false)
                }
            )
        }

        // 5. Learning Hub Screen
        composable(route = AppScreens.LearningHubScreen.name) {
            LearningHubScreen(
                onHomeClick = {
                    navController.navigate(AppScreens.HomeScreen.name) {
                        popUpTo(AppScreens.HomeScreen.name) { inclusive = true }
                    }
                },
                onExploreClick = {
                    navController.navigate(AppScreens.ExploreScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onArticleClick = { /* Open Article */ },
                onProfileClick = {
                    navController.navigate(AppScreens.ProfileScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // 6. Profile Screen (NEW)
        composable(route = AppScreens.ProfileScreen.name) {
            ProfileScreen(
                onHomeClick = {
                    navController.navigate(AppScreens.HomeScreen.name) {
                        popUpTo(AppScreens.HomeScreen.name) { inclusive = true }
                    }
                },
                onExploreClick = {
                    navController.navigate(AppScreens.ExploreScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLearnClick = {
                    navController.navigate(AppScreens.LearningHubScreen.name) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

// A temporary helper to visualize screens before we design them
@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = name)
    }
}