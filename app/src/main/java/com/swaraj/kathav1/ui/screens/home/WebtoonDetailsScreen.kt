package com.swaraj.kathav1.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.swaraj.kathav1.data.model.Episode
import com.swaraj.kathav1.data.model.Webtoon
import com.swaraj.kathav1.data.repository.WebtoonRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebtoonDetailsScreen(navController: NavController, webtoonId: String) {
    val webtoonRepository = remember { WebtoonRepository() }
    var webtoon by remember { mutableStateOf<Webtoon?>(null) }
    var episodes by remember { mutableStateOf<List<Episode>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(webtoonId) {
        coroutineScope.launch {
            webtoon = webtoonRepository.getWebtoonById(webtoonId)
            episodes = webtoonRepository.getEpisodes(webtoonId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Webtoon Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Webtoon Cover Image
            item {
                webtoon?.let {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.coverImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Webtoon Cover",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Webtoon Title & Info
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = it.title,
                            color = Color.White,
                            fontSize = 24.sp,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "By ${it.author}",
                            color = Color(0xFFA3D749),
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "⭐ ${it.rating} / 5",
                            color = Color(0xFFFFD700),
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = it.description,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Episodes",
                            color = Color.White,
                            fontSize = 18.sp,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            // Display Episodes
            items(episodes) { episode ->
                EpisodeItem(navController, webtoonId, episode)
            }
        }
    }
}

@Composable
fun EpisodeItem(navController: NavController, webtoonId: String, episode: Episode) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = episode.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Episode ${episode.episodeNumber}",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    val encodedWebtoonId = Uri.encode(webtoonId.trim())
                    val encodedEpisodeId = Uri.encode(episode.id.trim())
                    val route = "reader/$encodedWebtoonId/$encodedEpisodeId"
                    navController.navigate(route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3D749))
            ) {
                Text("Read")
            }
        }
    }
}
