package com.example.localizacionInalambrica

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.localizacionInalambrica.serviceGPS
import com.google.android.gms.location.*

class serviceGPS : Service() {
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationCallback: LocationCallback? = null
    var TAG = "service GPS"
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                Log.d(TAG, "Lat es: " + locationResult.lastLocation.latitude + ", "
                        + "Long es: " + locationResult.lastLocation.longitude)
                val intent = Intent("ACT_LOC",)
                intent.putExtra("Latitude",locationResult.lastLocation.latitude)
                intent.putExtra("Longitud",locationResult.lastLocation.longitude)
                intent.putExtra("Bearing",locationResult.lastLocation.bearing)
                intent.putExtra("Accuracy",locationResult.lastLocation.accuracy)
                intent.putExtra("Altitude",locationResult.lastLocation.altitude)
                intent.putExtra("elapseRealtime",locationResult.lastLocation.elapsedRealtimeNanos)
                intent.putExtra("speed",locationResult.lastLocation.speed)
               sendBroadcast(intent)

            }
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        requestLocation()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun requestLocation() {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        fusedLocationProviderClient!!.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.myLooper())
    }


}