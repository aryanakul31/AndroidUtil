package com.nakul.androidutil.permission_helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


object PermissionsUtil {

    private fun checkManifestAndAdd(context: Context): Array<out String>? {
        val pm: PackageManager = context.packageManager
        val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
            )
        } else {
            @Suppress("DEPRECATION") pm.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
        }
        return packageInfo.requestedPermissions
    }

    fun checkPermission(
        activity: Activity,
        permissionType: PermissionTypes,
        onPermissionGiven: () -> Unit = {},
        onPermissionRejected: (() -> Unit)? = null,
    ) {
        val list = ArrayList<String>()
        val manifestPermissions = checkManifestAndAdd(activity)

        permissionType.permissions.forEach { permission ->
            if (manifestPermissions?.contains(permission) == true) {
                list.add(permission)
            } else {
                Log.e(javaClass.simpleName, "Manifest missing permission $permission")
                onPermissionRejected?.invoke()
                return
            }
        }


        if (list.isEmpty()) onPermissionGiven.invoke()
        else checkPermissions(
            activity,
            list,
            onPermissionGiven = onPermissionGiven,
            onPermissionRejected = {
                permissionAlert(activity, permissionType) { canRequest ->
                    if (canRequest)
                        checkPermission(
                            activity,
                            permissionType,
                            onPermissionGiven,
                            onPermissionRejected
                        )
                    else onPermissionRejected?.invoke()
                }
            },
            onPermissionDisabled = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity.packageName, null)
                intent.data = uri
                activity.startActivity(intent)
                onPermissionRejected?.invoke()
            },
        )
    }

    /**Check Permission*/
    private fun checkPermissions(
        activity: Activity,
        list: ArrayList<String>,
        onPermissionGiven: () -> Unit = {},
        onPermissionRejected: () -> Unit = {},
        onPermissionDisabled: () -> Unit = {},
    ) = try {
        Dexter.withContext(activity).withPermissions(list)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    when {
                        p0?.areAllPermissionsGranted() == true -> onPermissionGiven.invoke()
                        p0?.isAnyPermissionPermanentlyDenied == true -> onPermissionDisabled.invoke()
                        else -> onPermissionRejected.invoke()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    /**Alert for Permission Necessity*/
    private fun permissionAlert(
        context: Activity,
        permissionType: PermissionTypes,
        requestPermission: (Boolean) -> Unit
    ) {
        val aD = android.app.AlertDialog.Builder(context)
        aD.setTitle(permissionType.alertMessage)
        aD.setCancelable(false)
        aD.setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
            requestPermission(true)
        }
        aD.setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
            dialogInterface.cancel()
            requestPermission(false)
        }
        aD.create()
        aD.show()
    }
}