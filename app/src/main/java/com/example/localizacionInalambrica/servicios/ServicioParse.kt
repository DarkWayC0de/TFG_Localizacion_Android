package com.example.localizacionInalambrica.servicios

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.localizacionInalambrica.notification.Notification
import com.example.localizacionInalambrica.other.Constants
import com.example.localizacionInalambrica.other.Constants.ACTION_PAUSE_SERVICE_PARSE
import com.example.localizacionInalambrica.other.Constants.ACTION_START_OR_RESUME_SERVICE_PARSE
import com.example.localizacionInalambrica.other.Constants.ACTION_STOP_SERVICE_PARSE
import com.parse.ParseObject
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import org.altbeacon.beacon.Beacon
import javax.inject.Inject

@AndroidEntryPoint
class ServicioParse : LifecycleService() {
    private val TAG = "ServicioParse"
    private var isFirstRun = true
    private var serverKilled = false

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var actualNotificationBuilder: NotificationCompat.Builder

    private var beaconConsumer: Collection<Beacon> = listOf<Beacon>()
    private var beaconConsumer1: Collection<Beacon> = listOf<Beacon>()
    private var beaconConsumer2: Collection<Beacon> = listOf<Beacon>()
    private var beaconConsumer3: Collection<Beacon> = listOf<Beacon>()


    override fun onCreate() {
        super.onCreate()
        actualNotificationBuilder = baseNotificationBuilder


    }

    companion object {
        val servicerStarted = MutableLiveData<Boolean>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE_PARSE -> {
                    if (isFirstRun) {
                        starForegroundService()
                        isFirstRun = false
                        Log.d(TAG, "Iniciado")
                    } else {
                        Log.d(TAG, "Recuperado")
                    }

                }
                ACTION_STOP_SERVICE_PARSE -> {
                    Log.d(TAG, " Terminado")
                    killService()
                }
                ACTION_PAUSE_SERVICE_PARSE -> {
                    Log.d(TAG, " Pausado")
                    pauseService()
                }
                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun starForegroundService() {
        servicerStarted.postValue(true)
        serverKilled = false
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        Notification.crearNotificationChannel(notificationManager)

        startForeground(Constants.NOTIFICATION_ID, baseNotificationBuilder.build())
        ServicioBluetooth.clientsLocations.observeForever { it ->
            if (it != null) {
                Log.d(TAG, "Envia mensaje")
                val date = it.second
                val paircolectionandlocation = it.first
                val location = paircolectionandlocation.second
                val beacons = paircolectionandlocation.first
                beacons.forEach { iit ->
                    if (!beaconConsumer.contains(iit)) {
                        if (!beaconConsumer1.contains(iit)) {
                            if (!beaconConsumer2.contains(iit)) {
                                if (!beaconConsumer3.contains(iit)) {
                                    val nuevosdatos: ParseObject = ParseObject("Rastreo")
                                    if (location != null) {
                                        val ubicacionRastreador = ParseObject("Ubicacion")
                                        ubicacionRastreador.put("Longitud", location.longitude)
                                        ubicacionRastreador.put("Latitude", location.latitude)
                                        ubicacionRastreador.put("Altitude", location.altitude)
                                        ubicacionRastreador.put("Accuracy", location.accuracy)
                                        ubicacionRastreador.put("Bearing", location.bearing)
                                        ubicacionRastreador.put("Speed", location.speed)
                                        ubicacionRastreador.put(
                                            "ElapsedRealtime",
                                            location.elapsedRealtimeNanos
                                        )
                                        ubicacionRastreador.put("Time", location.time)
                                        ubicacionRastreador.put("Provider", location.provider)
                                        //ubicacionRastreador.saveEventually()
                                        nuevosdatos.put("UbicacionRastreador", ubicacionRastreador)
                                    }
                                    nuevosdatos.put("nMensaje", iit.dataFields.get(0))
                                    nuevosdatos.put("MensajeUsuario", iit.id1.toString())
                                    nuevosdatos.put("Rastreador", ParseUser.getCurrentUser())
                                    nuevosdatos.put("idUsuario", iit.id3.toString())
                                    nuevosdatos.put("Fecha", date)
                                    nuevosdatos.put("DistanciaBeacon", iit.distance)
                                    nuevosdatos.saveEventually()
                                }
                            }
                        }
                    }
                }
                beaconConsumer3 = beaconConsumer2
                beaconConsumer2 = beaconConsumer1
                beaconConsumer1 = beaconConsumer
                beaconConsumer = beacons
            }
        }
    }

    private fun pauseService() {
        servicerStarted.postValue(false)

    }

    private fun killService() {
        serverKilled = true
        isFirstRun = true
        pauseService()
        stopForeground(true)
        stopSelf()
    }

}