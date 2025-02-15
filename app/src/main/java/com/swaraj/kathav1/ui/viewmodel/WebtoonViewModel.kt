package com.swaraj.kathav1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.swaraj.kathav1.data.model.Webtoon
import com.swaraj.kathav1.data.model.Episode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WebtoonViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _webtoons = MutableStateFlow<List<Webtoon>>(emptyList())
    val webtoons: StateFlow<List<Webtoon>> = _webtoons

    private val _episodes = MutableStateFlow<List<Episode>>(emptyList())
    val episodes: StateFlow<List<Episode>> = _episodes

    /** Fetch all webtoons from Firestore */
    fun fetchWebtoons() {
        viewModelScope.launch {
            try {
                val result = firestore.collection("webtoons").get().await()
                _webtoons.value = result.documents.map { document ->
                    Webtoon(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        description = document.getString("description") ?: "",
                        author = document.getString("author") ?: "",
                        rating = document.getDouble("rating") ?: 0.0,
                        coverImageUrl = document.getString("coverImageUrl") ?: "",
                        genres = document.get("genres") as? List<String> ?: emptyList(),
                        episodes = emptyList() // Episodes can be fetched separately
                    )
                }
            } catch (e: Exception) {
                _webtoons.value = emptyList()
            }
        }
    }

    /** Fetch episodes for a given webtoon */
    fun fetchEpisodes(webtoonId: String) {
        viewModelScope.launch {
            try {
                val result = firestore.collection("webtoons")
                    .document(webtoonId)
                    .collection("episodes")
                    .get()
                    .await()
                _episodes.value = result.documents.map { document ->
                    Episode(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        episodeNumber = document.getLong("episodeNumber")?.toInt() ?: 0,
                        releaseDate = when (val releaseDateField = document.get("releaseDate")) {
                            is Long -> releaseDateField
                            is Timestamp -> releaseDateField.seconds * 1000
                            else -> 0L
                        },
                        imageUrls = document.get("imageUrls") as? List<String> ?: emptyList()
                    )
                }.sortedBy { it.episodeNumber }
            } catch (e: Exception) {
                _episodes.value = emptyList()
            }
        }
    }
}
