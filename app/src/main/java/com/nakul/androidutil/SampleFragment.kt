package com.nakul.androidutil

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.nakul.androidutil.databinding.FragmentSampleBinding
import com.nakul.androidutil.location_helper.ILocationHelper
import com.nakul.androidutil.permission_helper.PermissionFragment

class SampleFragment : PermissionFragment(R.layout.fragment_sample), ILocationHelper {

    private var binding: FragmentSampleBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSampleBinding.bind(view)

        binding?.clMain?.setOnClickListener {
            checkPermission()
        }
    }

    override fun getPermissionData(): PermissionData {
        val permissions = ArrayList<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            permissions.add(android.Manifest.permission.FOREGROUND_SERVICE)
        }
        return PermissionData(
            permissions = permissions,
            alertMessage = "Permission is required",
            disabledMessage = "Permission is disabled"
        )
    }

    override fun onPermissionGranted() {
        Log.e(this.javaClass.name, "onPermissionGranted")
        startLocationService()
    }

    override fun onLocationUpdated(location: Location) {
        binding?.tvLocation?.text = "${location.latitude} ${location.longitude}"
    }

    private val gpsEnabler =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ -> }

    override fun getGPSRequester(): ActivityResultLauncher<IntentSenderRequest> = gpsEnabler
    override fun onDestroy() {
        super.onDestroy()
        stopLocationService()
    }
}