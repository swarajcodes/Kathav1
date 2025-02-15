package com.swaraj.kathav1.ui.screens.reader

import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swaraj.kathav1.ui.viewmodel.ReaderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaReaderScreen(
    navController: NavController,
    webtoonId: String?,
    episodeId: String?,
    webtoonTitle: String?,
    episodeTitle: String?,
    viewModel: ReaderViewModel = viewModel()
) {
    if (webtoonId.isNullOrEmpty() || episodeId.isNullOrEmpty()) {
        navController.popBackStack()
        return
    }

    // Observe the image URLs from the ViewModel
    val imageUrls by viewModel.imageUrls.collectAsState()
    var isFullScreen by remember { mutableStateOf(false) }

    // Load episode images when the screen is composed
    LaunchedEffect(webtoonId, episodeId) {
        viewModel.fetchEpisodeImages(webtoonId, episodeId)
    }

    // Hide system UI for full-screen mode
    val view = LocalView.current
    LaunchedEffect(isFullScreen) {
        if (isFullScreen) {
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        } else {
            view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    Scaffold(
        topBar = {
            if (!isFullScreen) {
                TopAppBar(
                    title = {
                        Column {
                            Text(text = webtoonTitle ?: "Webtoon")
                            Text(
                                text = "Episode: $episodeTitle",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { isFullScreen = !isFullScreen }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle Fullscreen"
                            )
                        }
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable { isFullScreen = !isFullScreen } // Tap to toggle full-screen
        ) {
            if (imageUrls.isEmpty()) {
                // Show a loading indicator if images are being fetched
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                MangaImageList(imageUrls)
            }
        }
    }

    // Handle back gesture in full-screen mode
    BackHandler(enabled = isFullScreen) {
        isFullScreen = false
    }
}

@Composable
fun MangaImageList(imageUrls: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(imageUrls) { imageUrl ->
            MangaImage(imageUrl)
        }
    }
}

@Composable
fun MangaImage(imageUrl: String) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Manga Page",
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentScale = ContentScale.FillWidth
    )
}
