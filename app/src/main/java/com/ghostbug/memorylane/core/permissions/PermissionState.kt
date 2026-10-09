package com.ghostbug.memorylane.core.permissions

data class PermissionState(
    val hasCameraAccess: Boolean,
    val hasLocationAccess: Boolean,
){
    fun hasAllAccess(): Boolean {
        return hasCameraAccess && hasLocationAccess
    }
}
