package com.github.fredrik9000.firmadetaljer_android.company_details

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.fredrik9000.firmadetaljer_android.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class CompanyLocationMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var companyAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_location_map)

        companyAddress = intent.getStringExtra(ADDRESS)!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val latlng = getLocationFromAddress(companyAddress)
        latlng?.let {
            map.addMarker(MarkerOptions().position(it).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLng(it))
        }  ?: run {
            Toast.makeText(applicationContext, R.string.location_not_found, Toast.LENGTH_LONG).show()
        }
    }

    private fun getLocationFromAddress(strAddress: String): LatLng? {

        val coder = Geocoder(this)
        val address: List<Address>?
        var p1: LatLng? = null

        try {
            // May throw an IOException
            // TODO: Call on a background thread
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null || address.isEmpty()) {
                return null
            }

            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)

        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        return p1
    }

    companion object {
        const val ADDRESS = "ADDRESS"
    }
}
