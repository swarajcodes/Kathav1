package com.swaraj.kathav1.ui.screens.home

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.swaraj.kathav1.navigation.HomeNavGraph
import com.swaraj.kathav1.ui.components.BottomNavBar
import com.swaraj.kathav1.ui.theme.Kathav1Theme
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()
    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Kathav1Theme {
        Scaffold(
            bottomBar = { BottomNavBar(navController) },
            containerColor = Color.Black
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                HomeNavGraph(
                    navController = navController,
                    modifier = Modifier
                )
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Exit Confirmation") },
                text = { Text("Are you sure you want to exit the app?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExitDialog = false
                            (context as? Activity)?.finish()
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showExitDialog = false }
                    ) {
                        Text("No")
                    }
                }
            )
        }

        BackHandler {
            showExitDialog = true
        }
    }
}
