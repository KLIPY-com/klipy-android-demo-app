package com.klipy.demoapp.data.mapper

import com.klipy.demoapp.data.base64toBitmap
import com.klipy.demoapp.data.dto.FileMetaDataDto
import com.klipy.demoapp.data.dto.MediaItemDto
import com.klipy.demoapp.domain.models.MediaItem
import com.klipy.demoapp.domain.models.MetaData
import com.klipy.demoapp.presentation.features.conversation.model.MediaType
import java.util.UUID

class MediaItemMapperImpl : MediaItemMapper {
    override fun mapToDomain(data: MediaItemDto): MediaItem {
        return when (data) {
            is MediaItemDto.ClipMediaItemDto -> {
                val selector = data.file?.gif?.let {
                    MetaData(
                        url = it,
                        width = data.fileMeta!!.gif!!.width!!,
                        height = data.fileMeta.gif!!.height!!
                    )
                }
                val preview = data.file?.mp4?.let {
                    MetaData(
                        url = it,
                        width = data.fileMeta!!.mp4!!.width!!,
                        height = data.fileMeta.mp4!!.height!!
                    )
                }
                MediaItem(
                    id = data.slug!!,
                    placeHolder = data.placeHolder?.base64toBitmap(),
                    lowQualityMetaData = selector,
                    highQualityMetaData = preview,
                    mediaType = MediaType.CLIP
                )
            }

            is MediaItemDto.GeneralMediaItemDto -> {
                val fileType = data.file?.run {
                    sm ?: md ?: hd ?: xs
                }
                val highDefFileType = data.file?.run {
                    hd ?: md ?: sm ?: xs
                }
                MediaItem(
                    id = data.slug!!,
                    placeHolder = data.placeHolder?.base64toBitmap(),
                    lowQualityMetaData = fileType?.gif?.mapToDomain(),
                    highQualityMetaData = highDefFileType?.gif?.mapToDomain(),
                    mediaType = if (data.type == "gif") {
                        MediaType.GIF
                    } else {
                        MediaType.STICKER
                    }
                )
            }

            is MediaItemDto.AdMediaItemDto -> {
                val metaData = MetaData(
                    url = data.content!!,
                    width = data.width!!,
                    height = data.height!!
                )
                MediaItem(
                    id = "ad-${UUID.randomUUID()}",
                    placeHolder = null,
                    lowQualityMetaData = metaData,
                    highQualityMetaData = null,
                    mediaType = MediaType.AD
                )
            }
        }
    }

    private fun FileMetaDataDto.mapToDomain() =
        MetaData(
            url = url!!,
            width = width!!,
            height = height!!
        )
}