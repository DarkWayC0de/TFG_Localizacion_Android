package com.example.localizacionInalambrica

import android.app.Application
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*


val APLICATIONID ="e0ef0e30-b8e6-11eb-8529-0242ac130003"

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val beacon = Beacon.Builder()
            .setId1("2f234454-cf6d-4a0f-adf2-f4911ba9ffa6")
            .setId2("1")
            .setId3("2")
            .setManufacturer(0x0118)
            .setTxPower(-59)
            .setDataFields(Arrays.asList(0L))
            .build()
        val beaconParser = BeaconParser()
            .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")
        val beaconTransmitter = BeaconTransmitter(applicationContext, beaconParser)
        beaconTransmitter.startAdvertising(beacon)


    }
    external fun adios():Int
    companion object {
        // Used to load the 'cripto-lib' library on application startup.
        init {
            System.loadLibrary("cripto-lib")
        }
    }
   /* external fun hola(): Int
    external fun mio(): String
    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }*/
}