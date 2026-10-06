package com.ghostbug.memorylane.features.camera.domain.model

import kotlin.time.Instant

data class SaveMemory(
    val userId : String ,
    val image : String ,
    val description:String,
    val latitude : Double ,
    val longitude : Double ,
    val timeStamp : Instant
)