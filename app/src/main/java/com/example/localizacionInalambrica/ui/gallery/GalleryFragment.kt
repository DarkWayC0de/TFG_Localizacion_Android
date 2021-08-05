package com.example.localizacionInalambrica.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.localizacionInalambrica.R
import com.example.localizacionInalambrica.RangingActivity
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private var beaconManager: BeaconManager? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
                ViewModelProvider(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val buton: Button =        root.findViewById(R.id.button2)
        buton.setOnClickListener {
            beaconManager = context?.let { BeaconManager.getInstanceForApplication(it) }
            beaconManager!!.beaconParsers.add(
                BeaconParser()
                    .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))

            beaconManager!!.removeAllRangeNotifiers()
            beaconManager!!.addRangeNotifier { beacons, region ->
                if (beacons.size > 0) {
                    Log.i(
                        RangingActivity.TAG,
                        "The first beacon I see is about " + beacons.iterator()
                            .next().distance + " meters away."
                    )
                    val bea = beacons.last()
                    val a = bea.dataFields
                    val b = a.size
                }
            }
        }
        return root

    }

    val rangingObserver = Observer<Collection<Beacon>> { beacons ->
        Log.d(TAG, "Ranged: ${beacons.count()} beacons")
        for (beacon: Beacon in beacons) {
            Log.d(TAG, "$beacon about ${beacon.distance} meters away")
        }
    }
    companion object {
        protected const val TAG = "RangingActivity"
    }


}