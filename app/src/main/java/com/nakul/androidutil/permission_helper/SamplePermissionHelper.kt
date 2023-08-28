package com.nakul.androidutil.permission_helper

import android.app.Activity
import android.util.Log


object SamplePermissionHelper {
    fun permissionCamera(activity: Activity) {
        PermissionsUtil.checkPermission(activity, PermissionTypes.CAMERA, onPermissionGiven = {
            Log.e(javaClass.name, "Permission Granted")
        },
            onPermissionRejected = {
                Log.e(javaClass.name, "Permission Denied")
            })
    }
}