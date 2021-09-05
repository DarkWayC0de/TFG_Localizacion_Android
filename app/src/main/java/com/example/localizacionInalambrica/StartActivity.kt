package com.example.localizacionInalambrica

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.localizacionInalambrica.other.Constants.APLICATIONID
import com.example.localizacionInalambrica.other.Constants.PREFERENSES_SERVERADDR
import com.parse.Parse
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_start) as NavHostFragment
        val navController = navHostFragment.navController

        pruebaAnteriorLogueo()

    }

    @Inject
    lateinit var sharedPref: SharedPreferences
    private fun pruebaAnteriorLogueo() {
        val query = sharedPref.getString(PREFERENSES_SERVERADDR, null)
        if (query != null) {
            Parse.initialize(
                this.let {
                    Parse.Configuration.Builder(it)
                        .applicationId(APLICATIONID) // if desired
                        .server(query)
                        .build()
                })
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser != null) {
                iniciarapp()
            }
        }
    }

    private fun iniciarapp(){
        val actividad =  Intent(this,MainActivity::class.java)
        startActivity(actividad)
        finish()
    }

}