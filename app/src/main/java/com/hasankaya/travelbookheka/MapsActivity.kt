package com.hasankaya.travelbookheka

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import android.util.Log

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.hekasoftdesign.containerappdemo.model.City
import com.hekasoftdesign.containerappdemo.model.MyContainer
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager:LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var databaseFB : FirebaseFirestore
private  var cityMainContainerList:ArrayList<MyContainer> = ArrayList()
private lateinit var  sharedPreferencesLoadData: SharedPreferences
private lateinit var cityList:List<City>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        databaseFB = FirebaseFirestore.getInstance()
        sharedPreferencesLoadData = this.getSharedPreferences(packageName,android.content.Context.MODE_PRIVATE)
        if(sharedPreferencesLoadData.getInt("loadDataStatus",2)==2  ||sharedPreferencesLoadData.getInt("loadDataStatus",2)==0 ){
         //   getCityDataFromFirestore()
            //yeterli sürede çözemediğim bir hatadan dolayı fonksiyonu burada bırakıyorum.alternatif olarak localde çekiyorum verileri.
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setMapData()
    }

    fun getCityDataFromFirestore(){

        databaseFB.collection("Containers").addSnapshotListener{snapshot, exception->
            if(exception != null){
                Toast.makeText(applicationContext,exception.localizedMessage.toString(), Toast.LENGTH_LONG).show()
            }
            else{
                if(snapshot?.isEmpty==false){
                    val documents = snapshot.documents
                    for(document in documents){
                        var Acitycontainer : MyContainer = MyContainer(
                            document.get("CDate") as String,
                            1,
                            document.get("SensorID") as String,
                            20,
                            20,
                            document.get("latitude") as String,
                            document.get("longtitude") as String,
                            document.get("CName") as String
                        )
                        cityMainContainerList.add(Acitycontainer)
                    }
                }
            sharedPreferencesLoadData.edit().putInt("loadDataStatus",1).apply()
            }
        }
    }
    fun  getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
    fun setMapData(){
        val jsonFileString = getJsonDataFromAsset(applicationContext, "cities_of_turkey.json")
        val gson = Gson()
        val listCityType = object : TypeToken<List<City>>() {}.type
        var cities: List<City> = gson.fromJson(jsonFileString, listCityType)
        cityList =cities
        cities.forEach {
            println(it.name)
            //mMap.addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(),it.longitude.toDouble())))
        }
//        cities.forEach {
//        var CityContainerLocation = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
//          mMap.addMarker(MarkerOptions().position(CityContainerLocation).title(it.name +"\n" +it.region +"\n"+ it.population))
//
//    }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if(location != null){
                    val newUserLocation = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(newUserLocation).title("user"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation,15f))


                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)

            //last location, kayıtlı bilinen son lokasyonu alıyor.

            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(lastLocation != null){
                val lastLocationLatlng =LatLng(lastLocation.longitude, lastLocation.longitude)
               // mMap.addMarker(MarkerOptions().position(lastLocationLatlng).title("user"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatlng,15f))
            }

            cityList.forEach {
                mMap.addMarker(MarkerOptions().position(LatLng(it.latitude.toDouble(),it.longitude.toDouble())).title(it.name + "\n"+"Bölge: "+ it.region +"\n") )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if(grantResults.size > 0){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}