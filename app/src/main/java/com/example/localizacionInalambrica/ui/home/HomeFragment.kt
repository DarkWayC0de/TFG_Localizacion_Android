package com.example.localizacionInalambrica.ui.home

import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.Servicios.ServicioRastreo
import com.example.localizacionInalambrica.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.localizacionInalambrica.other.Constants.DEFAULT_ZOOM
import com.example.localizacionInalambrica.toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter

class HomeFragment : Fragment() {
    private val beaconParser = BeaconParser()
        .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")

    val contexts= this.context;
    private var beaconTransmitter : BeaconTransmitter? = null
    private lateinit var homeViewModel: HomeViewModel
   /* private val bluetoothPermission = PermissionSaferFragment(this,
        Manifest.permission.BLUETOOTH,
        onDenied = { toast("Permission Denied") }
    ) { toast("Should show Rationale") }
    private val bluetoothAdminPermission = PermissionSaferFragment(this,
        Manifest.permission.BLUETOOTH_ADMIN,
        onDenied = { toast("Permission Denied") }
    ) { toast("Should show Rationale") }
*/
    private fun toast(s: String)
    {
        context?.toast(s)
    }

    /**tracking map **/
    private var map: GoogleMap? = null
    private var root : View? = null
    private var mapView : MapView? = null
    private var isTracking = false
    private var location : Location ? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root!!.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        mapView = root!!.findViewById(R.id.mapView_home)
        /**tracking map **/
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync{
            map = it
            actualLocation()
        }

        val buton:Button = root!!.findViewById(R.id.buttonpermisions)
        buton.setOnClickListener {


        }
            /*
            bluetoothPermission.runWithPermission {
                bluetoothAdminPermission.runWithPermission {
                    fineLocationPermission.runWithPermission {
                        coarseLocationPermission.runWithPermission {
                            toast("FUNCIONA")
                            /*
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location : Location? ->
                                    // Got last known location. In some rare situations this can be null.
                                    toast(location.toString())
                                    if(location!=null){
                                    beaconTransmitter = BeaconTransmitter(context, beaconParser)
                                        val string :String =      ((location.latitude/0.0001).toInt()).toString() +
                                                            "," + ((location.longitude/0.0001).toInt()).toString() +
                                                            "," + location.altitude.toInt().toString() +
                                                            "," + ((location.bearing/0.1).toInt()).toString() +
                                                            "," + ((location.speed/0.1).toInt()).toString()
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
                            */
                        }
                    }
                }
            }
        } */
        sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        suscribeToObservers()
        return root
    }
    private fun suscribeToObservers(){
        ServicioRastreo.isTracking.observe(viewLifecycleOwner, Observer {
            isTracking = it
        })
        ServicioRastreo.actualPosition.observe(viewLifecycleOwner, Observer {
            location = it
            actualLocation()
        })
    }
    private var lastCircle : Circle? =null
    private fun actualLocation(){
        if(location != null){
            map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location!!.latitude,location!!.longitude),DEFAULT_ZOOM)
            )
            if (lastCircle != null) {
                lastCircle!!.remove()
            }
            lastCircle = map?.addCircle(
                CircleOptions()
                    .center(LatLng(location!!.latitude,location!!.longitude))
                    .radius(2.0)
                    .strokeWidth(2f)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.BLUE)
            )


        }
    }
    fun sendCommandToService(action: String) =
        Intent(requireContext(), ServicioRastreo::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

}
