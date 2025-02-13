package com.klipy.demoapp.data.mapper

import com.klipy.demoapp.data.dto.MediaItemDto
import com.klipy.demoapp.domain.models.MediaItem

interface MediaItemMapper {

    fun mapToDomain(data: MediaItemDto): MediaItem
}
