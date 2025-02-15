package com.swaraj.kathav1.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReaderViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _imageUrls = MutableStateFlow<List<String>>(emptyList())
    val imageUrls: StateFlow<List<String>> = _imageUrls

    /** Fetch images for a specific episode */
    fun fetchEpisodeImages(webtoonId: String, episodeId: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("webtoons")
                    .document(webtoonId)
                    .collection("episodes")
                    .document(episodeId)
                    .get()
                    .await()
                _imageUrls.value = snapshot.get("imageUrls") as? List<String> ?: emptyList()
            } catch (e: Exception) {
                _imageUrls.value = emptyList()
            }
        }
    }
}
