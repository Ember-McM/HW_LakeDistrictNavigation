package com.GLAS.LakeDistrictNavigation


import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class LocationFragment : Fragment() {
    private lateinit var mMap: GoogleMap
    var selectedPath : Polyline? = null
    private val args: LocationFragmentArgs by navArgs()


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap



        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))

        // Define the lake district
        val LAKES = LatLngBounds(
            LatLng(54.000233, -3.602606), //SW bounds
            LatLng(54.977333, -2.673426) //NE bounds
        )

        val LAKEBounds = LatLngBounds(
            LatLng(54.207619, -3.461018), //SW bounds
            LatLng(54.744094, -2.695331) //NE bounds
        )

        val TESTRENDERBounds = LatLngBounds(
            LatLng(54.207785, -3.673555), //SW bounds
            LatLng(54.759265, -2.496691) //NE bounds
        )

//        val options = BitmapFactory.Options()
//        options.inSampleSize = 6
//
//        val bitmap1 = BitmapFactory.decodeResource(resources, R.drawable.artboard_1_min,options)
//        val bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.artboard_2,options)
//        val bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.artboard_3,options)
//
//        val lakesOverlatTest1 = GroundOverlayOptions()
//            .image(BitmapDescriptorFactory.fromBitmap(bitmap1))
//            .positionFromBounds(TESTRENDERBounds)
//            .visible(true)
//
//        val lakesOverlatTest2 = GroundOverlayOptions()
//            .image(BitmapDescriptorFactory.fromBitmap(bitmap2))
//            .positionFromBounds(TESTRENDERBounds)
//            .visible(true)
//
//        val lakesOverlatTest3 = GroundOverlayOptions()
//            .image(BitmapDescriptorFactory.fromBitmap(bitmap3))
//            .positionFromBounds(TESTRENDERBounds)
//            .visible(true)
//
//        mMap.addGroundOverlay(lakesOverlatTest1)
//        mMap.addGroundOverlay(lakesOverlatTest2)
//        mMap.addGroundOverlay(lakesOverlatTest3)

        //Import Test Markers
        val IS : InputStream = resources.openRawResource(R.raw.heritage)
        val scanner = Scanner(IS)
        val inputStream = resources.openRawResource(R.raw.heritage)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var herritageMarker = BitmapDescriptorFactory.fromResource(R.drawable.test_asset_castle_pin)
        var ctr: Int
        try {
            ctr = inputStream.read()
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr)
                ctr = inputStream.read()
            }
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //Log.v("Text Data", byteArrayOutputStream.toString())
        try {
            // Parse the data into jsonobject to get original data in form of json.
            val jObject = JSONObject(
                byteArrayOutputStream.toString()
            )
            val jObjectResult: JSONObject = jObject.getJSONObject("Categories")
            val jArray = jObjectResult.getJSONArray("Heritage sites")
            var placeName = ""
            var description = ""
            var latitude = 0.0
            var longitude = 0.0

            for (i in 0 until jArray.length()) {
                placeName = jArray.getJSONObject(i).getString("Name")
                latitude = jArray.getJSONObject(i).getDouble("Latitude")
                longitude = jArray.getJSONObject(i).getDouble("Longitude")
                try {
                    val Link = jArray.getJSONObject(i).getString("Link")
                    if (Link !=null)
                        description = Link
                }
                catch (e : Exception){

                }
                Log.v("Place", placeName)
                var herritageMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(latitude,longitude))
                        .title(placeName)
                        .icon(herritageMarker)
                        .snippet(description)
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        val tileOptions = TileOverlayOptions().visible(true)
        tileOptions.tileProvider(CustomMapTileProvider(resources.assets))
        tileOptions.fadeIn(true)

        mMap.getUiSettings().setMapToolbarEnabled(false);
        //mMap.setMyLocationEnabled(true);
        mMap.addTileOverlay(tileOptions)

        //mMap.setMyLocationEnabled(true);

        //Import Test GPX
        val parser = GPXParser() // consider injection
        val am :AssetManager = resources.assets
        for (I in am.list("GPXs")!!){
            Log.v("Msg",I)
            try {
                val input: InputStream = requireContext().assets.open("GPXs/$I")
                val parsedGpx: Gpx? = parser.parse(input) // consider using a background thread
                parsedGpx?.let {
                    // do something with the parsed track
                    var points: ArrayList<LatLng?> = ArrayList()
                    val tracks = parsedGpx.tracks
                    for (track in tracks){
                        for (segments in track.trackSegments){
                            for (point in segments.trackPoints){
                                val lat = point.latitude
                                val lng = point.longitude
                                val position = LatLng(lat, lng)
                                points?.add(position)
                                //Log.v("Msg",position.toString())
                            }
                        }
                    }


                    var polyLineOptions = PolylineOptions()

                    polyLineOptions.addAll(points)
                    polyLineOptions.jointType(JointType.ROUND)
                    polyLineOptions.clickable(true)

                    polyLineOptions.width(10f)
                    polyLineOptions.color(Color.GRAY)
                    polyLineOptions.zIndex(1000f)
                    val polyline = googleMap.addPolyline(polyLineOptions!!)
                    polyline.tag = parsedGpx.metadata.name
                } ?: {
                    // error parsing track
                }
            } catch (e: IOException) {
                // do something with this exception
                e.printStackTrace()
            } catch (e: XmlPullParserException) {
                // do something with this exception
                e.printStackTrace()
            }
        }




        //Test opverlay
        // Test Render SW = 54.207785, -3.673555
        // Test Render NE = 54.759265, -2.496691
        // Bounding box = -407844.391296407 7207121.170564921 -278510.669573609 7315909.246688295

        // Add a marker
        val lake = LatLng(54.385222, -2.938333)
        mMap.addMarker(MarkerOptions().position(lake).title("Marker in LD"))


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LAKES.center, 9.5f))
        mMap.setLatLngBoundsForCameraTarget(LAKEBounds);
        mMap.setMinZoomPreference(9f)
        mMap.setMaxZoomPreference(15f)
        mMap.uiSettings.isRotateGesturesEnabled = false

        if (arguments?.getString("StartLocation") != "0.0,0.0"){

            val splitString = arguments?.getString("StartLocation")!!.split(",")

            val zoomLocation = LatLng(splitString[0].toDouble(),splitString[1].toDouble())
            Log.v("Nav","Juping to " + zoomLocation.toString())
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zoomLocation ,13f))
        }


        mMap.setOnPolylineClickListener {
            polyline -> polyline

                if (selectedPath != null){
                    selectedPath!!.color = Color.GRAY
                    selectedPath!!.zIndex = 1000f
                    }
                selectedPath = polyline
                polyline.color = Color.BLUE
            polyline.zIndex = 1010f
        }



    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }






}