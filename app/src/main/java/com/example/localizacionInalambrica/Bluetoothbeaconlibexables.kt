package com.example.localizacionInalambrica


import android.app.Activity
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import org.altbeacon.beacon.*


/**
 *             Monitoring Example Code
 *
 * */

class MonitoringActivity : Activity(), BeaconConsumer {
    private var beaconManager: BeaconManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        beaconManager = BeaconManager.getInstanceForApplication(this)
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager!!.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager!!.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.removeAllMonitorNotifiers()
        beaconManager!!.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(region: Region?) {
                Log.i(TAG, "I just saw an beacon for the first time!")
            }

            override fun didExitRegion(region: Region?) {
                Log.i(TAG, "I no longer see an beacon")
            }

            override fun didDetermineStateForRegion(state: Int, region: Region?) {
                Log.i(
                    TAG,
                    "I have just switched from seeing/not seeing beacons: $state"
                )
            }
        })
        try {
            beaconManager!!.startMonitoringBeaconsInRegion(
                Region(
                    "myMonitoringUniqueId",
                    null,
                    null,
                    null
                )
            )
        } catch (e: RemoteException) {
        }
    }

    companion object {
        protected const val TAG = "MonitoringActivity"
    }
}

class RangingActivity : Activity(), BeaconConsumer {
    private var beaconManager: BeaconManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)
        beaconManager = BeaconManager.getInstanceForApplication(this)
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager!!.beaconParsers.add(
            BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
               // .setBeaconLayout("m:2-3=beac,i:4-5,p:6-6,d:7-28"))


        beaconManager!!.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager!!.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager!!.removeAllRangeNotifiers()
        beaconManager!!.addRangeNotifier { beacons, region ->
            if (beacons.size > 0) {
                Log.i(
                    TAG,
                    "The first beacon I see is about " + beacons.iterator()
                        .next().distance + " meters away."
                )
                val bea= beacons.last()
                val a = bea.dataFields
                val b = a.size
            }
        }
        try {
            beaconManager!!.startRangingBeaconsInRegion(
                Region(
                    "myRangingUniqueId",
                    null,
                    null,
                    null
                )
            )
        } catch (e: RemoteException) {
        }
    }

    companion object {
        const val TAG = "RangingActivity"
    }
}
