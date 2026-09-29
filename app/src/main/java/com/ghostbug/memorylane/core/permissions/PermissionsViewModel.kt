package com.ghostbug.memorylane.core.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel

class PermissionsViewModel  (
    private val applicationContext: Context
): ViewModel(){

    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeAt(visiblePermissionDialogQueue.lastIndex)  //pop the the first item in the list
    }

    fun onPermissionResult(
        permission: String,
        granted : Boolean
    ){
        if (!granted) {
            visiblePermissionDialogQueue.add(0,permission)
        }
    }
    fun checkPermission(): Boolean {
        return CAMERAX_PERMISSION.all {
            ContextCompat.checkSelfPermission(
                applicationContext ,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    companion object {
         val CAMERAX_PERMISSION = arrayOf(Manifest.permission.CAMERA)
    }
}