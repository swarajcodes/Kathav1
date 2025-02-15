package com.swaraj.kathav1.ui.screens.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.swaraj.kathav1.R

@Composable
fun SplashScreenContent(
    onNavigateToLogin: () -> Unit,
    onNavigateToGuest: () -> Unit
) {
    val context = LocalContext.current

    // Auto-login check
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val isGuest = prefs.getBoolean("guest_login", false)
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null || isGuest) {
            onNavigateToGuest()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            painter = painterResource(id = R.drawable.katha_logo1),
            contentDescription = "Katha Logo",
            modifier = Modifier.size(180.dp)
        )

        Text(
            text = "India's #1 Webtoon Platform",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = "Continue with Phone Number",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Guest Login Button
        Text(
            text = "Continue as Guest",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.background,
            fontWeight = FontWeight.Medium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                setGuestLogin(context)
                onNavigateToGuest()
            }
        )

        Spacer(modifier = Modifier.height(64.dp))
    }
}

// Function to store guest login state
fun setGuestLogin(context: Context) {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("guest_login", true).apply()
}
