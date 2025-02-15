package com.swaraj.kathav1.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.swaraj.kathav1.data.model.Episode
import com.swaraj.kathav1.data.model.Webtoon
import kotlinx.coroutines.tasks.await

class WebtoonRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val webtoonsCollection = firestore.collection("webtoons")

    /**      Fetch All Webtoons **/
    suspend fun getWebtoons(): List<Webtoon> {
        return try {
            val result = webtoonsCollection.get().await()
            result.documents.map { document ->
                Webtoon(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    description = document.getString("description") ?: "",
                    author = document.getString("author") ?: "",
                    rating = document.getDouble("rating") ?: 0.0,
                    coverImageUrl = document.getString("coverImageUrl") ?: "",
                    genres = document.get("genres") as? List<String> ?: emptyList()
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** Fetch Webtoon by ID **/
    suspend fun getWebtoonById(webtoonId: String): Webtoon? {
        return try {
            val document = webtoonsCollection.document(webtoonId).get().await()
            if (document.exists()) {
                Webtoon(
                    id = document.id,
                    title = document.getString("title") ?: "",
                    description = document.getString("description") ?: "",
                    author = document.getString("author") ?: "",
                    rating = document.getDouble("rating") ?: 0.0,
                    coverImageUrl = document.getString("coverImageUrl") ?: "",
                    genres = document.get("genres") as? List<String> ?: emptyList()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Fetch All Episodes of a Webtoon //
    suspend fun getEpisodes(webtoonId: String): List<Episode> {
        return try {

            val episodesCollection = FirebaseFirestore.getInstance()
                .collection("webtoons").document(webtoonId).collection("episodes")

            val result = episodesCollection.get().await()

            if (result.isEmpty) {
            } else {
                result.documents.forEach { document ->
                }
            }

            result.documents.mapNotNull { document ->
                val title = document.getString("title")
                val episodeNumber = document.getLong("episodeNumber")?.toInt()
                val releaseDateField = document.get("releaseDate") // 🔥 Fix: Properly handle `Timestamp`
                val imageUrls = document.get("imageUrls") as? List<String>

                val releaseDate: Long = when (releaseDateField) {
                    is Long -> releaseDateField
                    is com.google.firebase.Timestamp -> releaseDateField.seconds * 1000 // Convert to milliseconds
                    else -> {
                        0L
                    }
                }

                if (title == null || episodeNumber == null || releaseDate == 0L || imageUrls == null) {
                    null
                } else {
                    Episode(
                        id = document.id,
                        title = title,
                        episodeNumber = episodeNumber,
                        releaseDate = releaseDate,
                        imageUrls = imageUrls
                    )
                }
            }.sortedBy { it.episodeNumber }
        } catch (e: Exception) {
            emptyList()
        }
    }

}
