package com.example.localizacionInalambrica

import android.Manifest
import android.Manifest.permission.*
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.localizacionInalambrica.Servicios.ServicioRastreo
import com.example.localizacionInalambrica.other.Constants
import com.example.localizacionInalambrica.other.Constants.ACTION_SHOW_HOME_FRAGMEWNT
import com.example.localizacionInalambrica.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.localizacionInalambrica.permisos.permissions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.parse.ParseUser
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

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
    private var navController: NavController? = null
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
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)
        val navheader = navView.getHeaderView(0)

        // add user to menu
        val currentUser = ParseUser.getCurrentUser()
        val username: TextView = navheader.findViewById(R.id.usernamenav)
        val user = currentUser.username
        username.text = user
        val correo: TextView = navheader.findViewById(R.id.correonav)
        correo.text = currentUser.email

        requestPermissions()
        navigateToHomeFragmentIfNeeded(intent)



    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToHomeFragmentIfNeeded(intent)
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

    private fun navigateToHomeFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == ACTION_SHOW_HOME_FRAGMEWNT) {
            navController!!.navigate(R.id.nav_home)
        }
    }

    private fun requestPermissions() {
        if (permissions.hastLocationPermissions(this)) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.explicacion_permisos_ubicaion),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.explicacion_permisos_ubicaion),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

}