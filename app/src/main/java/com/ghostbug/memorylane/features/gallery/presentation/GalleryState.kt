package com.ghostbug.memorylane.features.gallery.presentation

import com.ghostbug.memorylane.features.capture.data.dto.MemoriesDto
import com.ghostbug.memorylane.features.capture.domain.model.Memories

data class GalleryState(
    val loading: Boolean = false,
    val memories:List<MemoriesDto>  = emptyList(),
    val error: String? = null

)

