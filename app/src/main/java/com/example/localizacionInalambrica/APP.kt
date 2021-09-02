package com.example.localizacionInalambrica

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.parse.Parse
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Parse.enableLocalDatastore(this)
        Log.d("APP", "Se llama onCREATE")
    }

    external fun adios(): Int
    external fun adios2(a: Int): Int
    external fun adios3(): Int

    companion object {
        // Used to load the 'cripto-lib' library on application startup.
        init {
            System.loadLibrary("cripto-lib")
        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d("APP", "Se llama onLowMemory")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d("APP", "Se llama TErminate")
    }
}