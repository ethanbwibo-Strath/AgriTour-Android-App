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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.agritour.ui.screens.AddFarmScreen
import com.example.agritour.ui.screens.AuthScreen
import com.example.agritour.ui.screens.BookingDetailScreen

import com.example.agritour.ui.screens.BookingScreen
import com.example.agritour.ui.screens.ExploreScreen
import com.example.agritour.ui.screens.FarmDetailScreen
import com.example.agritour.ui.screens.HomeScreen
import com.example.agritour.ui.screens.LearningHubScreen
import com.example.agritour.ui.screens.MyBookingsScreen
import com.example.agritour.ui.screens.MyListingsScreen
import com.example.agritour.ui.screens.ProfileScreen
import com.example.agritour.ui.screens.RevenueScreen
import com.example.agritour.ui.screens.SplashScreen
import com.example.agritour.ui.screens.ChatScreen

@Composable
fun FarmAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.SplashScreen.name
    ) {
        // 0. Splash Screen
        composable(route = AppScreens.SplashScreen.name) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(AppScreens.HomeScreen.name) {
                        popUpTo(AppScreens.SplashScreen.name) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(AppScreens.AuthScreen.name) {
                        popUpTo(AppScreens.SplashScreen.name) { inclusive = true }
                    }
                }
            )
        }

        // 0.5. Auth Screen
        composable(route = AppScreens.AuthScreen.name) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreens.HomeScreen.name) {
                        popUpTo(AppScreens.AuthScreen.name) { inclusive = true }
                    }
                }
            )
        }

        // 1. Home Screen
        composable(route = AppScreens.HomeScreen.name) {
            HomeScreen(
                onFarmClick = { farmId ->
                    navController.navigate("${AppScreens.FarmDetailScreen.name}/$farmId") },
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
                onFarmClick = { farmId ->
                    navController.navigate("${AppScreens.FarmDetailScreen.name}/$farmId") },
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
        composable(
            route = "${AppScreens.FarmDetailScreen.name}/{farmId}",
            arguments = listOf(navArgument("farmId") { type = NavType.StringType })
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getString("farmId") ?: ""
            FarmDetailScreen(
                farmId = farmId,
                onBackClick = { navController.popBackStack() },
                onBookClick = { navController.navigate("${AppScreens.BookingScreen.name}/$farmId") },
                // PASS ARGUMENTS TO ROUTE
                onChatClick = { ownerId, ownerName ->
                    navController.navigate("${AppScreens.ChatScreen.name}/$ownerId/$ownerName")
                }
            )
        }

        // 4. Booking Screen
        composable(
            route = "${AppScreens.BookingScreen.name}/{farmId}", // <--- Accepts ID
            arguments = listOf(navArgument("farmId") { type = NavType.StringType })
        ) { backStackEntry ->
            val farmId = backStackEntry.arguments?.getString("farmId") ?: ""

        BookingScreen(
                farmId = farmId, // <--- We will add this parameter to the screen next
                onBackClick = { navController.popBackStack() },
                onConfirmClick = {
                    // Navigate to Home, clearing the stack so they can't "back" into the booking flow
                    navController.navigate(AppScreens.HomeScreen.name) {
                        popUpTo(AppScreens.HomeScreen.name) { inclusive = true }
                    }
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

        // 6. Profile Screen
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
                },
                onMyBookingsClick = {
                    navController.navigate(AppScreens.MyBookingsScreen.name)
                },
                onMyListingsClick = {
                    navController.navigate(AppScreens.MyListingsScreen.name)
                },
                onRevenueClick = {
                    navController.navigate(AppScreens.RevenueScreen.name)
                },
                onLogoutClick = {
                    navController.navigate(AppScreens.AuthScreen.name) {
                        popUpTo(AppScreens.HomeScreen.name) { inclusive = true }
                    }
                }
            )
        }

        // 7. My Bookings Screen (UPDATED)
        composable(route = AppScreens.MyBookingsScreen.name) {
            MyBookingsScreen(
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
                },
                onProfileClick = {
                    navController.popBackStack()
                },
                onBookingClick = { bookingId ->
                    navController.navigate("BookingDetailScreen/$bookingId") // Navigate to detail
                }
            )
        }

        // 8. Booking Detail Screen (NEW)
        composable(
            route = "BookingDetailScreen/{bookingId}",
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailScreen(
                bookingId = bookingId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 9. My Listings Screen
        composable(route = AppScreens.MyListingsScreen.name) {
            MyListingsScreen(
                onBackClick = { navController.popBackStack() },
                onAddFarmClick = { navController.navigate(AppScreens.AddFarmScreen.name) },
                onFarmClick = { farmId ->
                    navController.navigate("${AppScreens.FarmDetailScreen.name}/$farmId")
                }
            )
        }

        // 10. Add Farm Screen (Placeholder for now)
        composable(route = AppScreens.AddFarmScreen.name) {
            AddFarmScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 11. Revenue Screen
        composable(route = AppScreens.RevenueScreen.name) {
            RevenueScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // 12. Chat Screen
        composable(
            route = "${AppScreens.ChatScreen.name}/{ownerId}/{ownerName}",
            arguments = listOf(
                navArgument("ownerId") { type = NavType.StringType },
                navArgument("ownerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val ownerId = backStackEntry.arguments?.getString("ownerId") ?: ""
            val ownerName = backStackEntry.arguments?.getString("ownerName") ?: "Farm Owner"

            ChatScreen(
                onBackClick = { navController.popBackStack() },
                ownerId = ownerId,
                ownerName = ownerName
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