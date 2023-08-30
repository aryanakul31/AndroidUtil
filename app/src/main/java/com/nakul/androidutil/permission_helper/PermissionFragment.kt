package com.nakul.androidutil.permission_helper

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

abstract class PermissionFragment(
    @LayoutRes layout: Int
) : Fragment(layout) {

    data class PermissionData(
        val permissions: ArrayList<String>,
        val alertMessage: String,
        val disabledMessage: String,
    )

    private val permissionRequester =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            for (permission in permissions.keys) {
                if (permissions[permission] == true) {
                    continue
                } else {
                    disabledHandling()
                    return@registerForActivityResult
                }
            }
            onPermissionGranted()
        }

    private fun disabledHandling() {
        Log.e(javaClass.simpleName, "Permission disabled")
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setCancelable(false)
        alertDialog.setTitle(getPermissionData().disabledMessage)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts(
                "package",
                requireContext().packageName,
                null
            )
            intent.data = uri
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        alertDialog.setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
            dialogInterface.cancel()
            onPermissionRejected()
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun showRationaleHandling() {
        Log.e(javaClass.simpleName, "shouldShowRequestPermissionRationale")
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setCancelable(false)
        alertDialog.setTitle(getPermissionData().alertMessage)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
            dialogInterface.dismiss()
            permissionRequester.launch(getFinalisedPermissions()?.toTypedArray())
        }
        alertDialog.setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
            dialogInterface.cancel()
            onPermissionRejected()
        }
        alertDialog.create()
        alertDialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    fun checkPermission() {
        val list = getFinalisedPermissions()
        when {
            list == null -> onPermissionRejected()
            list.isEmpty() -> onPermissionGranted()
            list.any { shouldShowRequestPermissionRationale(it) } -> showRationaleHandling()
            else -> permissionRequester.launch(list.toTypedArray())
        }
    }

    private fun getFinalisedPermissions(): ArrayList<String>? {
        val list = ArrayList<String>()
        val manifestPermissions = getManifestPermissions(requireActivity())

        getPermissionData().permissions.forEach { permission ->
            when {
                manifestPermissions?.contains(permission) != true -> {
                    Log.e(javaClass.simpleName, "Manifest missing permission $permission")
                    return null
                }

                !hasPermission(permission) -> {
                    list.add(permission)
                }
            }
        }
        return list
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun getManifestPermissions(context: Context): Array<out String>? {
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

    open fun onPermissionRejected() = Unit
    abstract fun getPermissionData(): PermissionData
    abstract fun onPermissionGranted()
}