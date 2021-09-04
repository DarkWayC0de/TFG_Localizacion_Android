package com.example.localizacionInalambrica

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.parse.Parse
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class App : Application() {
    private val TAG = "APP"

    @Inject
    lateinit var sharedPref: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Parse.enableLocalDatastore(this)
        Log.d(TAG, "Se llama onCREATE")

    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.d(TAG, "Se llama onLowMemory")
    }

    override fun onTerminate() {
        super.onTerminate()
        Log.d(TAG, "Se llama Terminate")
    }
}