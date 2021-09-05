package com.example.localizacionInalambrica.ui.home

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.other.Constants
import com.example.localizacionInalambrica.other.Constants.ACTION_START_OR_RESUME_SERVICE_PARSE
import com.example.localizacionInalambrica.other.Constants.ACTION_START_OR_RESUME_SERVICE_RASTREO
import com.example.localizacionInalambrica.other.Constants.ACTION_START_SERVICE_BLUETOOTH_CLIENTE
import com.example.localizacionInalambrica.other.Constants.ACTION_START_SERVICE_BLUETOOTH_RASTREADOR
import com.example.localizacionInalambrica.other.Constants.ACTION_STOP_SERVICE_NOTIFICATION
import com.example.localizacionInalambrica.other.Constants.DEFAULT_ZOOM
import com.example.localizacionInalambrica.servicios.ServicioBluetooth
import com.example.localizacionInalambrica.servicios.ServicioParse
import com.example.localizacionInalambrica.servicios.ServicioRastreo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.parse.ParseUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    val contexts = this.context

    @Inject
    lateinit var sharedPref: SharedPreferences

    val TAG = "HomeFragment"

    /**tracking map **/
    private var map: GoogleMap? = null
    private var root: View? = null
    private var mapView: MapView? = null
    private var isTracking = false
    private var location: Location? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        mapView = root!!.findViewById(R.id.mapView_home)
        /**tracking map **/
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync {
            map = it
            actualLocation()
        }
        val userRolePref = sharedPref.getString(Constants.PREFERENSES_USERROLE, null)
        if (userRolePref == null) {
            ParseUser.logOut()
            requireActivity().finish()
        }
        val buton: Button = root!!.findViewById(R.id.buttonpermisions)
        buton.setOnClickListener {
            if (!isTracking) {
                sendCommandToService(
                    ACTION_START_OR_RESUME_SERVICE_RASTREO,
                    ServicioRastreo::class.java
                )

                if (userRolePref == "Rastreador") {
                    sendCommandToService(
                        ACTION_START_SERVICE_BLUETOOTH_RASTREADOR,
                        ServicioBluetooth::class.java
                    )
                    sendCommandToService(
                        ACTION_START_OR_RESUME_SERVICE_PARSE,
                        ServicioParse::class.java
                    )
                } else {
                    if (userRolePref == "Usuario") {
                        sendCommandToService(
                            ACTION_START_SERVICE_BLUETOOTH_CLIENTE,
                            ServicioBluetooth::class.java
                        )
                    } else {
                        ParseUser.logOut()
                        requireActivity().finish()
                    }
                }
                buton.text = getString(R.string.FinalizarRastreo)
            } else {
                sendCommandToService(ACTION_STOP_SERVICE_NOTIFICATION, ServicioRastreo::class.java)
                buton.text = getString(R.string.ComenzarRastreo)
            }

        }
        buton.text = getString(R.string.ComenzarRastreo)

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
                    .center(LatLng(location!!.latitude, location!!.longitude))
                    .radius(2.0)
                    .strokeWidth(2f)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.BLUE)
            )


        }
    }

    fun sendCommandToService(action: String, cls: Class<*>) =
        Intent(requireContext(), cls).also {
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
