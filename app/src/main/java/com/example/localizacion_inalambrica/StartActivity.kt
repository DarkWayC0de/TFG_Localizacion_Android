package com.example.localizacion_inalambrica

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.parse.Parse
import com.parse.ParseUser

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_start) as NavHostFragment
        val navController = navHostFragment.navController

        pruebaAnterioLogueo()

    }

    private fun pruebaAnterioLogueo(){
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)!!
        val query = sharedPref.getString("serveraddr",null)
        if(query != null){
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
     }
}