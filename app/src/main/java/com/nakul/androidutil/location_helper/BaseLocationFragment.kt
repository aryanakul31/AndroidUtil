package com.nakul.androidutil.location_helper

import android.app.Activity
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

    abstract fun isPermissionGranted(): Boolean
    abstract fun locationUpdated(location: Location)

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (isPermissionGranted())
            requestGps()
    }

    private fun startLocationFetching() {
        LocationService.lastLocation.observe(viewLifecycleOwner) {
            locationUpdated(it ?: return@observe)
        }

        requireActivity().startService(
            Intent(requireContext(), LocationService::class.java)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().stopService(Intent(requireContext(), LocationService::class.java))
    }
}