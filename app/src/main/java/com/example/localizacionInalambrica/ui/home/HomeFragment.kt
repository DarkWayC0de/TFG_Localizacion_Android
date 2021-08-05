package com.example.localizacionInalambrica.ui.home

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.localizacionInalambrica.APLICATIONID
import com.example.localizacionInalambrica.PermissionSafer
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*

class HomeFragment : Fragment() {
    private val beaconParser = BeaconParser()
        .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")

    val contexts= this.context;
    private var beaconTransmitter : BeaconTransmitter? = null
    private lateinit var homeViewModel: HomeViewModel
    private val bluetoothPermission = PermissionSafer(this,
        Manifest.permission.BLUETOOTH,
        onDenied = { toast("Permission Denied") },
        onShowRationale = { toast("Should show Rationale") })
    private val bluetoothAdminPermission = PermissionSafer(this,
        Manifest.permission.BLUETOOTH_ADMIN,
        onDenied = { toast("Permission Denied") },
        onShowRationale = { toast("Should show Rationale") })
    private val fineLocationPermission = PermissionSafer(this,
        Manifest.permission.ACCESS_FINE_LOCATION,
        onDenied = { toast("Permission Denied") },
        onShowRationale = { toast("Should show Rationale") })
    private val coarseLocationPermission = PermissionSafer(this,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        onDenied = { toast("Permission Denied") },
        onShowRationale = { toast("Should show Rationale") })

    private fun toast(s: String)
    {
        context?.toast(s)
    }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        fineLocationPermission.runWithPermission {
            coarseLocationPermission.runWithPermission {
                toast("FUNCIONA")
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            }
        }
        val buton:Button =        root.findViewById(R.id.buttonpermisions)
        buton.setOnClickListener {
            bluetoothPermission.runWithPermission {
                bluetoothAdminPermission.runWithPermission {
                    fineLocationPermission.runWithPermission {
                        coarseLocationPermission.runWithPermission {
                            toast("FUNCIONA")
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                    // Got last known location. In some rare situations this can be null.
                                    toast(location.toString())
                                    if(location!=null){
                                    beaconTransmitter = BeaconTransmitter(context, beaconParser)
                                        val string :String =      ((location.latitude/0.0001).toInt()).toString() +
                                                            "," + ((location.longitude/0.0001).toInt()).toString() +
                                                            "," + location.altitude.toInt().toString() +
                                                            "," + ((location.bearing/0.001).toInt()).toString() +
                                                            "," + ((location.speed/0.001).toInt()).toString()
                                        val a  = string.encodeToByteArray()
                                        val b = a.size

                                    var array = arrayOf(2L,4L)
                                    val beacon = Beacon.Builder()
                                        .setId1(APLICATIONID)
                                        .setId2("65535")            // 0 - 65535
                                        .setId3("65535")            // 0 - 65535
                                        .setManufacturer(0x0118)
                                        .setTxPower(-59)
                                        .setDataFields(array.toMutableList())
                                        .build()
                                    beaconTransmitter!!.startAdvertising(beacon)
                                    }
                                }
                        }
                    }
                }
            }
        }
        return root
    }
}