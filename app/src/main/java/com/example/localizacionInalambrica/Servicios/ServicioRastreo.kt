package com.example.localizacionInalambrica.Servicios

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.localizacionInalambrica.*
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.other.Constants.ACTION_PAUSE_SERVICE
import com.example.localizacionInalambrica.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.localizacionInalambrica.other.Constants.ACTION_STOP_SERVICE
import com.example.localizacionInalambrica.other.Constants.NOTIFICATION_CHANEL_ID
import com.example.localizacionInalambrica.other.Constants.NOTIFICATION_CHANEL_NAME
import com.example.localizacionInalambrica.other.Constants.ACTION_SHOW_HOME_FRAGMEWNT
import com.example.localizacionInalambrica.other.Constants.FASTEST_LOCATION_INTERVAL
import com.example.localizacionInalambrica.other.Constants.LOCATION_UPDATE_INTERVAL
import com.example.localizacionInalambrica.other.Constants.NOTIFICATION_ID
import com.example.localizacionInalambrica.permisos.permissions
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import javax.inject.Inject


class ServicioRastreo : LifecycleService() {
    private val TAG = "ServicioRastreo"
    private var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    companion object{
       val  isTracking = MutableLiveData<Boolean>()
       val actualPosition = MutableLiveData<Location?>()

    }
    private fun postInitalValues(){
        isTracking.postValue(false)
        actualPosition.postValue(null)
    }

    override fun onCreate() {
        super.onCreate()
        postInitalValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE->{
                    if(isFirstRun){
                        starForegroundService()
                        isFirstRun = false
                        Log.d(TAG,"Iniciado")
                    } else {
                        Log.d(TAG, "Recuperado")
                    }

                }
                ACTION_PAUSE_SERVICE->{
                    Log.d( TAG,"ServicioRastreo Pausado")
                    pauseService()
                }
                ACTION_STOP_SERVICE->{
                    Log.d(TAG,"ServicioRastreo Terminado")
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
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_my_location_24)
            .setContentTitle(getString(R.string.tituloServicioRastreoNotificacion))
            .setContentText("0,0")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID,notificationBuilder.build())

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
        if(isTracking){
           if(permissions.hastLocationPermissions(this)){
            val request = LocationRequest().apply {
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
                result?.locations?.let{ locations ->
                    for (location in locations) {
                        modActualPosition(location)
                        Log.d(TAG,"Nueva localizacion ${location.toString()}")
                        //CAMBIAR  texto notificacion

                    }
                }
            }
        }

    }

    private fun getMainActivityPendingIntent()  = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also{
            it.action = ACTION_SHOW_HOME_FRAGMEWNT
        },
        FLAG_UPDATE_CURRENT
    )

    private fun crearNotificationChannel(notificationmanager :NotificationManager){
        val channel = NotificationChannel(
            NOTIFICATION_CHANEL_ID,
            NOTIFICATION_CHANEL_NAME,
            IMPORTANCE_LOW
        )
        notificationmanager.createNotificationChannel(channel)
    }

}