package dayfive.com.lrm.kotlinday5.gps_location_tracker

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast

/**
 * Created by manasi on 13/2/18.
 */
class GpsTracker(context: Context) : Service(), LocationListener {

    private lateinit var context1: Context
    private var isGpsEnabled: Boolean? = false
    private var isNetworkEnabled: Boolean= false
    private var canGetLocation: Boolean? = false
    private var location: Location? = null
    private var latitude: Double = 0.toDouble()
    private var logitude: Double = 0.toDouble()
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1
    private val MIN_TIME_BET_UPDATES = (1000 * 60 * 1).toLong()
    protected var locationManager: LocationManager? = null

   /* fun GpsTracker(context: Context): ??? {
        this.context = context
        getLocation()
    }
*/


    init {
        context1= context
        getLocation()
    }
    /*   @Override
    public void onCreate() {
        super.onCreate();

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BET_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    }
*/
    fun getLocation(): Location? {
        try {

            locationManager = context1.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BET_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
            //To enable the network and the Gps Service
            isGpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if ((!isNetworkEnabled) && (!isNetworkEnabled)) {
                Toast.makeText(context1, "NO GPS and the NETWORK access", Toast.LENGTH_SHORT).show()
            } else {
                this.canGetLocation = true
                //check for the network
                if (isNetworkEnabled) {

                    //Check id the locatopn or the gps service is enabled or not
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context1.startActivity(intent)
                    } else {
                        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BET_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d("Network: ", "Network Enabled")
                        if (locationManager != null) {
                            location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                logitude = location!!.longitude
                            }
                        }

                    }// if the gps is enabled

                }
                //Check for the gps location
                if (isGpsEnabled!!) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BET_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d("Gps Service: ", "Gps Enabled")
                        if (locationManager != null) {
                            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                logitude = location!!.longitude
                            }
                        }

                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location
    }


    /**
     * This method is used to stop accessing the GPs Service from our app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context1.startActivity(intent)
                ActivityCompat.requestPermissions(context1 as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            } else {
                locationManager!!.removeUpdates(this@GpsTracker)
            }
        }
    }

    /**
     *
     *
     * This method is used to calculate the latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude
    }

    /**
     *
     *
     * This method is used to calculate the longitude
     */
    fun getLongitude(): Double {
        if (location != null) {
            logitude = location!!.longitude
        }
        return logitude
    }

    /**
     *
     * This Function will check whether the wifi is enabled
     *
     */
    fun canGetLoaction(): Boolean {
        return this.canGetLocation!!
    }


    /**
     * To enable the gps and the location service from the settings of the device
     *
     *
     */
    fun openSettings() {
        val builder = AlertDialog.Builder(context1)
        builder.setTitle("GPS Settings")
        builder.setMessage("Enable the GPS")
        builder.setPositiveButton("SETTINGS") { dialogInterface, i ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context1.startActivity(intent)
            dialogInterface.cancel()
        }
        builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.cancel() }
        builder.setCancelable(false)
        builder.show()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        Log.e("Lat: ", location.latitude.toString())
        Log.e("Lang: ", location.longitude.toString())
        latitude = location.latitude
        logitude = location.longitude

    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

    }


}
