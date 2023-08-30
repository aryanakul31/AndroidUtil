package com.nakul.androidutil.permission_helper

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.nakul.androidutil.R
import com.nakul.androidutil.databinding.FragmentSamplePermissionBinding

class SamplePermissionFragment : PermissionFragment(R.layout.fragment_sample_permission) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FragmentSamplePermissionBinding.bind(view).clMain.setOnClickListener {
            checkPermission()
        }
    }

    override fun getPermissionData(): PermissionData {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        return PermissionData(
            permissions = permissions,
            alertMessage = "Permission is required",
            disabledMessage = "Permission is disabled"
        )
    }

    override fun onPermissionGranted() {
        Log.e(this.javaClass.name, "onPermissionGranted")
    }
}