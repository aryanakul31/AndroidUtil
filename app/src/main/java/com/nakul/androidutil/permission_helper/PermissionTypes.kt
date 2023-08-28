package com.nakul.androidutil.permission_helper

import android.Manifest.permission
import android.os.Build

enum class PermissionTypes(val permissions: ArrayList<String>, val alertMessage: String) {
    CAMERA(getCameraPermission(), "Camera permission is required."),
}


private fun getCameraPermission(): ArrayList<String> {
    val list = ArrayList<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permission.POST_NOTIFICATIONS
    }
    return list
}