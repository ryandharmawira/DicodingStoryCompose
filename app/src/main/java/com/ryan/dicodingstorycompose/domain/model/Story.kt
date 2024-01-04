package com.ryan.dicodingstorycompose.domain.model

import java.time.LocalDateTime

data class Story(
    val id: String,
    val authorName: String,
    val description: String,
    val photoUrl: String,
    val createdAt: LocalDateTime,
    val lat: Double? = null,
    val lon: Double? = null,
)