package com.nakul.androidutil.location_helper

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.nakul.androidutil.R

const val LOCATION_NOTIFICATION_CHANNEL_ID = 12345

class LocationService : Service() {
    private val manager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    private val runOnce = false
    private val interval = 5_000L

    companion object {
        var lastLocation = MutableLiveData<Location?>()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationTracking()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationCallback?.let { mFusedLocationClient?.removeLocationUpdates(it) }
    }

    private fun startLocationTracking() {
        startForeground(LOCATION_NOTIFICATION_CHANNEL_ID, getNotification())
        startListeningUserLocation()
    }

    private fun startListeningUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
        } else {
            val mLocationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).build()
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            mFusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                shareLocation(location ?: return@addOnSuccessListener)
            }

            mFusedLocationClient?.requestLocationUpdates(mLocationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        shareLocation(locationResult.lastLocation ?: return)
                    }
                }.also {
                    mLocationCallback = it
                },
                Looper.getMainLooper()
            )
        }
    }

    private fun shareLocation(location: Location) {
        lastLocation.postValue(location)
        if (runOnce)
            stopSelf()
    }


    private fun getNotification(
        channelId: String = "location", description: String = getString(R.string.fetching_location)
    ): Notification {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) manager.createNotificationChannel(
            NotificationChannel(
                channelId, description, NotificationManager.IMPORTANCE_HIGH
            )
        )

        return with(NotificationCompat.Builder(applicationContext, channelId)) {
            setContentTitle(getString(R.string.app_name))
            setContentText(description)
            priority = NotificationCompat.PRIORITY_HIGH
            setSmallIcon(R.mipmap.ic_launcher)
            setAutoCancel(false)
            setOngoing(true)
            build()
        }
    }
}