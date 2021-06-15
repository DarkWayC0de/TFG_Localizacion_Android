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
import com.example.localizacionInalambrica.PermissionSafer
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class HomeFragment : Fragment() {

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
        val a:Button =        root.findViewById(R.id.buttonpermisions)
        a.setOnClickListener {
            bluetoothPermission.runWithPermission {
                bluetoothAdminPermission.runWithPermission {
                    fineLocationPermission.runWithPermission {
                        coarseLocationPermission.runWithPermission {
                            toast("FUNCIONA")
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                    // Got last known location. In some rare situations this can be null.
                                    toast(location.toString())

                                }
                        }
                    }
                }
            }
        }
        return root
    }
}