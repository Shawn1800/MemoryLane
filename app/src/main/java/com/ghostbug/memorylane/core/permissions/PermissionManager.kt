package com.ghostbug.memorylane.core.permissions

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.jar.Manifest

class PermissionManager(val context : Context) {
    private val appContext = context.applicationContext
    companion object{
        val REQUIRED_PERMISSIONS_PRE_T = arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            CAMERA
        )
        val REQUIRED_PERMISSIONS_POST_T=arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            CAMERA
        )
    }


    private val _permissionState = MutableStateFlow(PermissionState(
        hasCameraAccess = hasAccess(android.Manifest.permission.CAMERA),
        hasLocationAccess = hasAccess(listOf(ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION))
    ))

    val permissionState = _permissionState.asStateFlow()

    private fun hasAccess(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            appContext,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun hasAccess(permissions: List<String>): Boolean {
        return permissions.all(::hasAccess)
    }

      fun onPermissionChange(permissions: Map<String, Boolean>) {
        val hasLocationAccess = hasAccess(ACCESS_FINE_LOCATION) && hasAccess(ACCESS_COARSE_LOCATION)
        val hasStorageAccess = hasAccess(android.Manifest.permission.READ_MEDIA_IMAGES) || hasAccess(READ_EXTERNAL_STORAGE)

        _permissionState.value = PermissionState(
            hasCameraAccess = permissions[CAMERA] ?: _permissionState.value.hasCameraAccess,
            hasLocationAccess = hasLocationAccess
        )
    }

    suspend fun checkPermissions() {
        val newState = PermissionState(
            hasCameraAccess = hasAccess(CAMERA),
            hasLocationAccess = hasAccess(ACCESS_FINE_LOCATION) && hasAccess(ACCESS_COARSE_LOCATION)
        )

        _permissionState.emit(newState)
    }

     fun createSettingsIntent(): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            data = Uri.fromParts("package", context.packageName, null)
        }

        return intent
    }
}






