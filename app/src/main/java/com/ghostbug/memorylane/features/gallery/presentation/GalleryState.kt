package com.ghostbug.memorylane.features.gallery.presentation

import com.ghostbug.memorylane.features.capture.domain.model.Memories

data class GalleryState(
    val loading: Boolean = false,
    val memories:List<Memories>  = emptyList(),
    val error: String? = null

)

