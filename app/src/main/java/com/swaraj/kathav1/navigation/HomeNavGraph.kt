package com.swaraj.kathav1.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.swaraj.kathav1.ui.screens.home.*
import com.swaraj.kathav1.ui.screens.reader.MangaReaderScreen
import com.swaraj.kathav1.ui.theme.Kathav1Theme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.runBlocking

@Composable
fun HomeNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val isGuest = isGuestUser(context)

    Kathav1Theme {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeContent(navController) }
            composable("library") { LibraryScreen(navController) }

            // Only allow signed-in users to access Rewards & Profile
            if (!isGuest) {
                composable("rewards") { RewardsScreen(navController) }
                composable("profile") {
                    ProfileScreen(navController = navController)
                }
            }

            composable(
                "webtoon/{webtoonId}",
                arguments = listOf(navArgument("webtoonId") { type = NavType.StringType })
            ) { backStackEntry ->
                val webtoonId = backStackEntry.arguments?.getString("webtoonId") ?: ""
                WebtoonDetailsScreen(navController, webtoonId)
            }

            // ✅ Reader Route (Passes Webtoon Title & Episode Title)
            composable(
                "reader/{webtoonId}/{episodeId}",
                arguments = listOf(
                    navArgument("webtoonId") { type = NavType.StringType },
                    navArgument("episodeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val webtoonId = backStackEntry.arguments?.getString("webtoonId")
                val episodeId = backStackEntry.arguments?.getString("episodeId")

                if (!webtoonId.isNullOrEmpty() && !episodeId.isNullOrEmpty()) {
                    val (webtoonTitle, episodeTitle) = runBlocking {
                        fetchWebtoonAndEpisodeTitles(webtoonId, episodeId)
                    }
                    MangaReaderScreen(navController, webtoonId, episodeId, webtoonTitle, episodeTitle)
                }
            }
        }
    }
}

// Function to check if the user is logged in as a guest
fun isGuestUser(context: Context): Boolean {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    return prefs.getBoolean("guest_login", false)
}

// Function to fetch webtoon title and episode title from Firestore
suspend fun fetchWebtoonAndEpisodeTitles(webtoonId: String, episodeId: String): Pair<String, String> {
    val db = FirebaseFirestore.getInstance()

    return try {
        val webtoonRef = db.collection("webtoons").document(webtoonId).get().await()
        val episodeRef = db.collection("webtoons").document(webtoonId).collection("episodes").document(episodeId).get().await()

        val webtoonTitle = webtoonRef.getString("title") ?: "Webtoon"
        val episodeTitle = episodeRef.getString("title") ?: "Episode $episodeId"

        Pair(webtoonTitle, episodeTitle)
    } catch (e: Exception) {
        Pair("Webtoon", "Episode $episodeId")
    }
}
