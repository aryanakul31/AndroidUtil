package com.nakul.androidutil.location_helper

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.nakul.androidutil.R


private const val LOCATION_INTERVAL: Long = 10_000L
private const val LOCATION_NOTIFICATION_CHANNEL_ID = 12345

class LocationService : Service() {

    private val manager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    companion object {
        var locationHelper: ILocationHelper? = null
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.e(this.javaClass.name, "onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(LOCATION_NOTIFICATION_CHANNEL_ID, getNotification())
        startListeningUserLocation()
        return START_STICKY
    }

    private fun startListeningUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            stopSelf()
            return
        }

        checkAndRequestGps()

        val mLocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL).apply {
                setMinUpdateIntervalMillis(LOCATION_INTERVAL)
                setIntervalMillis(LOCATION_INTERVAL)
            }.build()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mFusedLocationClient?.requestLocationUpdates(mLocationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationHelper?.onLocationUpdated(
                    location = locationResult.lastLocation ?: return
                )
            }
        }.also {
            mLocationCallback = it
        }, Looper.getMainLooper()
        )
    }

    private fun checkAndRequestGps() {
        val mLocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)
        val result = LocationServices.getSettingsClient(locationHelper?.requireActivity() ?: return)
            .checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener { task ->
            try {
                task.getResult(ApiException::class.java)
            } catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvableApiException = ex as ResolvableApiException
                        locationHelper?.getGPSRequester()?.launch(
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