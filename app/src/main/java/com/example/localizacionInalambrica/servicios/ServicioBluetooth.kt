package com.example.localizacionInalambrica.servicios


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.localizacionInalambrica.notification.Notification
import com.example.localizacionInalambrica.other.Constants
import com.example.localizacionInalambrica.other.Constants.BEACON_LAYOUT
import com.example.localizacionInalambrica.other.Constants.NOTIFICATION_ID
import com.example.localizacionInalambrica.other.Utility.decodeHex
import com.example.localizacionInalambrica.permisos.Permissions
import dagger.hilt.android.AndroidEntryPoint
import org.altbeacon.beacon.*
import javax.inject.Inject

@AndroidEntryPoint
class ServicioBluetooth : LifecycleService() {
    private val TAG = "ServicioBluetooth"
    private var isFirstRun = true
    private var serverKilled = false
    var modeRastreador = false

    @Inject
    lateinit var beaconManager: BeaconManager

    companion object {

        val servicerStarted = MutableLiveData<Boolean>()
    }

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var actualNotificationBuilder: NotificationCompat.Builder

    override fun onCreate() {
        super.onCreate()
        actualNotificationBuilder = baseNotificationBuilder

        beaconManager.beaconParsers.clear()
        beaconManager.beaconParsers.add(
            BeaconParser().setBeaconLayout(BEACON_LAYOUT)
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE_BLUETOOTH_CLIENTE -> {
                    modeRastreador = false
                    if (isFirstRun) {
                        starForegroundService()
                        isFirstRun = false
                        Log.d(TAG, "Iniciado Cliente")
                    } else {
                        Log.d(TAG, "Recuperado Cliente")
                    }

                }
                Constants.ACTION_START_SERVICE_BLUETOOTH_RASTREADOR -> {
                    modeRastreador = true
                    if (isFirstRun) {
                        starForegroundService()
                        isFirstRun = false
                        Log.d(TAG, "Iniciado Rastreador")
                    } else {
                        Log.d(TAG, "Recuperado Rastreador")
                    }

                }
                Constants.ACTION_RESUME_SERVICE_BLUETOOTH -> {
                    Log.d(TAG, "Recuperado")
                    resumeService()
                }
                Constants.ACTION_PAUSE_SERVICE_BLUETOOTH -> {
                    Log.d(TAG, " Pausado")
                    pauseService()
                }
                Constants.ACTION_STOP_SERVICE_BLUETOOTH -> {
                    Log.d(TAG, " Terminado")
                    killService()
                }

                else -> {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun resumeService() {
        TODO("Not yet implemented")
    }

    private fun killService() {
        TODO("Not yet implemented")
    }

    private fun pauseService() {
        TODO("Not yet implemented")
    }

    private fun starForegroundService() {
        servicerStarted.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        Notification.crearNotificationChannel(notificationManager)

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        if (modeRastreador) {
            startBluetoothRecive()
        } else {
            ServicioRastreo.actualPosition.observeForever(Observer {
                if (it != null) {
                    sendBluetooth(it)
                }
            })
        }
    }

    private fun startBluetoothRecive() {
        if (Permissions.hastLocationAndBluetoothPermissions(this)) {
            beaconManager.setEnableScheduledScanJobs(false)
            beaconManager.backgroundBetweenScanPeriod = 0;
            beaconManager.backgroundScanPeriod = 1100;
            val region = Region("region", null, Identifier.parse("65535"), null)
            beaconManager.startRangingBeacons(region)
            val regionViewModel =
                BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)
            regionViewModel.rangedBeacons.observeForever(centralRangingObserver)
        }
    }

    private val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
            val a = beacon.id1.toString()
            val b = beacon.id2.toString()
            val c = beacon.id3.toString()
            val d = a + b + c

        }
    }

    private fun sendBluetooth(locate: Location) {
        if (Permissions.hastLocationAndBluetoothPermissions(this)) {
            val h = "0f3e30f3e32132130f3e30f3e3213213"
            val b: ByteArray = h.decodeHex()
            /*var array = ByteArray(16)
            val array1 = (locate.latitude/0.0001).toInt().toString().toByteArray(Charset.defaultCharset())
            var a = 0
            array1.forEach {
                array[a++] = it
            }
            */
            val beacon = Beacon.Builder()
                .setId1(h)
                .setId2("65535")            // 0 - 65535
                .setId3("65535")            // 0 - 65535
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(arrayOf(0L).asList())
                .build()


            val beaconTransmiter = BeaconTransmitter(
                applicationContext,
                beaconManager.beaconParsers.first()
            )
            beaconTransmiter.startAdvertising(beacon)

        }


    }

    /***
     *
     *          Encode
     *
     *  val string :String =
     *  ((location.latitude/0.0001).toInt()).toString() +            90 - 0 - 90 esperado 900000       1 bit signo 20 numero entero 0 - 1048576
    "," + ((location.longitude/0.0001).toInt()).toString() +        180 - 0 - 180 esperado 1800000     1 bit signo 21 numero entero 0 - 2097152
    "," + location.altitude.toInt().toString() +                    9000                               14 entero  0 - 16384
    "," + ((location.bearing/0.1).toInt()).toString() +             0.0 - 360.0 esperado 3600          12 entero  0 - 4096
    "," + ((location.speed/0.1).toInt()).toString()                 0.0 - 12.0           120            7
     *                                                                                                 21 + 22 + 14 +12 +7 = 76      76/ 8 = 9.5 -> 10 Byte
     *                                                                                                 128 - 76 = 52
     */

}