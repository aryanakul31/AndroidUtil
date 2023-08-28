package com.nakul.androidutil.location_helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.nakul.androidutil.R
import com.nakul.androidutil.location_helper.BaseLocationFragment

class SampleLocationHelper : BaseLocationFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sample_location_helper, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getPermissions()
    }

    private fun getPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        requestGps()
    }

    override fun locationUpdated(location: Location) {
        Log.e("Location","Location => ${location.latitude} ${location.longitude}")
        view?.findViewById<TextView>(R.id.tvLocation)?.text =
            "Location => ${location.latitude} ${location.longitude}"
    }
}