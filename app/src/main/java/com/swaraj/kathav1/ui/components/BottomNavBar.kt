package com.swaraj.kathav1.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.swaraj.kathav1.ui.theme.Kathav1Theme

@Composable
fun BottomNavBar(navController: NavHostController) {
    Kathav1Theme {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Don't show the BottomNavBar if on the MangaReaderScreen route
        if (currentRoute?.startsWith("reader/") == true) {
            return@Kathav1Theme
        }
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Library,
            BottomNavItem.Rewards,
            BottomNavItem.Search,
            BottomNavItem.Profile
        )

        NavigationBar(
            modifier = Modifier.height(64.dp),
            containerColor = Color.DarkGray.copy(alpha = 0.8f),
            contentColor = Color.White
        ) {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            items.forEach { item ->
                NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = item.title) },
                    label = { Text(item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFA3D749),
                        selectedTextColor = Color(0xFFA3D749),
                        unselectedIconColor = Color.White,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.DarkGray.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}