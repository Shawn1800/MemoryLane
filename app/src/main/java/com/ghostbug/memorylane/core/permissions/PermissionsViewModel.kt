package com.ghostbug.memorylane.core.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class PermissionsViewModel  (
     val permissionManager: PermissionManager
): ViewModel(){

    val permissionState = permissionManager.permissionState
    private val _uiEvent = MutableSharedFlow<PermissionUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()
     fun  onPermissionChange(requestedPermissions:Map<String, Boolean>)  {
        permissionManager.onPermissionChange(requestedPermissions)
    }
    fun openSettings(): Intent {
        return permissionManager.createSettingsIntent()
    }

    fun  onPermissionGranted() {
        viewModelScope.launch {
            _uiEvent.emit(PermissionUiEvent.onPermissionGranted)
        }
    }

    suspend fun checkPermissions() {
        permissionManager.checkPermissions()
    }

    }

