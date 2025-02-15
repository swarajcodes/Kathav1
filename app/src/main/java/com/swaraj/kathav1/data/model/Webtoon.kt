package com.swaraj.kathav1.data.model

data class Webtoon(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val rating: Double = 0.0,
    val coverImageUrl: String = "",
    val genres: List<String> = emptyList(),
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val id: String = "",
    val title: String = "",
    val episodeNumber: Int = 0,
    val releaseDate: Long = 0L,
    val imageUrls: List<String> = emptyList()
)
