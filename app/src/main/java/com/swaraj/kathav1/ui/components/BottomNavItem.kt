package com.swaraj.kathav1.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    data object Library : BottomNavItem("library", "Library", Icons.Default.FavoriteBorder)
    data object Rewards : BottomNavItem("rewards", "Rewards", Icons.Default.Star)
    data object Search : BottomNavItem("search", "Search", Icons.Default.Search)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}