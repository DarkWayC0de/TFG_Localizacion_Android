package com.example.localizacionInalambrica.servicios


import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.localizacionInalambrica.permisos.Permissions
import dagger.hilt.android.AndroidEntryPoint
import org.altbeacon.beacon.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ServicioBluetooth : LifecycleService() {
    private val TAG = "ServicioBluetooth"
    private var isFirstRun = true
    private var serverKilled = false
    var modeRastreador = false

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var region: Region

    private lateinit var beaconTransmiter: BeaconTransmitter

    @Inject
    lateinit var beaconManager: BeaconManager

    companion object {

        val servicerStarted = MutableLiveData<Boolean>()

        val clientsLocations = MutableLiveData<Pair<Pair<Collection<Beacon>, Location?>, Date>>()

        // Used to load the 'cripto-lib' library on application startup.
        init {
            System.loadLibrary("cripto-lib")
        }
    }

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var actualNotificationBuilder: NotificationCompat.Builder

    private var idUser: String? = null
    private var macKey: String? = null
    private var cifradoKey: String? = null
    private var location: Location? = null

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
                Constants.ACTION_STOP_SERVICE_NOTIFICATION -> {
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
        servicerStarted.postValue(true)
        if (modeRastreador) {
            beaconManager.startRangingBeacons(region)
        } else {
            beaconTransmiter.startAdvertising()
        }
    }

    private fun killService() {
        serverKilled = true
        isFirstRun = true
        pauseService()
        modeRastreador = false
        stopForeground(true)
        stopSelf()
    }

    private fun pauseService() {
        servicerStarted.postValue(false)
        beaconManager.disableForegroundServiceScanning()
        if (modeRastreador) {
            beaconManager.stopRangingBeacons(region)
        } else {
            beaconTransmiter.stopAdvertising()
        }
    }

    private fun starForegroundService() {
        servicerStarted.postValue(true)
        serverKilled = false

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        Notification.crearNotificationChannel(notificationManager)
        beaconManager.enableForegroundServiceScanning(
            baseNotificationBuilder.build(),
            NOTIFICATION_ID
        )
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
        ServicioRastreo.actualPosition.observeForever(Observer {
            if (it != null) {
                location = it
            }
        })

        if (modeRastreador) {
            startBluetoothRecibe()
        } else {
            idUser = sharedPref.getString(Constants.PREFERENSES_USERID, null)
            if (idUser == null) {
                Log.d(TAG, "Error preferencia userID")
            }
            macKey = sharedPref.getString(Constants.PREFERENSES_MACKEY32, null)
            if (macKey == null) {
                Log.d(TAG, "Error preferencia mackey32")
            }
            cifradoKey = sharedPref.getString(Constants.PREFERENSES_CIFRADOKEY64, null)
            if (cifradoKey == null) {
                Log.d(TAG, "Error preferencia CIFRADOKEY64")
            }
            ServicioRastreo.actualPosition.observeForever(Observer {

                if (it != null) {
                    sendBluetooth(it)
                }
            })
        }
    }

    private fun startBluetoothRecibe() {
        if (Permissions.hastLocationAndBluetoothPermissions(this)) {

            beaconManager.setEnableScheduledScanJobs(false)
            beaconManager.backgroundBetweenScanPeriod = 0
            beaconManager.backgroundScanPeriod = 1100
            beaconManager.foregroundBetweenScanPeriod = 0
            beaconManager.foregroundScanPeriod = 1100
            beaconManager.startRangingBeacons(region)
            val regionViewModel =
                BeaconManager.getInstanceForApplication(this).getRegionViewModel(region)
            regionViewModel.rangedBeacons.observeForever(centralRangingObserver)
        }
    }

    private val centralRangingObserver = Observer<Collection<Beacon>> { beacons ->
        if (beacons.isNotEmpty()) {
            clientsLocations.postValue(Pair(Pair(beacons, location), Date()))
            Log.d(TAG, "Se detectan beacons")
        }
    }

    private fun sendBluetooth(location: Location) {
        if (Permissions.hastLocationAndBluetoothPermissions(this)) {
            var iduser = ""
            if (idUser == null) {
                Log.d(TAG, "Error idUser es null")
                killService()
            } else {
                iduser = idUser as String
            }
            var mackey = ""
            if (macKey == null) {
                Log.d(TAG, "Error macKey es null")
                killService()
            } else {
                mackey = macKey as String
            }
            var cifradokey = ""
            if (cifradoKey == null) {
                Log.d(TAG, "Error cifradoKey es null")
                killService()
            } else {
                cifradokey = cifradoKey as String
            }
            val msg = location_to_encode_and_encrypter(
                (location.latitude / 0.0001).toInt(),
                (location.longitude / 0.0001).toInt(),
                location.altitude.toInt(),
                (location.bearing / 0.1).toInt(),
                (location.speed / 0.1).toInt(),
                mackey,
                cifradokey
            )

            val beacon = Beacon.Builder()
                .setId1(msg)
                .setId2("65535")            // 0 - 65535
                .setId3(iduser)            // 0 - 65535
                .setManufacturer(0x0118)
                .setTxPower(-59)
                .setDataFields(arrayOf(0L).asList())
                .build()


            beaconTransmiter = BeaconTransmitter(
                applicationContext,
                beaconManager.beaconParsers.first()
            )
            beaconTransmiter.startAdvertising(beacon)


        }


    }

    //07vm6qtPC5
    external fun location_to_encode_and_encrypter(
        longitud: Int,
        latitud: Int,
        altitud: Int,
        bearing: Int,
        speed: Int,
        mackey: String,
        cifradokey: String
    ): String


    /***
     *
     *          Encode
     *
     *  val string :String =
     *  ((location.latitude/0.0001).toInt()).toString() +            90 - 0 - 90 esperado 900000       1 bit signo 20 numero entero 0 - 1048576
    "," + ((location.longitude/0.0001).toInt()).toString() +        180 - 0 - 180 esperado 1800000     1 bit signo 21 numero entero 0 - 2097152
    "," + location.altitude.toInt().toString() +                                           9000        14 entero  0 - 16384
    "," + ((location.bearing/0.1).toInt()).toString() +             0.0 - 360.0 esperado 3600          12 entero  0 - 4096
    "," + ((location.speed/0.1).toInt()).toString()                 0.0 - 12.0           120            7
     *                                                                                                 21 + 22 + 14 +12 +7 = 76      76/ 8 = 9.5 -> 10 Byte
     *                                                                                                 128 - 76 = 52
     */

}