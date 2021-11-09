package com.example.localizacionInalambrica

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
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
import com.example.localizacionInalambrica.other.Constants.ACTION_SHOW_HOME_FRAGMEWNT
import com.example.localizacionInalambrica.other.Constants.ACTION_STOP_SERVICE_NOTIFICATION
import com.example.localizacionInalambrica.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.localizacionInalambrica.permisos.Permissions
import com.example.localizacionInalambrica.servicios.ServicioRastreo
import com.google.android.material.navigation.NavigationView
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var navController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions()

        Log.d("MainActivity", "Se llama onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "Se llama onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity", "Se llama onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "Se llama onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "Se llama onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "Se llama onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "Se llama onDestroy")
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
            navController!!.navigate(R.id.nav_rastreador)
        }
    }

    private fun requestPermissions() {
        Permissions.enablebluetooth(this)
        Permissions.enableLocation(this)
        if (Permissions.hastLocationAndBluetoothPermissions(this)) {
            continuaStart()
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.explicacion_permisos_ubicaion),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.explicacion_permisos_ubicaion),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        continuaStart()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    fun continuaStart() {
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val currentUser = ParseUser.getCurrentUser()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        if (currentUser.get("role") == "Rastreador") {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_rastreador, R.id.nav_registro, R.id.nav_slideshow
                ), drawerLayout
            )
        } else {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_rastreador
                ), drawerLayout
            )
            //TODO ocultar boton de registro y slideshow del menu izquierdo
        }
        setupActionBarWithNavController(navController!!, appBarConfiguration)
        navView.setupWithNavController(navController!!)

        val navheader = navView.getHeaderView(0)


        // add user to menu

        val username: TextView = navheader.findViewById(R.id.usernamenav)
        val user = currentUser.username
        username.text = user
        val correo: TextView = navheader.findViewById(R.id.correonav)
        correo.text = currentUser.email

        val boton: Button = navheader.findViewById(R.id.cerrar_sesion)
        boton.setOnClickListener {

            if (ServicioRastreo.isTracking.value == true) {
                sendCommandToService(ACTION_STOP_SERVICE_NOTIFICATION, ServicioRastreo::class.java)
            }
            ParseUser.logOut()
            val actividad = Intent(this, StartActivity::class.java)
            startActivity(actividad)

            finish()

        }


        navigateToHomeFragmentIfNeeded(intent)

    }

    fun sendCommandToService(action: String, cls: Class<*>) =
        Intent(this, cls).also {
            it.action = action
            this.startService(it)
        }

}