package com.swaraj.kathav1.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.swaraj.kathav1.navigation.clearGuestLogin

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val isGuest = prefs.getBoolean("guest_login", false)
    val username = remember { mutableStateOf(if (isGuest) "Guest User" else getUsernameFromPrefs(context)) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome, ${username.value}!", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (isGuest) {
                clearGuestLogin(context)
            } else {
                auth.signOut()
                prefs.edit().clear().apply()
            }
            Toast.makeText(context, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            navController.navigate("splash") {
                popUpTo(0) { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}

// Function to retrieve username from SharedPreferences
fun getUsernameFromPrefs(context: Context): String {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return prefs.getString("username", "Guest") ?: "Guest"
}
