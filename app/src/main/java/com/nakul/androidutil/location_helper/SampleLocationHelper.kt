package com.nakul.androidutil.location_helper

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.nakul.androidutil.R

class SampleLocationHelper : Fragment(R.layout.sample_location_helper), ILocationHelper {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startLocationService()
    }

    private val gpsEnabler =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ -> }

    override fun getGPSRequester(): ActivityResultLauncher<IntentSenderRequest> = gpsEnabler

    override fun onDestroyView() {
        super.onDestroyView()
        stopLocationService()
    }

    /** Get Location Updates*/
    override fun onLocationUpdated(location: Location) {
        Log.e(this.javaClass.name, "Location => ${location.latitude} ${location.longitude}")
    }
}