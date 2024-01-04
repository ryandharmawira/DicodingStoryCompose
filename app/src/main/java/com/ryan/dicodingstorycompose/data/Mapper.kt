package com.ryan.dicodingstorycompose.data

import com.ryan.dicodingstorycompose.common.toLocalDateTime
import com.ryan.dicodingstorycompose.data.remote.dto.StoryDto
import com.ryan.dicodingstorycompose.domain.model.Story

fun StoryDto.toStory(): Story {
    return Story(
        id = id,
        authorName = name,
        description = description,
        photoUrl = photoUrl,
        createdAt = createdAt.toLocalDateTime(),
        lat = lat,
        lon = lon,
    )
}