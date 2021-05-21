package com.example.localizacion_inalambrica

import android.app.Application
import com.parse.Parse
import java.io.IOException


val APLICATIONID ="e0ef0e30-b8e6-11eb-8529-0242ac130003"

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        /*Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("e0ef0e30-b8e6-11eb-8529-0242ac130003") // if defined
                .server("https://app.ed0cyawkrad.duckdns.org/parse/")
                .build()
        )*/

    }


}