package dayfive.com.lrm.kotlinday5.map_details

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dayfive.com.lrm.kotlinday5.R
import dayfive.com.lrm.kotlinday5.gps_location_tracker.GpsTracker
import android.content.Intent
import android.widget.Toast


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private  lateinit var buttonFindLocation: Button
    private lateinit var  mapFragment: SupportMapFragment
    private lateinit var gpsTracker: GpsTracker
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private lateinit var location: Location
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1
    private val MIN_TIME_BET_UPDATES = (1000 * 60 * 1).toLong()
    private lateinit var locationManager: LocationManager
    lateinit var handler: Handler;

    val ACCESS_FINE_LOCATION = 1
    val ACCESS_COARSE_LOCATION= 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        buttonFindLocation=findViewById(R.id.btn_find_location) as Button
        handler= Handler()
        buttonFindLocation.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
               loadMapData()
            }
        })
    }



    fun loadMapData()
    {
        mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        gpsTracker = GpsTracker(this)

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap1: GoogleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
        } else {

           gpsTracker(googleMap1)
           //googleMap= googleMap1
        }

    }

    fun gpsTracker(googleMap1: GoogleMap)
    {
        if (gpsTracker.canGetLoaction()) {

            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            gpsTracker.onLocationChanged(location)
            latitude = gpsTracker.getLatitude()
            longitude = gpsTracker.getLongitude()

            mMap = googleMap1

            // Add a marker in Sydney and move the camera

            Handler().postDelayed({
                val pune = LatLng(latitude, longitude)
                mMap.addMarker(MarkerOptions().position(pune).title("Marker in Pune"))
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pune))
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(8.0f));
                //mMap.setMaxZoomPreference(14.0f);
                mMap.maxZoomLevel
            }, 1500)


            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setTitle("Location")
            builder.setMessage("This is your current location: Latitude: $latitude Longitude: $longitude")
            builder.setPositiveButton("OK") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.show()

        } else {
            gpsTracker.openSettings()//Open the settings alert to enable the GPS sevice
        }
    }


    override fun onRequestPermissionsResult(permsRequestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (permsRequestCode) {

            0 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE_LOCATION)        //gpsTracker()
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //Show an explanation to the user *asynchronously*
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)        //gpsTracker()
                    }else{
                        val builder = AlertDialog.Builder(this)
                        builder.setCancelable(true)
                        builder.setTitle("Alert")
                        builder.setMessage("Location permission is required")
                        builder.setPositiveButton("OK") { dialogInterface, i -> dialogInterface.dismiss() }
                        builder.show()
                    }
                }


            }

            1 -> {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  loadMapData()
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MapsActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        //Show an explanation to the user *asynchronously*
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), ACCESS_COARSE_LOCATION)        //gpsTracker()
                    }else{
                        val builder = AlertDialog.Builder(this)
                        builder.setCancelable(true)
                        builder.setTitle("Alert")
                        builder.setMessage("Location permission is required")
                        builder.setPositiveButton("OK") { dialogInterface, i -> dialogInterface.dismiss() }
                        builder.show()
                    }
                }
            }
        }

    }



}
