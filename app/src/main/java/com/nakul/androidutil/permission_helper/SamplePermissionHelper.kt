package com.nakul.androidutil.permission_helper

import android.app.Activity
import android.util.Log


object SamplePermissionHelper {
    fun permissionNotification(activity: Activity) {
        PermissionsUtil.checkPermission(activity, PermissionTypes.NOTIFICATION, onPermissionGiven = {
            Log.e(javaClass.name, "Permission Granted")
        },
            onPermissionRejected = {
                Log.e(javaClass.name, "Permission Denied")
            })
    }
}