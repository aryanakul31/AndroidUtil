package com.nakul.androidutil.location_helper

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest

interface ILocationHelper {
    fun onLocationUpdated(location: Location)
    fun requireActivity(): Activity
    fun getGPSRequester(): ActivityResultLauncher<IntentSenderRequest>

    fun startLocationService(){
        val locationService = Intent(requireActivity(), LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) requireActivity().startForegroundService(
            locationService
        )
        else requireActivity().startService(locationService)

        LocationService.locationHelper = this
    }
    fun stopLocationService() {
        val locationService = Intent(requireActivity(), LocationService::class.java)
        requireActivity().stopService(locationService)
    }
}