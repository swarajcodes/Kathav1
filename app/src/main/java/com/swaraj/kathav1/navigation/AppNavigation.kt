package com.swaraj.kathav1.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.swaraj.kathav1.ui.screens.auth.LoginScreen
import com.swaraj.kathav1.ui.screens.auth.OTPScreen
import com.swaraj.kathav1.ui.screens.auth.SplashScreenContent
import com.swaraj.kathav1.ui.screens.home.HomeScreen
import com.swaraj.kathav1.ui.screens.home.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isGuest = prefs.getBoolean("guest_login", false)
    val username = prefs.getString("username", null)
    val startDestination = when {
        auth.currentUser != null || (isGuest && username != null) -> "home"
        else -> "splash"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            SplashScreenContent(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToGuest = {
                    setGuestLogin(context)
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                onNavigateToOTP = { verificationId, phone ->
                    navController.navigate("otp/$verificationId/$phone")
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("otp/{verificationId}/{phoneNumber}") { backStackEntry ->
            OTPScreen(
                verificationId = backStackEntry.arguments?.getString("verificationId") ?: "",
                phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: "",
                navController = navController,
                onNavigateBack = { navController.popBackStack() } // ✅ Fix: Add back navigation
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(navController)
        }
    }
}

// Function to set guest login in SharedPreferences
fun setGuestLogin(context: Context) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("guest_login", true).apply()
}

// Function to clear guest session on logout
fun clearGuestLogin(context: Context) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().remove("guest_login").apply()
}
