package com.example.localizacionInalambrica

import android.Manifest.permission.*
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.widget.Filter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseUser

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    // Permisos
    // Permisos BluetoothBLE
/*
    private val bluetoothPermission = PermissionSafer(this,
        BLUETOOTH,
        onDenied = { toast("Permission Denied") },
        onShowRationale = { toast("Should show Rationale") })
    private val bluetoothAdminPermission = PermissionSafer(this,
        BLUETOOTH_ADMIN,
        onDenied = { toast("Permission Denied") },
        onShowRationale = { toast("Should show Rationale") }) */
    private val fineLocationPermission = PermissionSaferMain(this,
        ACCESS_FINE_LOCATION,
        onDenied = { toast("Permission Denied") }
    ) { toast("Should show Rationale") }
    private val coarseLocationPermission = PermissionSaferMain(this,
        ACCESS_COARSE_LOCATION,
        onDenied = { toast("Permission Denied") }
    ) { toast("Should show Rationale") }
    private val backgroundLocationPermission = PermissionSaferMain(this,
        ACCESS_BACKGROUND_LOCATION,
        onDenied = { toast("Permission Denied") }
    ) { toast("Should show Rationale") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(
                view,
                "Replace with your own action",
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val navheader = navView.getHeaderView(0)
        val currentUser = ParseUser.getCurrentUser()
        val username : TextView = navheader.findViewById(R.id.usernamenav)
        val user =currentUser.username
        username.text =user
        val correo : TextView = navheader.findViewById(R.id.correonav)
        correo.text = currentUser.email
        startServiceGPS()
    }

    private  fun startServiceGPS(){
        fineLocationPermission.runWithPermission {
          coarseLocationPermission.runWithPermission {
              backgroundLocationPermission.runWithPermission {
                  val receiver = LocationBroadcstReciver();
                  val filter : IntentFilter = IntentFilter("ACT_LOC")
                  registerReceiver(receiver,filter)
                  val intent = Intent(this@MainActivity, serviceGPS::class.java)
                  startService(intent)
              }
          }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}