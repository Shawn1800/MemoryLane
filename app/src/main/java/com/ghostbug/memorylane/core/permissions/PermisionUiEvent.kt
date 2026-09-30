package com.ghostbug.memorylane.core.permissions

sealed  class  PermissionUiEvent {
data object onPermissionGranted: PermissionUiEvent()
}
