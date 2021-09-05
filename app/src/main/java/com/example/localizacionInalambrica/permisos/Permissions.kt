package com.example.localizacionInalambrica.permisos

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import org.altbeacon.beacon.BeaconManager
import pub.devrel.easypermissions.EasyPermissions


object Permissions {
    fun hastLocationAndBluetoothPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN
            )

        } else {
            EasyPermissions.hasPermissions(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN
            )
        }

    fun enablebluetooth(context: Context) {
        verifyBluetooth(context)
    }

    fun enableLocation(context: Context) {
        val manager = context.getSystemService(LOCATION_SERVICE) as LocationManager?
        if (!manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps(context)
        }
    }

    private fun buildAlertMessageNoGps(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes",
            DialogInterface.OnClickListener { _, _ -> context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) })
        builder.setNegativeButton("No",
            DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        builder.show()
    }

    private fun verifyBluetooth(context: Context) {
        try {
            if (!BeaconManager.getInstanceForApplication(context).checkAvailability()) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Bluetooth not enabled")
                builder.setMessage("Please enable bluetooth in settings and restart this application.")
                builder.setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { _, _ ->
                        context.startActivity(
                            Intent(
                                Settings.ACTION_BLUETOOTH_SETTINGS
                            )
                        )
                    })
                builder.setOnDismissListener {
                    //finish();
                    //System.exit(0);
                }
                builder.show()
            }
        } catch (e: RuntimeException) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Bluetooth LE not available")
            builder.setMessage("Sorry, this device does not support Bluetooth LE.")
            builder.setPositiveButton(R.string.ok, null)
            builder.setOnDismissListener {
                //finish();
                //System.exit(0);
            }
            builder.show()
        }
    }

}