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

    }
    external fun adios():Int
    external fun adios2(a : Int): Int
    external fun adios3():Int
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