package com.example.localizacionInalambrica

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationRequest

fun createLocationRequest() {
    val locationRequest = LocationRequest.create()?.apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
}

internal class LocationBroadcstReciver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACT_LOC") {
            val latitude = intent.getDoubleExtra("Latitude", 0.0)
            val longitud = intent.getDoubleExtra("Longitud", 0.0)
            val altitud = intent.getDoubleExtra("Altitude", 0.0)
            val elapsedRealtimeNanos = intent.getLongExtra("elapseRealtime", 0L)
            val bearing = intent.getFloatExtra("Bearing", 0.0f)
            val accuracy = intent.getFloatExtra("Accuracy", 0.0f)
            val speed = intent.getFloatExtra("speed", 0.0f)
            context.toast( "Lat es: " + latitude + ", "
                    + "Long es: " + longitud)
        }
    }
}