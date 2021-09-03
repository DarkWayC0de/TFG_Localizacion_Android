package com.example.localizacionInalambrica.servicios

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.notification.Notification.crearNotificationChannel
import com.example.localizacionInalambrica.other.Constants.ACTION_PAUSE_SERVICE_RASTREO
import com.example.localizacionInalambrica.other.Constants.ACTION_START_OR_RESUME_SERVICE_RASTREO
import com.example.localizacionInalambrica.other.Constants.ACTION_STOP_SERVICE_RASTREO
import com.example.localizacionInalambrica.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.localizacionInalambrica.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.localizacionInalambrica.other.Constants.NOTIFICATION_ID
import com.example.localizacionInalambrica.permisos.Permissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServicioRastreo : LifecycleService() {
    private val TAG = "ServicioRastreo"
    private var isFirstRun = true
    private var serverKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var actualNotificationBuilder: NotificationCompat.Builder


    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val actualPosition = MutableLiveData<Location?>()

    }

    private fun postInitalValues() {
        isTracking.postValue(false)
        actualPosition.postValue(null)
    }

    override fun onCreate() {
        super.onCreate()
        actualNotificationBuilder = baseNotificationBuilder
        postInitalValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            actualizarNotificacionUbicacion(it)
        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE_RASTREO -> {
                    if (isFirstRun) {
                        starForegroundService()
                        isFirstRun = false
                        Log.d(TAG, "Iniciado")
                    } else {
                        Log.d(TAG, "Recuperado")
                    }

                }
                ACTION_PAUSE_SERVICE_RASTREO -> {
                    Log.d(TAG, "ServicioRastreo Pausado")
                    pauseService()
                }
                ACTION_STOP_SERVICE_RASTREO -> {
                    Log.d(TAG, "ServicioRastreo Terminado")
                    killService()
                }

                else -> {}
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
    private fun starForegroundService() {
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        crearNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        actualPosition.observe(
            this, Observer {
                if (it != null && !serverKilled) {
                    val notification = actualNotificationBuilder
                        .setContentText(
                            "Log: " + it.longitude.toString() + ", Lat: " + it.latitude.toString() + "\n"
                                    + "Alt: " + it.altitude.toString()
                        )
                    notificationManager.notify(NOTIFICATION_ID, notification.build())
                }
            }

        )

    }
    private fun pauseService() {
        isTracking.postValue(false)
    }
    private fun modActualPosition(location: Location?){
        location.let {
            actualPosition.postValue(location)
        }
    }
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking :Boolean){

        if(isTracking) {
            if (Permissions.hastLocationAndBluetoothPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object  : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result.locations.let { locations ->
                    for (location in locations) {
                        modActualPosition(location)
                        Log.d(TAG, "Nueva localizacion ${location.toString()}")
                        //CAMBIAR  texto notificacion

                    }
                }
            }
        }

    }




    private fun actualizarNotificacionUbicacion(isTracking: Boolean) {
        val notificationActionText =
            if (isTracking) {
                getString(R.string.botonCerrarNotificacion)
            } else {
                getString(R.string.botonContinuarNotificacion)
            }

        val pendingIntent =
            if (isTracking) {
                val cerrarIntent = Intent(this, ServicioRastreo::class.java).apply {
                    action = ACTION_STOP_SERVICE_RASTREO
                }
                PendingIntent.getService(this, 1, cerrarIntent, FLAG_UPDATE_CURRENT)
            } else {
                val comenzarIntent = Intent(this, ServicioRastreo::class.java).apply {
                    action = ACTION_START_OR_RESUME_SERVICE_RASTREO
                }
                PendingIntent.getService(this, 1, comenzarIntent, FLAG_UPDATE_CURRENT)
            }
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        actualNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(actualNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if (!serverKilled) {
            actualNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black_24dp, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, actualNotificationBuilder.build())
        }
    }

    private fun killService() {
        serverKilled = true
        isFirstRun = true
        pauseService()
        postInitalValues()
        stopForeground(true)
        stopSelf()

    }

}