package com.nakul.androidutil.location_helper

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority

abstract class BaseLocationFragment : Fragment() {
    private val gpsEnablerResponse =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) startLocationFetching()
        }

    fun requestGps() {
        val mLocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(requireActivity())
            .checkLocationSettings(settingsBuilder.build())

        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
                startLocationFetching()
            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = ex as ResolvableApiException
                        gpsEnablerResponse.launch(
                            IntentSenderRequest.Builder(resolvableApiException.resolution).build()
                        )
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                    else -> ex.printStackTrace()
                }
            }
        }
    }

    private fun startLocationFetching() {
        LocationService.lastLocation.observe(viewLifecycleOwner) {
            locationUpdated(it ?: return@observe)
        }
        if (canStartService(LocationService::class.java)) {
            requireActivity().startService(
                Intent(requireContext(), LocationService::class.java)
            )
        }
    }

    abstract fun locationUpdated(location: Location)

    private fun canStartService(serviceClass: Class<*>): Boolean {
        val manager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return false
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
    }
}