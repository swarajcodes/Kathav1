package com.swaraj.kathav1.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.swaraj.kathav1.data.model.Webtoon
import com.swaraj.kathav1.ui.viewmodel.WebtoonViewModel

@Composable
fun HomeContent(navController: NavController, viewModel: WebtoonViewModel = viewModel()) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    var username by remember { mutableStateOf(sharedPreferences.getString("username", "Guest") ?: "Guest") }

    // Observe webtoons from the ViewModel
    val webtoons by viewModel.webtoons.collectAsState()

    // Load webtoons once when the composable is first composed
    LaunchedEffect(Unit) {
        viewModel.fetchWebtoons()
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Welcome, $username!",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = {
                FirebaseAuth.getInstance().signOut()
                sharedPreferences.edit().clear().apply()
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                navController.navigate("login") { popUpTo(0) }
            }) {
                Text("Logout")
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(webtoons) { webtoon ->
                WebtoonCard(webtoon = webtoon, onClick = {
                    navController.navigate("webtoon/${webtoon.id}")
                })
            }
        }
    }
}

@Composable
fun WebtoonCard(webtoon: Webtoon, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(webtoon.coverImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = webtoon.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = webtoon.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = webtoon.description,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
