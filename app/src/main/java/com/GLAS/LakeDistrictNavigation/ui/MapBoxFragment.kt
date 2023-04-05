package com.GLAS.LakeDistrictNavigation.ui

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.GLAS.LakeDistrictNavigation.*
import com.GLAS.LakeDistrictNavigation.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.bindgen.Expected
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.switchCase
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.generated.vectorSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import org.jgrapht.Graph
import org.jgrapht.Graphs
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic
import org.jgrapht.alg.shortestpath.AStarShortestPath
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.WeightedMultigraph
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//"mapbox://styles/embermcm/cl6235hp0000815ldc0x17m59"
//"https://api.mapbox.com/v4/embermcm.cvd15om8/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoiZW1iZXJtY20iLCJhIjoiY2w2MjVzNXJnMGRtaTNrcG5qbzNwcmI4MSJ9.OuyiLSyNbCSjMM5p_4SRiw"
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapBoxFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
data class MapNode(val name: String, val latitude : Double, val longitude : Double )
data class Connection(val startPoint: Point, val endPoint : Point, val usable: Boolean, val details : ConnectionDetails?)
data class ConnectionVerbose(val startName : String, val endName : String, val connection: Connection)
//Walk Rout	Car Route	Bike Rout	Bus Route	Ferry Rout	Train Rout
data class ConnectionDetails(val Walk : String?, val Car : String?,val Bike : String?,val Bus : String?,val Ferry : String?,val Train : String?)
data class RouteLookup(val id : String, val transportType : String)
data class RouteValue(val id: String, val title : String, val transportType: String, val distance : Double, val time : Double, val health : Double, var Co2 : Double, val tranquility : Double, val reliability : Double, val difficulty : String, val startName : String, val endName: String, var inverted : Boolean = false, var favorate : Boolean = false, var hexColour : String = "", var CoSave : Double = 0.0)
data class CodeToGPX(val id: String, val points : ArrayList<Point>, val inverted: Boolean)
data class LocationToVisit (val name: String, val location: Location, val minDistance: Double)


enum class ClickMode{
    INFO,
    START,
    END,
    CHANGE,
    ROUTE,
}

class MapBoxFragment : Fragment(), OnMapClickListener {
    private lateinit var reqestPermissions: ActivityResultLauncher<String>

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mapView: MapView

    private lateinit var mapGraph: Graph<MapNode, DefaultWeightedEdge>
    private var NodeList = ArrayList<MapNode>()

    private lateinit var mapGraphRedux: Graph<MapNode, DefaultWeightedEdge>
    private lateinit var mapGraphBus: Graph<MapNode, DefaultWeightedEdge>
    private var nodeListRedux = ArrayList<MapNode>()
    private var routeListRedux = ArrayList<RouteValue>()
    private var routeListBus = ArrayList<RouteValue>()

    private var GPXList = ArrayList<CodeToGPX>()


    private var Connections = ArrayList<Connection>()
    private var ConnectionVerboses = ArrayList<ConnectionVerbose>()
    private var RoutesValueList = ArrayList<RouteValue>()
    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager

    private lateinit var pinAnnotationManager : PointAnnotationManager
    private lateinit var pointAnnotationManager : PointAnnotationManager


    private lateinit var polylineAnnotationManager : PolylineAnnotationManager
    //private lateinit var polylineAnnotationManagerRoutes : PolylineAnnotationManager
    private lateinit var polylineAnnotationManagerWalk : PolylineAnnotationManager
    private lateinit var polylineAnnotationManagerBike : PolylineAnnotationManager
    private lateinit var polylineAnnotationManagerCar : PolylineAnnotationManager
    private lateinit var polylineAnnotationManagerBus : PolylineAnnotationManager


    private lateinit var fab : FloatingActionButton
    private val bottomSheetView by lazy { requireView().findViewById<ConstraintLayout>(R.id.bottomSheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewConections: RecyclerView
    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var standardBottomSheetInfoBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var standardBottomSheetConnectionsBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var sheetControllers: ConstraintLayout

    lateinit var routesCard: View

    private var markerId = 0
    private var markerWidth = 0
    private var markerHeight = 20
    private val asyncInflater by lazy { AsyncLayoutInflater(requireContext()) }

    private lateinit var currentClick : String
    private lateinit var currentConnections : List<String>

    private lateinit var clickMode : ClickMode

    private  lateinit var infoSheetTitle: TextView
    private  lateinit var infoSheetContent: TextView

    var showLandmarks = true
    var showLocations = true
    var minZoom = 10.5
    var fabVisible = false
    var walkingLayerVis = true
    var cyclingLayerVis = true
    var busLayerVis = true

    lateinit var navTextStart : TextView
    lateinit var navTextEnd : TextView
    lateinit var startPointAnotation : PointAnnotation
    lateinit var endPointAnotation : PointAnnotation
    lateinit var conectionRouteValues : ArrayList<RouteValue>

    lateinit var currentRoute : DirectionsRoute
    lateinit var walkFileList : ArrayList<String>
    lateinit var clientDirections : MapboxDirections
    lateinit var dispayRoutes : ArrayList<RouteValue>
    lateinit var locationsToVisit : ArrayList<LocationToVisit>

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference : DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var myLocation = "No Location_No Location"
    private lateinit var notificationManager: NotificationManager

    var MyLoc : Location? = null
    var CityName : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        reqestPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isGranted : Boolean ->
            if (isGranted){
                SaveWalkGPX()
                Log.v("Permsions","IsGranted")
            }
            else{
                Log.v("Permsions","NotGranted")
            }
        }



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_box, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Read any and all data first
        mapView = requireView().findViewById(R.id.mapView)

        initLocationComponent()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        createNotificationChannel()



        mapGraph = WeightedMultigraph<MapNode, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        mapGraphRedux = WeightedMultigraph<MapNode, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        mapGraphBus = WeightedMultigraph<MapNode, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
        //ReadGraph()
        ReadBespokeGraph()
        LoadAllGPX()
        readLocationsToVisit()

        firebaseAnalytics = Firebase.analytics
        firebaseDatabase = Firebase.database

        //Set up Annotation Manager
        val annotationApi = mapView.annotations

        polylineAnnotationManager = annotationApi.createPolylineAnnotationManager()
//        polylineAnnotationManagerRoutes = annotationApi.createPolylineAnnotationManager()
        polylineAnnotationManagerWalk = annotationApi.createPolylineAnnotationManager()
        polylineAnnotationManagerCar = annotationApi.createPolylineAnnotationManager()
        polylineAnnotationManagerBike = annotationApi.createPolylineAnnotationManager()
        polylineAnnotationManagerBus = annotationApi.createPolylineAnnotationManager()

        pointAnnotationManager = annotationApi.createPointAnnotationManager()
        pinAnnotationManager = annotationApi.createPointAnnotationManager()

        viewAnnotationManager = mapView.viewAnnotationManager

        val standardBottomSheet = requireView().findViewById<FrameLayout>(R.id.bottomSheet)
        standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val standardBottomSheetInfo = requireView().findViewById<FrameLayout>(R.id.bottomSheetInfo)
        standardBottomSheetInfoBehavior = BottomSheetBehavior.from(standardBottomSheetInfo)
        standardBottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val standardBottomSheetConnection = requireView().findViewById<FrameLayout>(R.id.bottomSheetConections)
        standardBottomSheetConnectionsBehavior  = BottomSheetBehavior.from(standardBottomSheetConnection)
        standardBottomSheetConnectionsBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        infoSheetTitle = requireView().findViewById(R.id.botomSheetInfoTitle)
        infoSheetContent = requireView().findViewById(R.id.botomSheetContnet)

        navTextStart = requireView().findViewById(R.id.searchStart)
        navTextEnd = requireView().findViewById(R.id.searchEnd)


        //Load Styles and Stuff like that
        var iconWidth = 50
        var iconWidthLarge = 65
        var iconWidthVeryLarge = 70
        mapboxMap = mapView.getMapboxMap().apply{
            loadStyle(
                style(styleUri = "mapbox://styles/embermcm/cla8ahqd1000y14og5yx92ggf") {

                    addOnMapClickListener(this@MapBoxFragment)


//                +rasterSource("LakesMap"){
//                    tileSize(256)
//                    //tileSet("none",)
//                    //tiles(listOf("https://api.mapbox.com/v4/embermcm.cvd15om8/{z}/{x}/{y}.png"))
//                    url( "mapbox://embermcm.cvd15om8")
//                    volatile(true) }

                    +image("start_node_image") {
                        bitmap(resizeMapIcons(50,50,R.drawable.redcircle)!!)
                    }

                    +image("dir_node_image") {
                        bitmap(resizeMapIcons(iconWidthVeryLarge,(iconWidthVeryLarge / 0.85f).toInt(),R.drawable.pin_diretion)!!)
                    }

                    +image("heritage_image"){
                        bitmap(resizeMapIcons(1,1,R.drawable.nullicon_256x)!!)
                    }

                    //Each pin is originaly 256 x 304
                    +image("waterfall_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_waterfall_2)!!)
                    }
                    +image("castle_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_castle_2)!!)
                    }
                    +image("garden_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_garden_2)!!)
                    }
                    +image("landmark_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_landmark_2)!!)
                    }
                    +image("museum_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_museum_2)!!)
                    }
                    +image("nature_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_nature_2)!!)
                    }
                    +image("writer_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_writer_2)!!)
                    }
                    +image("boat_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_boat_2)!!)
                    }
                    +image("activity_pin"){
                        bitmap(resizeMapIcons(iconWidth,(iconWidth / 0.85f).toInt(),R.drawable.pin_activity)!!)
                    }

                    +image("lake_pin"){
                        bitmap(resizeMapIcons(iconWidthLarge,(iconWidthLarge / 0.85f).toInt(),R.drawable.pin_lake)!!)
                    }
                    +image("village_pin"){
                        bitmap(resizeMapIcons(iconWidthLarge,(iconWidthLarge / 0.85f).toInt(),R.drawable.pin_village)!!)
                    }

                    +image("startwalk_pin"){
                        bitmap(resizeMapIcons(40,40,R.drawable.start_mws_pin)!!)
                    }
                    +image("turnwalk_pin"){
                        bitmap(resizeMapIcons(40,40,R.drawable.turn_mws_pin)!!)
                    }

                    +vectorSource("start-nodes"){
                        url("mapbox://embermcm.cl6m1uawu006s28n7bapha1sp-3b64x")
                    }

                    +vectorSource("miles-without-stiles"){
                        url("mapbox://embermcm.clbns8f620bn327psv1gfb7s0-6fze9")
                    }




                    +vectorSource("walking-start-nodes"){
                        url("mapbox://embermcm.clagnf9oz2bfw20qz68zh206r-9rz9v")
                    }

                    +vectorSource("heritage-nodes"){
                        url("mapbox://embermcm.cl9flt64p1jhw21tmudacupho-81kjr")
                    }

                    +vectorSource("location-nodes"){
                        url("mapbox://embermcm.cl9mknfww02xs2cnvtkmteagm-408uy")

                    }

                    +geoJsonSource("walk-network"){
                        url("asset://walking_routes.geojson")
                    }

                    +geoJsonSource("bike-network"){
                        url("asset://cycling_routes.geojson")
                    }

                    +geoJsonSource("bus-network"){
                        url("asset://bus_routes.geojson")
                    }

                    +lineLayer("Walking-Layer", "walk-network"){
                        lineCap(LineCap.ROUND)
                        lineJoin(LineJoin.ROUND)



                        lineOpacity(1.0)
                        lineWidth(2.0)
                        lineColor(ContextCompat.getColor(requireContext(),R.color.splitComp1Dark))
                        visibility(Visibility.NONE)
                    }

                    +lineLayer("Cycling-Layer", "bike-network"){
                        lineCap(LineCap.ROUND)
                        lineJoin(LineJoin.ROUND)
                        lineOpacity(1.0)
                        lineWidth(2.0)
                        lineColor(ContextCompat.getColor(requireContext(),R.color.splitComp2Dark))
                        visibility(Visibility.NONE)
                    }

                    +lineLayer("Bus-Layer", "bus-network"){
                        lineCap(LineCap.ROUND)
                        lineJoin(LineJoin.ROUND)
                        lineOpacity(1.0)
                        lineWidth(2.0)
                        lineColor(ContextCompat.getColor(requireContext(),R.color.splitComp2Light))
                        visibility(Visibility.NONE)
                    }

                    +symbolLayer("Miles_Layer","miles-without-stiles"){
                        sourceLayer("MapWalks")
                        iconImage(
                            switchCase {
                                eq {
                                    get {
                                        literal("Icon")
                                    }
                                    literal("start")
                                }
                                literal("startwalk_pin")

                                eq {
                                    get {
                                        literal("Icon")
                                    }
                                    literal("turn")
                                }
                                literal("turnwalk_pin")
                                literal("heritage_image")
                            }
                        )
                        iconAnchor(IconAnchor.CENTER)
                        visibility(Visibility.VISIBLE)

                        iconAllowOverlap(true)

                        minZoom(11.0)
                    }


                    +symbolLayer("Heritage-Layer","heritage-nodes") {
                        sourceLayer("HeritageSites")
                        //iconImage("heritage_image")
                        iconImage(
                            switchCase {
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("waterfall")
                                }
                                literal("waterfall_pin")
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("boat")
                                }
                                literal("boat_pin")
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("boat")
                                }
                                literal("boat_pin")
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("castle")
                                }
                                literal("castle_pin")
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("garden")
                                }
                                literal("garden_pin")
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("museum")
                                }
                                literal("museum_pin")
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("nature")
                                }
                                literal("nature_pin")
                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("writer")
                                }
                                literal("writer_pin")

                                eq {
                                    get {
                                        literal("Marker")
                                    }
                                    literal("activity")
                                }
                                literal("activity_pin")


                                literal("landmark_pin")
                            }
                        )
                        iconAnchor(IconAnchor.BOTTOM)

                        if (showLandmarks){
                            visibility(Visibility.VISIBLE)
                        }
                        else {
                            visibility(Visibility.NONE)
                        }

                        iconAllowOverlap(true)
                        minZoom(minZoom)
                    }

                    +symbolLayer("Locations-Layer","location-nodes") {
                        sourceLayer("Locations")
                        //iconImage("heritage_image")
                        iconImage(
                            switchCase {
                                eq {
                                    get {
                                        literal("Catagory")
                                    }
                                    literal("lake")
                                }
                                literal("lake_pin")

                                literal("village_pin")
                            }
                        )
                        iconAnchor(IconAnchor.BOTTOM)

                        if (showLocations){
                            visibility(Visibility.VISIBLE)
                        }
                        else {
                            visibility(Visibility.NONE)
                        }

                        iconAllowOverlap(true)
                        minZoom(minZoom -1.5)


                    }

                    +symbolLayer("Start-Layer","start-nodes"){
                        sourceLayer("startnodes")
                        iconImage("start_node_image")
                        iconAnchor(IconAnchor.CENTER)
                        visibility(Visibility.NONE)
                        iconAllowOverlap(true)
                    }

                    +symbolLayer("Walking-Start-Layer","walking-start-nodes"){
                        sourceLayer("walking_starts")
                        iconImage("dir_node_image")
                        iconAnchor(IconAnchor.BOTTOM)
                        visibility(Visibility.NONE)
                        iconAllowOverlap(true)
                    }






                }
            )
        }


        //Handle the UI
        routesCard = requireView().findViewById(R.id.card_location_info)
        //Set up switches
        val landmarkSwitch = requireView().findViewById<Switch>(R.id.landmarkSwitch)
        //landmarkSwitch.isChecked = showLandmarks
        landmarkSwitch.setOnClickListener {
            ToggleLandmarks(landmarkSwitch.isChecked, "Heritage-Layer")
            showLandmarks = landmarkSwitch.isChecked
        }

        val locationSwitch = requireView().findViewById<Switch>(R.id.locationSwitch)
        //locationSwitch.isChecked = showLocations
        locationSwitch.setOnClickListener {
            ToggleLandmarks(locationSwitch.isChecked, "Locations-Layer")
            showLocations = locationSwitch.isChecked
        }

        val walkingOverlay  =  requireView().findViewById<CardView>(R.id.walking_layer)
        walkingOverlay.setOnClickListener {
            ToggleLandmarks(walkingLayerVis, "Walking-Layer")
            walkingLayerVis = !walkingLayerVis
        }

        val cyclingOverlay  =  requireView().findViewById<CardView>(R.id.cycling_layer)
        cyclingOverlay.setOnClickListener {
            ToggleLandmarks(cyclingLayerVis, "Cycling-Layer")
            cyclingLayerVis = !cyclingLayerVis
        }

        val busOverlay  =  requireView().findViewById<CardView>(R.id.bus_layer)
        busOverlay.setOnClickListener {
            ToggleLandmarks(busLayerVis, "Bus-Layer")
            busLayerVis = !busLayerVis
        }


        val mapOptionButton = requireView().findViewById<ImageButton>(R.id.layerFab)
        val switchesContaier = requireView().findViewById<CardView>(R.id.LayersCard)
        mapOptionButton.setOnClickListener {
            if (switchesContaier.visibility == View.VISIBLE){
                switchesContaier.visibility = View.GONE
            }
            else {
                switchesContaier.visibility = View.VISIBLE
            }

        }
        recyclerView = requireView().findViewById(R.id.routesRecyclerView)
        recyclerViewConections = requireView().findViewById(R.id.conectionsRecyclerView)

        fab = requireView().findViewById(R.id.button_plan)
        sheetControllers = requireView().findViewById(R.id.sheetControles)
        clickMode = ClickMode.INFO
        currentClick = ""
        currentConnections = listOf("")
        fab.setOnClickListener { view ->
            ToggleFABMenu()
        }
        val beginRouteButton : FloatingActionButton = requireView().findViewById(R.id.fab_StartRoute)

        getLocation()

        beginRouteButton.setOnClickListener{
            val navStart = navTextStart.text.toString()
            val navEnd = navTextEnd.text.toString()


            //If both start and end exist AND are not the same

            if (navStart != "" && navEnd != ""){
                val startNode = nodeListRedux.firstOrNull { it.name == navStart }!!
                val endNode = nodeListRedux.firstOrNull { it.name == navEnd }!!
                if (navStart != navEnd){
                    //Setup the recycler data
                    conectionRouteValues = ArrayList<RouteValue>()

                    dispayRoutes = ArrayList<RouteValue>()

                    TraverseMap(navStart,navEnd)
                    TraverseBus(navStart,navEnd)
                    NavigateMap(fromLngLat(startNode.longitude,startNode.latitude), fromLngLat(endNode.longitude,endNode.latitude),navStart,navEnd)


                    ToggleLandmarks(showLandmarks, "Heritage-Layer")
                    ToggleLandmarks(showLocations, "Locations-Layer")

                    BuildRouteReceycleView2(dispayRoutes)
                    Log.v("Recycer", dispayRoutes.count().toString())
                    ShowBottomSheet()
                    getLocation()
                    val locationInfo = myLocation.split("_")
                    SendRouteToDatabase(navStart,navEnd,locationInfo[0],locationInfo[1],"Request",null)
                }
            }
        }




        //AddMarkers(pointAnnotationManager)

        //Zoom to The correct area
        val Lakes_Bounds: CameraBoundsOptions = CameraBoundsOptions.Builder()
            .bounds(
                CoordinateBounds(
                    fromLngLat( -3.85,53.9 ),
                    fromLngLat(-2.5,55.1),
                    false
                )
            )
            .maxZoom(17.0)
            .minZoom(7.0)

            .build()

        mapView.getMapboxMap().setBounds(Lakes_Bounds)
        //ReadRoutes()


        //Load any details coming from another fragment
        if (arguments?.getString("StartLocation") != "0.0,0.0"){

            val splitString = arguments?.getString("StartLocation")!!.split(",")

            val zoomLocation = LatLng(splitString[0].toDouble(),splitString[1].toDouble())
            Log.v("Nav","Juping to " + zoomLocation.toString())
            val cameraPosition = CameraOptions.Builder()
                .center(
                    Point.fromLngLat(
                        zoomLocation.longitude,zoomLocation.latitude
                    )
                )
                .zoom(14.0)
                .build()
            mapView.getMapboxMap().setCamera(cameraPosition)
        }

        if (arguments?.getString("StartRoute") != "None"){
            val splitString = arguments?.getString("StartRoute")!!.split("_")
            val startNode = nodeListRedux.firstOrNull { it.name == splitString[1] }!!
            val endNode = nodeListRedux.firstOrNull { it.name == splitString[2] }!!

            dispayRoutes = ArrayList<RouteValue>()

            if (splitString[0].lowercase() == "walk"){
                TraverseMap(splitString[1],splitString[2])
            }
            else if (splitString[0].lowercase() == "driving"){
                getRoute(mapView.getMapboxMap(),fromLngLat(startNode.longitude,startNode.latitude),fromLngLat(endNode.longitude,endNode.latitude),DirectionsCriteria.PROFILE_DRIVING,"#ee4e8b", splitString[1],splitString[2]  )

            }
            else if (splitString[0].lowercase() == "cycling"){
                getRoute(mapView.getMapboxMap(),fromLngLat(startNode.longitude,startNode.latitude),fromLngLat(endNode.longitude,endNode.latitude),DirectionsCriteria.PROFILE_CYCLING,  "#FF5733", splitString[1],splitString[2]  )
            }
            else if (splitString[0].lowercase() == "bus"){
                TraverseBus(splitString[1],splitString[2])
            }

            BuildRouteReceycleView2(dispayRoutes)
            Log.v("Recycer", dispayRoutes.count().toString())

            ShowBottomSheet()
            if (splitString[0].lowercase() == "bus"){
                highlightRoute(polylineAnnotationManagerBus)
            }
            //val startRoute = BuildConectionDetails(splitString[0],splitString[1])
            Log.v("Nav","Juping to " + splitString)

            ToggleFABMenu()

        }

        //AddGraphVisulisation()


    }



    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true

            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.location_puck,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.location_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.02)
                    }
                    stop {
                        literal(20.0)
                        literal(0.7)
                    }
                }.toJson()
            )
        }

    }



    //Use a routing api to find driving and cycling connections between points
    fun NavigateMap(startPoint: Point, endPoint: Point, startName: String, endName: String)
    {
        //getRoute(mapView.getMapboxMap(),startPoint,endPoint,DirectionsCriteria.PROFILE_WALKING,  "#FFC300", startName,endName )
        getRoute(mapView.getMapboxMap(),startPoint,endPoint,DirectionsCriteria.PROFILE_CYCLING,  "#FF5733", startName,endName  )
        getRoute(mapView.getMapboxMap(),startPoint,endPoint,DirectionsCriteria.PROFILE_DRIVING,"#ee4e8b", startName,endName )
    }


    fun CheckRouteForCoolStuff(points: List<Point>){
        var nearbyMaxDist = 1000.0
        val nearbyLocations = ArrayList<LocationToVisit>()
        pinAnnotationManager.deleteAll()
        Log.v("Visit", locationsToVisit.count().toString())
        //For each point in the list
        for (point in points){
            val pointLocation = Location("") ;  pointLocation.latitude = point.latitude() ;  pointLocation.longitude = point.longitude()

            //check if it is near any features

            for (feature in locationsToVisit){

                val distance = pointLocation.distanceTo(feature.location)
                if (distance < nearbyMaxDist){
                    if (!nearbyLocations.contains(feature)){
                        nearbyLocations.add(feature)

                    }
                    else{
                        //Update Distance?
                    }
                }
            }
        }

        val pinIconR = bitmapFromDrawableRes(requireContext(), R.drawable.pinr)
        val pinIconL = bitmapFromDrawableRes(requireContext(), R.drawable.pinl)
        var pinAlternater = pinIconR

        for (feature in nearbyLocations){
            val myPoint = Point.fromLngLat(feature.location.longitude,feature.location.latitude)
            Log.v("Visit",feature.name)
            Log.v("Visit",myPoint.toString())


            if (pinAlternater == pinIconR){
                pinAlternater = pinIconL
            }
            else{
                pinAlternater = pinIconR
            }



            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(myPoint)
                .withIconImage(pinAlternater!!)
                .withIconAnchor(IconAnchor.BOTTOM)
                .withSymbolSortKey(40.0)

            var pinPointAnntionation = pinAnnotationManager.create(pointAnnotationOptions)

        }
    }



    //call the routing API and display it on the map
    private fun getRoute(mapboxMap: MapboxMap?, origin: Point, destination: Point,  criteria: String,color: String, startName: String, endName: String)  {

        val client = MapboxDirections.builder()
            .routeOptions(
                RouteOptions.builder()
                    .coordinatesList(listOf(
                        origin,
                        destination
                    ))
                    .profile(criteria)
                    .overview(DirectionsCriteria.OVERVIEW_SIMPLIFIED)

                    .build()
            )
            .accessToken(resources.getString(R.string.mapbox_access_token))
            .build()

        client.enqueueCall(object : Callback<DirectionsResponse?> {
            override fun onResponse(call: Call<DirectionsResponse?>?, response: Response<DirectionsResponse?>) {

                Log.v("Routes","Response code: " + response.code())
                if (response.body() == null) {
                    Log.v("Routes","No routes found, make sure you set the right user and access token.")
                    return
                } else if (response.body()!!.routes().size < 1) {
                    Log.v("Routes","No routes found")
                    return
                }
                currentRoute = response.body()!!.routes().get(0)
                if (currentRoute != null){
                    val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                        .withGeometry(LineString.fromPolyline(currentRoute.geometry()!!, PRECISION_6))
                        // Style the line that will be added to the map.
                        .withLineColor(color)
                        .withLineWidth(5.0)
                        .withLineSortKey(1.0)
                        .withLineOpacity(0.2)



                    val fraction : Int
                    val mode : String
                    if (criteria == DirectionsCriteria.PROFILE_CYCLING) {
                        polylineAnnotationManagerBike.create(polylineAnnotationOptions)
                        fraction = polylineAnnotationOptions.getPoints().size * 1/3
                        mode = "Bike"
                    }
                    else {
                        polylineAnnotationManagerCar.create(polylineAnnotationOptions)
                        fraction = polylineAnnotationOptions.getPoints().size * 2/3
                        mode = "Car"

                        UpdateCarbonSavings(currentRoute.distance())
                    }

                    //PlaceRouteID(polylineAnnotationOptions.getPoints()[fraction], mode, "" )
                    val myRoute = calculateRouteDetails(currentRoute,criteria, startName,endName )

                    if (criteria == DirectionsCriteria.PROFILE_CYCLING){
                        //Add a temp carbon savings
                        myRoute.CoSave = currentRoute.distance()/1000 * 0.15
                    }

                    dispayRoutes.add(myRoute)
                    recyclerView.adapter!!.notifyItemInserted(recyclerView.adapter!!.itemCount-1)
                    if (recyclerView.adapter!!.itemCount == 1){
                        if (criteria == DirectionsCriteria.PROFILE_CYCLING) {
                            highlightRoute(polylineAnnotationManagerBike)
                        }
                        else{
                            highlightRoute(polylineAnnotationManagerCar)
                        }
                    }

                }




            }

            override fun onFailure(call: Call<DirectionsResponse?>, t: Throwable) {
                Log.v("Routeing",t.toString())
            }
        })
    }

    fun UpdateCarbonSavings(distance: Double){
        //Co 2 of car roughly 150g per km
        var Co2Mult =  0.15
        var carbonCost = (distance/1000) * Co2Mult

        for (route in dispayRoutes) {
            if (route.transportType == "Walk" || route.transportType == "cycling"){
                route.CoSave = carbonCost
                Log.v("carbonUpdate", "Updated " + route.transportType)
            }
            else if (route.transportType == "Bus"){
                var savings = carbonCost - (route.distance * 0.06)
                if (savings < 0){
                    savings = 0.0
                }

                route.CoSave = savings


                Log.v("carbonUpdate", "Updated " + route.transportType)
                Log.v("carbonUpdate", "Bus Co2 = " + carbonCost + "-" + route.Co2)
            }
        }
        recyclerView.adapter!!.notifyDataSetChanged()
    }

    //Use some basic calculations to geuss route details
    fun calculateRouteDetails(route : DirectionsRoute, routeMode : String, startName: String, endName: String) : RouteValue
    {
        val distance = currentRoute.distance()/1000
        val time = currentRoute.duration() / 3600
        var health = 0.0
        var difficulty = 1
        var tranquility = 3.0
        var reliability = 5.0
        var co2 = 0.0
        if (routeMode == DirectionsCriteria.PROFILE_WALKING){
            health = distance * 100 / 1.6
            if (distance > 5){
                //Short walk
                difficulty = 2
                tranquility = 3.5
                reliability = 5.0
            }
            if (distance > 12){
                difficulty = 3
                tranquility = 4.0
                reliability = 4.5
            }

            if (distance > 18){
                difficulty = 4
                tranquility = 4.5
                reliability = 3.5
            }

        }
        else if (routeMode == DirectionsCriteria.PROFILE_CYCLING){
            health = distance * 32


            if (distance > 10){
                //Short cycle
                difficulty = 1
                tranquility = 3.5
                reliability = 5.0
            }
            if (distance > 20){
                difficulty = 2
                tranquility = 4.0
                reliability = 4.5
            }
            if (distance > 30){
                difficulty = 3
                tranquility = 4.5
                reliability = 3.5
            }
        }
        else {
            //Co 2 of car roughly 150g per km
            co2 = distance * 0.15



        }

        return RouteValue(routeMode,routeMode,routeMode,distance,time,health,co2,tranquility,reliability,DifficultySwitch(difficulty),startName,endName)
    }

    fun highlightRoute(polylineAnnotationManagerHighlight: PolylineAnnotationManager){
        Log.v("Highlight", polylineAnnotationManagerHighlight.toString() )
        for (line in polylineAnnotationManagerWalk.annotations){ line.lineOpacity = 0.2 ; polylineAnnotationManagerWalk.update(line) }
        for (line in polylineAnnotationManagerBus.annotations){ line.lineOpacity = 0.2 ; polylineAnnotationManagerBus.update(line) }
        for (line in polylineAnnotationManagerBike.annotations){ line.lineOpacity = 0.2 ; polylineAnnotationManagerBike.update(line) }
        for (line in polylineAnnotationManagerCar.annotations){ line.lineOpacity = 0.2 ; polylineAnnotationManagerCar.update(line) }

        val bigList = ArrayList<Point>()
        for (line in polylineAnnotationManagerHighlight.annotations){ line.lineOpacity = 1.0
            polylineAnnotationManagerHighlight.update(line)


            bigList.addAll(line.points)
        }
        val cameraPosition = mapboxMap.cameraForCoordinates(bigList,EdgeInsets(100.0, 75.0, 850.0, 75.0),null,null)
        // Set camera position
        mapboxMap.setCamera(cameraPosition)
        CheckRouteForCoolStuff(bigList)
    }


    fun PlaceRouteID(point: Point, routeMode: String, text : String ){
        val newView = layoutInflater.inflate(R.layout.route_quicklook,null)
        var quicklook =  viewAnnotationManager.addViewAnnotation(
            resId = R.layout.route_quicklook,
            options = viewAnnotationOptions {
                geometry(point)
                //associatedFeatureId(markerId)
                anchor(ViewAnnotationAnchor.TOP)
                allowOverlap(true)
            },
            asyncInflater = asyncInflater
        ) { viewAnnotation ->

            // calculate offsetY manually taking into account icon height only because of bottom anchoring
            viewAnnotationManager.updateViewAnnotation(
                viewAnnotation,
                viewAnnotationOptions {
                    offsetY(0)
                }
            )

            if (routeMode == "Walk") {viewAnnotation.findViewById<ImageView>(R.id.walk).visibility = View.VISIBLE
                viewAnnotation.findViewById<ImageView>(R.id.walk).setImageResource(R.drawable.walk)}
            if (routeMode == "Bike") {viewAnnotation.findViewById<ImageView>(R.id.bike).visibility = View.VISIBLE
                viewAnnotation.findViewById<ImageView>(R.id.bike).setImageResource(R.drawable.bike)}
            if (routeMode == "Car") {viewAnnotation.findViewById<ImageView>(R.id.car).visibility = View.VISIBLE
                viewAnnotation.findViewById<ImageView>(R.id.car).setImageResource(R.drawable.car)}
            if (routeMode == "Bus") {viewAnnotation.findViewById<ImageView>(R.id.bus).visibility = View.VISIBLE
                viewAnnotation.findViewById<ImageView>(R.id.bus).setImageResource(R.drawable.bus)                         }
            if (routeMode == "Train") {viewAnnotation.findViewById<ImageView>(R.id.train).visibility = View.VISIBLE
                viewAnnotation.findViewById<ImageView>(R.id.train).setImageResource(R.drawable.train)}
            if (routeMode == "Ferry") {viewAnnotation.findViewById<ImageView>(R.id.ferry).visibility = View.VISIBLE
                viewAnnotation.findViewById<ImageView>(R.id.ferry).setImageResource(R.drawable.ferry)}

            if (text != ""){
                viewAnnotation.findViewById<TextView>(R.id.busNumber).visibility = View.VISIBLE
                viewAnnotation.findViewById<TextView>(R.id.busNumber).text = text
            }

            viewAnnotation.findViewById<CardView>(R.id.quickLookCard).setOnClickListener(){
                highLightTransportType(routeMode.lowercase())
            }

            Log.v("Graphics", routeMode)
            if (viewAnnotation.findViewById<ImageView>(R.id.walk).visibility == View.VISIBLE){
                Log.v("Graphics", "Where are you?" )
            }


        }

    }


    //Set a map layer visibility
    fun ToggleLandmarks(value : Boolean, layerId : String){
        var layerVis = mapView.getMapboxMap().getStyle()?.getLayer(layerId)
        if (value){
            layerVis?.visibility(Visibility.VISIBLE)
        }
        else{
            layerVis?.visibility(Visibility.NONE)
        }
    }

    fun BuildConectionDetails(transportType: String, transportId : String) : ConnectionDetails{
        if (transportType == "Walk"){
            return  ConnectionDetails(transportId,null,null,null,null,null)
        }
        else if (transportType == "Bike"){
            return  ConnectionDetails(null,null,transportId,null,null,null)
        }
        else if (transportType == "Car"){
            return  ConnectionDetails(null,transportId,null,null,null,null)
        }
        else if (transportType == "Bus"){
            return  ConnectionDetails(null,null,null,transportId,null,null)
        }
        else if (transportType == "Ferry"){
            return  ConnectionDetails(null,null,null,null,transportId,null)
        }
        else {
            return  ConnectionDetails(null,null,null,null,null,transportId)
        }
    }

    //Display a line in the map's style (vector line that can use some features like dashes)
    fun AddLineToStyle(lineCords: ArrayList<Point>, sourceName: String){
        mapView.getMapboxMap().getStyle {
            // Specify a unique string as the source ID (SOURCE_ID)
            // and reference the location of source data
             it.addSource(geoJsonSource(sourceName){
                 featureCollection(FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromLngLats(lineCords))))
             })

            it.addLayer(lineLayer(sourceName + "layer",sourceName) {
                lineCap(LineCap.ROUND)
                lineJoin(LineJoin.ROUND)
                //lineDasharray(listOf(3.5,2.5))
                lineSortKey(30.0)


                lineOpacity(1.0)
                lineWidth(4.0)
                lineColor(ContextCompat.getColor(requireContext(),R.color.splitComp1Light))
            })
        }
        Log.v("Layers","Added" + sourceName)
    }


    fun RemoveAllLinesFromStyle(){
        var x = 6
        while (x >= 0){

            mapView.getMapboxMap().getStyle {
                // Specify a unique string as the source ID (SOURCE_ID)
                // and reference the location of source data
                it.removeStyleLayer(x.toString()+"layer")
                it.removeStyleSource(x.toString())
                Log.v("Layers","Removed" + x)

                x--
            }
        }
    }

    fun RemoveLineFromStyle(id : String){
            mapView.getMapboxMap().getStyle {
                // Specify a unique string as the source ID (SOURCE_ID)
                // and reference the location of source data
                it.removeStyleLayer(id.toString()+"layer")
                it.removeStyleSource(id.toString())
                Log.v("Layers","Removed" + id)


        }
    }

    fun AddPointsToStyle(Cord1: Point, Cord2: Point, sourceName: String){
        mapView.getMapboxMap().getStyle {
            // Specify a unique string as the source ID (SOURCE_ID)
            // and reference the location of source data
            it.addSource(geoJsonSource(sourceName+"C"){
                featureCollection(FeatureCollection.fromFeature(Feature.fromGeometry(LineString.fromLngLats(
                    listOf(Cord1,Cord2)))))
            })

            it.addLayer(circleLayer(sourceName + "BGlayer",sourceName+"C") {
                circleRadius(7.0)
                circleColor("#FFFFFF") //White
                circleSortKey(0.1)
            })

            it.addLayer(circleLayer(sourceName + "Clayer",sourceName+"C") {
                circleRadius(6.0)
                circleColor(ContextCompat.getColor(requireContext(),R.color.splitComp1Light))
                circleSortKey(20.0)
            })


        }
    }

    fun RemoveAllPointsFromStyle(){
        var x = 1
        while (x >= 0){

            mapView.getMapboxMap().getStyle {
                // Specify a unique string as the source ID (SOURCE_ID)
                // and reference the location of source data
                it.removeStyleLayer(x.toString()+"Clayer")
                it.removeStyleLayer(x.toString()+"BGlayer")
                it.removeStyleSource(x.toString()+ "C")
                x--
            }
        }
    }

    private fun ReadRoutes() {
        val IS : InputStream = resources.openRawResource(R.raw.routecodelookup)
        val scanner = Scanner(IS)
        val inputStream = resources.openRawResource(R.raw.routecodelookup)
        val byteArrayOutputStream = ByteArrayOutputStream()
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
            val jArray = jObjectResult.getJSONArray("Lookup")
            var code = ""
            var transportType = ""
            var distance = ""
            var time = ""
            var health = ""
            var co2 = ""
            var tranquility = ""
            var reliability = ""
            var difficulty = ""

            for (i in 0 until jArray.length()) {
                code = jArray.getJSONObject(i).getString("Route code")
                transportType = jArray.getJSONObject(i).getString("TRANSPORT")
                distance = jArray.getJSONObject(i).getString("Distance")
                time = jArray.getJSONObject(i).getString("Time")
                health = jArray.getJSONObject(i).getString("Health")
                co2 = jArray.getJSONObject(i).getString("CO2 emitted")
                tranquility = jArray.getJSONObject(i).getString("Tranquility")
                reliability = jArray.getJSONObject(i).getString("Time Reliability")
                difficulty = jArray.getJSONObject(i).getString("Difficulty")
                val startName = jArray.getJSONObject(i).getString("FROM")
                val endName = jArray.getJSONObject(i).getString("TO")

                RoutesValueList.add(RouteValue(code,transportType,transportType,distance.toDouble(),time.toDouble(),health.toDouble(),co2.toDouble(),tranquility.toDouble(),reliability.toDouble(),difficulty,startName,endName))
                //data.add(WikiViewModel(category, placeName,tempLocation,link,placeLocation,shortDisc,description,0f))
            }
            //Log.v("wiki", link)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        val updatedState = if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }

    fun ToggleFABMenu()
    {
        //Swich Some Visibliliys

        var navCard : CardView = requireView().findViewById(R.id.navCard)

        viewAnnotationManager.removeAllViewAnnotations()

        var layerVis = mapView.getMapboxMap().getStyle()?.getLayer("Walking-Start-Layer")
        if (fabVisible){
            routesCard.visibility = View.GONE
            navCard.visibility = View.GONE
            clickMode = ClickMode.START
            layerVis?.visibility(Visibility.NONE)
            fab.setImageResource(R.drawable.route_plan)
            //deleteAllPolyLines()
            ToggleLandmarks(showLandmarks, "Heritage-Layer")
            ToggleLandmarks(showLocations, "Locations-Layer")

            fabVisible = false
        }
        else   {
            clickMode = ClickMode.INFO
            fabVisible = true
            ToggleLandmarks(false, "Heritage-Layer")
            ToggleLandmarks(false, "Locations-Layer")
            navCard.visibility = View.VISIBLE
            layerVis?.visibility(Visibility.VISIBLE)
            fab.setImageResource(R.drawable.route_return)

        }
    }

    fun deleteAllPolyLines(){
        polylineAnnotationManagerWalk.deleteAll()
        polylineAnnotationManagerCar.deleteAll()
        polylineAnnotationManagerBike.deleteAll()
        polylineAnnotationManagerBus.deleteAll()
    }


    private fun AddStartNodes(pointAnnotationManager : PointAnnotationManager) {
        //Load Marker
        var nodeMarker = resizeMapIcons(30,30,R.drawable.redcircle)

        //Add points at nodes
        for (point in NodeList){
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                // Define a geographic coordinate.
                .withPoint(fromLngLat(point.longitude, point.latitude))
                .withIconColor("#EC681C")
                .withSymbolSortKey(2.0)
                // Specify the bitmap you assigned to the point annotation
                // The bitmap will be added to map style automatically.
                .withIconImage(nodeMarker!!)
//                .withTextField(point.name)
//                .withTextSize(12.0)



            // Add the resulting pointAnnotation to the map.
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    override fun onMapClick(point: Point): Boolean {
        var routePriority = false
        ClearNavButtons()

        mapboxMap.queryRenderedFeatures(
            RenderedQueryGeometry(mapboxMap.pixelForCoordinate(point)), RenderedQueryOptions(listOf("Walking-Start-Layer"), null)
        ) {
            onFeatureClicked(it) { feature ->


                if (feature.id() != null && fabVisible) {

                    //Do Something
                    var featureName = feature.getStringProperty("Node")
                    Log.v("Click",featureName)

                    val startNode = nodeListRedux.firstOrNull {it.name == featureName}
                    if (startNode != null) {
                        var startPoint: Point = feature.geometry() as Point
                        viewAnnotationManager.removeAllViewAnnotations()
                        //AddAnotation(startPoint,featureName)
                        val locationText : TextView = routesCard.findViewById(R.id.locationName)
                        locationText.text = featureName
                        val locationCoord : TextView = routesCard.findViewById(R.id.locationCoord)
                        locationCoord.text = startPoint.latitude().toFloat().round(2).toString() + ", " + startPoint.longitude().toFloat().round(2).toString()

                        //SetupLocationButtons(routesCard, featureName, startPoint)
                        var featureInfo = feature.getStringProperty("Description")
                        SetUpNavButtons(featureName, startPoint,true,featureInfo,startPoint.latitude().toFloat().round(2).toString() + ", " + startPoint.longitude().toFloat().round(2).toString())
                        SetupLogbook(false,featureName,"Node")



                        routesCard.visibility = View.VISIBLE
                        routePriority = true
                    }

                    if (clickMode == ClickMode.INFO){
                        //Toast.makeText(activity, "Clicked $featureName",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        var layerVis = mapView.getMapboxMap().getStyle()?.getLayer("Start-Layer")
        //if (layerVis?.visibility != Visibility.VISIBLE){
        if (!routePriority) {


            mapboxMap.queryRenderedFeatures(
                RenderedQueryGeometry(mapboxMap.pixelForCoordinate(point)), RenderedQueryOptions(listOf("Heritage-Layer","Locations-Layer"), null)
            ) {
                onFeatureClicked(it) { feature ->
                    if (feature.id() != null) {

//                        if (!fabVisible){
//                            ToggleFABMenu()
//                        }

                        if (routesCard.visibility != View.VISIBLE){
                            routesCard.visibility = View.VISIBLE
                        }

                        if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            ShowSheetConrolers()
                        }

                        //Do Something
                        var featureName = feature.getStringProperty("Name")
                        var featureInfo = feature.getStringProperty("Description")
                        var featureCoords = point.latitude().toFloat().round(2).toString() + ", " + point.longitude().toFloat().round(2).toString()
                        var startPoint: Point = feature.geometry() as Point

                        val locationText : TextView = routesCard.findViewById(R.id.locationName)
                        locationText.text = featureName
                        val locationCoord : TextView = routesCard.findViewById(R.id.locationCoord)
                        locationCoord.text = featureCoords


//                        val startNode = nodeListRedux.firstOrNull {it.name == featureName}
//                        if (startNode != null){
//                            SetUpNavButtons(featureName, startPoint,true)
//                        }
//                        else{
//                            SetUpNavButtons(featureName, startPoint,false)
//                        }

                        SetUpNavButtons(featureName, startPoint,false,featureInfo,featureCoords)
                        SetupLogbook(true,featureName,"location_")

                        routesCard.visibility = View.VISIBLE
                        //ShowInfoPopup(featureName,featureInfo, featureCoords)
                    }
                }
            }

            mapboxMap.queryRenderedFeatures(
                RenderedQueryGeometry(mapboxMap.pixelForCoordinate(point)), RenderedQueryOptions(listOf("Miles_Layer"), null)
            ) {
                onFeatureClicked(it) { feature ->
                    if (feature.id() != null && feature.hasProperty("Text")) {

                        if (routesCard.visibility != View.VISIBLE){
                            routesCard.visibility = View.VISIBLE
                        }

                        if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                            ShowSheetConrolers()
                        }
                        //Do Something
                        var featureName = "MWS " + feature.getStringProperty("Text")
                        var featureLink = feature.getStringProperty("Link")
                        var featureCoords = point.latitude().toFloat().round(2).toString() + ", " + point.longitude().toFloat().round(2).toString()
                        var startPoint: Point = feature.geometry() as Point

                        val locationText : TextView = routesCard.findViewById(R.id.locationName)
                        locationText.text = featureName
                        val locationCoord : TextView = routesCard.findViewById(R.id.locationCoord)
                        locationCoord.text = featureCoords

                        MilesWithoutStiles(startPoint,featureLink)
                        SetupLogbook(true,featureName,"mws_")
                        routesCard.visibility = View.VISIBLE
                    }
                }
            }
        }
        return true
    }

    fun Float.round(decimals: Int): Float {
        var multiplier = 1.0f
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }

    fun SetUpNavButtons(startName: String, startPoint: Point, isStartNode: Boolean, info : String?, location : String){
        val startCard : MaterialCardView = requireView().findViewById(R.id.cardStartFrom)
        val endCard : MaterialCardView = requireView().findViewById(R.id.cardEndAt)
        val zoomCard : MaterialCardView = requireView().findViewById(R.id.cardZoomTo)
        val tripsCard : MaterialCardView = requireView().findViewById(R.id.cardLocationInfo)
        val tripsText : TextView = requireView().findViewById(R.id.infoText)
        val tripsIcon : ImageView = requireView().findViewById(R.id.InfoImage)

        zoomCard.setOnClickListener {
            val cameraPosition = CameraOptions.Builder()
                .center(
                    startPoint
                )
                //.zoom(11.0)
                .build()
            mapView.getMapboxMap().setCamera(cameraPosition)

        }

        if (isStartNode) {
            startCard.visibility = View.VISIBLE
            startCard.setOnClickListener {
                if (::startPointAnotation.isInitialized) {
                    //There can be only one
                    pointAnnotationManager.delete(startPointAnotation)
                }

                navTextStart.text = startName
                routesCard.visibility = View.GONE
                val startIcon = bitmapFromDrawableRes(requireContext(), R.drawable.startpin)
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(startPoint)
                    .withIconImage(startIcon!!)
                    .withIconAnchor(IconAnchor.BOTTOM)
                    .withSymbolSortKey(40.0)

                startPointAnotation = pointAnnotationManager.create(pointAnnotationOptions)
            }
            endCard.visibility = View.VISIBLE
            endCard.setOnClickListener {
                if (::endPointAnotation.isInitialized) {
                    //There can be only one
                    pointAnnotationManager.delete(endPointAnotation)
                }

                navTextEnd.text = startName
                routesCard.visibility = View.GONE
                val startIcon = bitmapFromDrawableRes(requireContext(), R.drawable.endpin)
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(startPoint)
                    .withIconImage(startIcon!!)
                    .withIconAnchor(IconAnchor.BOTTOM)
                    .withSymbolSortKey(40.0)

                endPointAnotation = pointAnnotationManager.create(pointAnnotationOptions)
            }

            tripsIcon.setImageResource(R.drawable.show_routes)
            tripsText.text = "Show Nearby"
            tripsCard.setOnClickListener {
                LoadStartNode(startName)
            }


        }
        else{
            startCard.visibility = View.GONE
            endCard.visibility = View.GONE

            tripsIcon.setImageResource(R.drawable.infoicon)
            tripsText.text = "Info"
            tripsCard.setOnClickListener {
                ShowInfoPopup(startName,info!!, location)
            }


        }

    }

    fun ClearNavButtons(){
        if (routesCard.visibility == View.VISIBLE){
            routesCard.visibility = View.GONE
        }
    }

    fun SetupLogbook(showInfo: Boolean, name: String, type: String){
        val logCard : MaterialCardView = requireView().findViewById(R.id.cardVisitLocation)
        val logText : TextView = requireView().findViewById(R.id.VisitLocationText)
        val logIcon : ImageView = requireView().findViewById(R.id.VisitLocationImage)
        if (showInfo){
            logCard.visibility = View.VISIBLE
            var visited = CheckIfVisited(name,type)
            if (visited){
                logText.text = "Visited"
                logIcon.setImageResource(R.drawable.visit_location)
                logCard.setOnClickListener(){
                    logText.text = "Unvisited"
                    logIcon.setImageResource(R.drawable.unvisit_location)
                    UnvisitLocation(name,type)
                }
            }
            else {
                logText.text = "Unvisited"
                logIcon.setImageResource(R.drawable.unvisit_location)
                logCard.setOnClickListener(){
                    logText.text = "Visited"
                    logIcon.setImageResource(R.drawable.visit_location)
                    VisitLocation(name,type)
                    getLocation()
                    val locationInfo = myLocation.split("_")
                    SendVisitToDatabase(name,type,locationInfo[0],locationInfo[1])
                    }
                }
            }
        else{
            logCard.visibility = View.GONE
        }
    }

    fun MilesWithoutStiles(startPoint: Point,  link : String){
        val startCard : MaterialCardView = requireView().findViewById(R.id.cardStartFrom)
        val endCard : MaterialCardView = requireView().findViewById(R.id.cardEndAt)
        val zoomCard : MaterialCardView = requireView().findViewById(R.id.cardZoomTo)
        val tripsCard : MaterialCardView = requireView().findViewById(R.id.cardLocationInfo)
        val tripsText : TextView = requireView().findViewById(R.id.infoText)
        val tripsIcon : ImageView = requireView().findViewById(R.id.InfoImage)

        zoomCard.setOnClickListener {
            val cameraPosition = CameraOptions.Builder()
                .center(
                    startPoint
                )
                //.zoom(11.0)
                .build()
            mapView.getMapboxMap().setCamera(cameraPosition)
        }


            startCard.visibility = View.GONE
            endCard.visibility = View.GONE

            tripsIcon.setImageResource(R.drawable.ic_icon_link)
            tripsText.text = "Link"
            tripsCard.setOnClickListener {
                val uri: Uri =
                    Uri.parse(link) // missing 'http://' will cause crashed

                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }




    }




    private fun SetupLocationButtons(view: View, startName: String, startPoint: Point) {
        val showOnMapButton : MaterialCardView = view.findViewById(R.id.cardStartFrom)
        val showConnectionsButton : MaterialCardView = view.findViewById(R.id.cardEndAt)
        showConnectionsButton.setOnClickListener {
            //LoadStartNode(startName)
            viewAnnotationManager.removeAllViewAnnotations()
            AddGenericAnnotation(R.layout.annotation_text,"Start",startPoint)
        }
    }

    private fun onFeatureClicked(
        expected: Expected<String, List<QueriedFeature>>,
        onFeatureClicked: (Feature) -> Unit
    ) {
        if (expected.isValue && expected.value?.size!! > 0) {
            expected.value?.get(0)?.feature?.let { feature ->
                onFeatureClicked.invoke(feature)
            }
        }
    }

    fun LoadStartNode(name: String){
        //Find our node in the Graph
        val startNode = nodeListRedux.firstOrNull {it.name == name}
        Log.v("Connections", name)

        if (startNode != null){
            var startPoint : Point = fromLngLat(startNode.longitude,startNode.latitude)

            //Find the neighbors
            val neighbors = Graphs.neighborListOf(mapGraphRedux,startNode)
            val neighborNames = neighbors.map { it.name }

            Log.v("Json",  startNode.name + " has " + neighborNames )

            val neighborPoints = ArrayList<Point>()
            for (neighbor in neighbors){
                neighborPoints.add(fromLngLat(neighbor.longitude,neighbor.latitude))
            }

            ShowConnections(name,neighborNames)



            //BuildConectionReclerView(startNode.name,neighborNames)
            //ShowConnectionSheet()

        }
    }

    fun BuildConectionReclerView(startName: String, neighborNames: List<String>){
        val data = ArrayList<ConnectionViewModel>()

        for (endName in neighborNames){
               data.add(ConnectionViewModel(startName,endName))
        }
        val adapter = ConnectionEntryAdapter(data)
        adapter.setOnItemClickListener(object : ConnectionEntryAdapter.onItemClickListner {
            override fun chooseConnectoion(startLocation: String, endLocation: String) {
                //ShowRoutesBetween(startLocation, endLocation)
            }
        })

        recyclerViewConections.adapter = adapter
        recyclerViewConections.layoutManager = LinearLayoutManager(activity)
    }

//    fun ShowRoutesBetween(startName: String, endName: String){
//        //Get the conection between current click and markerID
//        var conections = ConnectionVerboses.filter { it.startName == startName} + ConnectionVerboses.filter { it.endName == startName}
//        val conectionDetails : ConnectionDetails?
//        if (conections.firstOrNull { it.startName == endName} != null){
//            val myConection =  conections.firstOrNull { it.startName == endName}
//            conectionDetails = myConection?.connection?.details
//        }
//        else{
//            val myConection =  conections.firstOrNull { it.endName == endName}
//            conectionDetails = myConection?.connection?.details
//        }
//        ShowRoutes(conectionDetails!!)
//
//        //Deleta all other anotations
//        deleteAllPolyLines()
//        viewAnnotationManager.removeAllViewAnnotations()
//        pointAnnotationManager.deleteAll()
//
//        //Toggle the Fab menu
//        ToggleFABMenu()
//    }

    fun ShowConnections(startPoint: String, neighborPoints : List<String>){
        deleteAllPolyLines()
        val routes = ArrayList<RouteValue>()
        for (connection in neighborPoints){
            val myRoute = RouteToCode(startPoint,connection)
            if (myRoute != null){
                Log.v("Connections", myRoute.title)
                routes.add(myRoute)
            }
        }

        ShowTraversalOnMap(routes,false)
    }

    fun LoadAllGPX()
    {
        Log.v("Msg","loadingGPXS")
        val bigList  = ArrayList<Point>()
        val uniqeList = ArrayList<String>()
        //Go through each non-null route and add them to a list
        for (e in routeListRedux){
            uniqeList.add(e.id)
        }
        for (e in routeListBus){
            uniqeList.add(e.id)
        }
        val parser = GPXParser() // consider injection
        val am : AssetManager = resources.assets
        var elementCountIterator = 0
        for (element in uniqeList){
            elementCountIterator+=1
            Log.v("Msg",element)
            try {
                val input: InputStream = requireContext().assets.open("GPXs/$element" +".gpx")
                val parsedGpx: Gpx? = parser.parse(input) // consider using a background thread
                parsedGpx?.let {
                    // do something with the parsed track
                    var points = ArrayList<Point>()
                    val tracks = parsedGpx.tracks
                    for (track in tracks){
                        for (segments in track.trackSegments){
                            for (point in segments.trackPoints){
                                val lat = point.latitude
                                val lng = point.longitude
                                val position = Point.fromLngLat(lng, lat)
                                points.add(position)

                            }
                        }
                    }
                    bigList += points
                    GPXList.add(CodeToGPX(element,points,false))
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
    }


    fun BuildRouteReceycleView2(routeList: ArrayList<RouteValue>){
        //Use the name of routes and the route ID to look up stuff...?

        val data = routeList


        val adapter = RouteEntryAdapter(data,false)
        adapter.setOnItemClickListener(object : RouteEntryAdapter.onItemClickListner {
            override fun chooseRoute(string: String) {
                val splitString = string.split("_")
                val transportType = splitString[0].lowercase()
                Log.v("Highlight", transportType)

                highLightTransportType(transportType)
            }

            override fun removeme() {

            }

            override fun downloadRoute(string: String) {
                DownloadPermsions()
            }

            override fun completeRoute(myDetails: RouteValue) {
                AskToCompleatRoute(myDetails)
            }

            override fun rateRoute(myDetails: RouteValue) {
                getLocation()
                val locationInfo = myLocation.split("_")
                RateRoute(myDetails, locationInfo[0],locationInfo[1])
            }

            override fun fillSurvey() {
                val action = MapBoxFragmentDirections.actionMapBoxFragmentToSurveyFragment()
                view!!.findNavController().navigate(action)
            }

        })
//        adapter.setOnItemClickListener(object  : JourneyEntryAdapter.onItemClickListner{
//        })
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    fun highLightTransportType(transportType : String){
        when (transportType){
            "walk" -> highlightRoute(polylineAnnotationManagerWalk)
            "bus" -> highlightRoute(polylineAnnotationManagerBus)
            "cycling" -> highlightRoute(polylineAnnotationManagerBike)
            "driving" -> highlightRoute(polylineAnnotationManagerCar)
            "bike" -> highlightRoute(polylineAnnotationManagerBike)
            "car" -> highlightRoute(polylineAnnotationManagerCar)
            else -> Log.v("Highlight", "???")
        }
    }

    private fun LookUpCode(codein : String, transportType: String): RouteValue? {
        var codeList = RoutesValueList.filter { route -> route.id == codein}

        var myRoute = codeList.firstOrNull{
            it.transportType.lowercase() == transportType.lowercase()
        }

        if (myRoute == null){
            Log.v("lookUp", codeList.toString())
            Log.v("lookUp", transportType)
        }
        return myRoute
    }

    fun ShowBottomSheet(){
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        standardBottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        standardBottomSheetConnectionsBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    ShowSheetConrolers()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset
            }
        }
        standardBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    fun ShowConnectionSheet(){
        standardBottomSheetConnectionsBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        standardBottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        standardBottomSheetConnectionsBehavior.isHideable = false

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ShowSheetConrolers()

                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset
            }
        }
        standardBottomSheetConnectionsBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    fun ShowInfoSheet(title : String,info : String, coords: String){
        //standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        standardBottomSheetConnectionsBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        infoSheetTitle.text = title
        infoSheetContent.text = info


        standardBottomSheetInfoBehavior.halfExpandedRatio = 0.35f
        standardBottomSheetInfoBehavior.isFitToContents = true
        standardBottomSheetInfoBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    //handleUserExit()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset
            }
        }
        standardBottomSheetInfoBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    fun ShowInfoPopup(title : String,info : String, coords: String){

        var viewToShow = layoutInflater.inflate( R.layout.location_info_popup, null)
        val popUpText : TextView = viewToShow.findViewById(R.id.popupText)
        val popUpName : TextView = viewToShow.findViewById(R.id.popupName)
        val popUpCoord : TextView = viewToShow.findViewById(R.id.popupCoords)

        popUpText.text = info
        popUpName.text = title
        popUpCoord.text = coords

        var newDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(viewToShow)

            .setNeutralButton("Close") { dialog, which ->

            }
            .show()
    }

    private fun handleUserExit() {
        deleteAllPolyLines()
        RemoveAllLinesFromStyle()
        RemoveAllPointsFromStyle()
        viewAnnotationManager.removeAllViewAnnotations()
        clickMode = ClickMode.START
        currentClick = ""
        polylineAnnotationManager.deleteAll()
        pinAnnotationManager.deleteAll()
        if (this::startPointAnotation.isInitialized)
            pointAnnotationManager.delete(startPointAnotation)
        if (this::endPointAnotation.isInitialized)
            pointAnnotationManager.delete(endPointAnotation)
        //Other stuff?
    }

    private fun ShowSheetConrolers() {
        sheetControllers.visibility = View.VISIBLE
        Log.v("Sheets",sheetControllers.visibility.toString())
        val closeButton : CardView = sheetControllers.findViewById(R.id.CloseSheetCard)
        closeButton.setOnClickListener {
            handleUserExit()
            sheetControllers.visibility = View.GONE
        }
        val reopenButton : CardView = sheetControllers.findViewById(R.id.ReopenSheetCard)
        reopenButton.setOnClickListener {
            ShowBottomSheet()
            sheetControllers.visibility = View.GONE
        }
    }

    fun GenrateRouteList(conectionDetails: ConnectionDetails ): ArrayList<String> {
        val list = ArrayList<String>()
        if (conectionDetails.Walk != null)
            list.add(conectionDetails.Walk)
        if (conectionDetails.Bike != null && !list.contains(conectionDetails.Bike))
            list.add(conectionDetails.Bike)
        if (conectionDetails.Car != null && !list.contains(conectionDetails.Car))
            list.add(conectionDetails.Car)
        if (conectionDetails.Ferry != null && !list.contains(conectionDetails.Ferry))
            list.add(conectionDetails.Ferry)
        if (conectionDetails.Bus != null && !list.contains(conectionDetails.Bus))
            list.add(conectionDetails.Bus)
        if (conectionDetails.Train != null && !list.contains(conectionDetails.Train))
            list.add(conectionDetails.Train)

        Log.v("Msg",list.toString())

        return list
    }

    fun GenrateIndepentantRouteList(conectionDetails: ConnectionDetails ): ArrayList<RouteLookup> {
        val list = ArrayList<RouteLookup>()
        if (conectionDetails.Walk != null)
            list.add(RouteLookup( conectionDetails.Walk, "Walk"))
        if (conectionDetails.Bike != null)
            list.add(RouteLookup( conectionDetails.Bike, "Bike"))
        if (conectionDetails.Car != null)
            list.add(RouteLookup( conectionDetails.Car, "Car"))
        if (conectionDetails.Ferry != null)
            list.add(RouteLookup( conectionDetails.Ferry, "Ferry"))
        if (conectionDetails.Bus != null)
            list.add(RouteLookup( conectionDetails.Bus, "Bus"))
        if (conectionDetails.Train != null)
            list.add(RouteLookup( conectionDetails.Train, "Train"))

        Log.v("Msg",list.toString())

        return list
    }




    fun AddAnotation(point: Point, markerId: String){
        //viewAnnotationManager.removeAllViewAnnotations()

        if (currentClick == markerId){
            //Just get rid of yourself?

        }
        else {
            viewAnnotationManager.addViewAnnotation(
                resId = R.layout.annotation_button,
                options = viewAnnotationOptions {
                    geometry(point)

                    //associatedFeatureId(markerId)
                    anchor(ViewAnnotationAnchor.BOTTOM)
                    allowOverlap(false)
                },
                asyncInflater = asyncInflater
            ) { viewAnnotation ->
                //viewAnnotation.visibility = View.GONE
                // calculate offsetY manually taking into account icon height only because of bottom anchoring
                viewAnnotationManager.updateViewAnnotation(
                    viewAnnotation,
                    viewAnnotationOptions {
                        offsetY(markerHeight)
                    }
                )
                viewAnnotation.findViewById<TextView>(R.id.annotationButtonName).text =
                    markerId
//            viewAnnotation.findViewById<ImageView>(R.id.closeNativeView).setOnClickListener { _ ->
//                viewAnnotationManager.removeViewAnnotation(viewAnnotation)
//            }

                //3 Cases for Click: Clicked same, clicked other and clicked linked
                var startButton = viewAnnotation.findViewById<Button>(R.id.annoationButtonStart)
                var endButton = viewAnnotation.findViewById<Button>(R.id.annoationButtonEnd)
                startButton.setOnClickListener { b ->
                    val button = b as Button
                    clickMode = ClickMode.END
                    //LoadStartNode(markerId)
                    viewAnnotationManager.removeAllViewAnnotations()
                    AddGenericAnnotation(R.layout.annotation_text,"Start",point)

                }

                if (clickMode == ClickMode.END) {
                    if (currentConnections.contains(markerId)) {
                        //Show swtich and End here
                        startButton.text = "Switch"
                        endButton.visibility = View.VISIBLE
                        endButton.setOnClickListener { b ->
                            val button = b as Button
                            //TODO call some method to handle loading routes
                            clickMode = ClickMode.ROUTE
                            //Get the conection between current click and markerID
                            var conections = ConnectionVerboses.filter { it.startName == currentClick} + ConnectionVerboses.filter { it.endName == currentClick}
                            val conectionDetails : ConnectionDetails?
                            if (conections.firstOrNull { it.startName == markerId} != null){
                                val myConection =  conections.firstOrNull { it.startName == markerId}
                                conectionDetails = myConection?.connection?.details
                            }
                            else{
                                val myConection =  conections.firstOrNull { it.endName == markerId}
                                conectionDetails = myConection?.connection?.details
                            }
                            //ShowRoutes(conectionDetails!!)

                            //Deleta all other anotations
                            //deleteAllPolyLines()
                            viewAnnotationManager.removeAllViewAnnotations()
                            pointAnnotationManager.deleteAll()

                            //Toggle the Fab menu
                            ToggleFABMenu()
                        }
                    }
                    else {
                        //Only show Switch
                        startButton.text = "Switch"

                    }
                }
                else { //Just show start
                }
            }
        }
    }

    fun AddGenericAnnotation(id : Int, string: String,point: Point){
        viewAnnotationManager.addViewAnnotation(
            resId = id,
            options = viewAnnotationOptions {
                geometry(point)
                //associatedFeatureId(markerId)
                anchor(ViewAnnotationAnchor.TOP)
                allowOverlap(true)
            },
            asyncInflater = asyncInflater
        ) { viewAnnotation ->

            // calculate offsetY manually taking into account icon height only because of bottom anchoring
            viewAnnotationManager.updateViewAnnotation(
                viewAnnotation,
                viewAnnotationOptions {
                    offsetY(markerHeight)
                }
            )
            viewAnnotation.findViewById<TextView>(R.id.annotationTextView).text =
                string
        }
    }



    fun ReadBespokeGraph(){
        //Setup the Json Reader
        val nameListToFindSolo = ArrayList<String>()
        val inputStream = resources.openRawResource(R.raw.network)
        val byteArrayOutputStream = ByteArrayOutputStream()
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

        try {
            // Parse the data into jsonobject to get original data in form of json.
            val jObject = JSONObject(
                byteArrayOutputStream.toString()
            )

            //Read Nodes
            val jArray = jObject.getJSONArray("Nodes")
            var placeName = ""
            var latitude = 0.0
            var longitude = 0.0

            //Read each start node
            for (i in 0 until jArray.length()) {
                placeName = jArray.getJSONObject(i).getString("Node")
                latitude = jArray.getJSONObject(i).getDouble("Latitude")
                longitude = jArray.getJSONObject(i).getDouble("Longitude")

                Log.v("Place", placeName)

                //Add nodes to graph
                val mapNode = MapNode(placeName, latitude, longitude)
                mapGraphRedux.addVertex(mapNode)
                mapGraphBus.addVertex(mapNode)
                nodeListRedux.add(mapNode)
                nameListToFindSolo.add(placeName)
            }


            //Read Conections
            val jArrayC = jObject.getJSONArray("Walk Conections")
            var start = ""
            var end = ""

            for (i in 0 until jArrayC.length()) {
                start = jArrayC.getJSONObject(i).getString("Start")
                end = jArrayC.getJSONObject(i).getString("End")
                val startNode: MapNode? = nodeListRedux.firstOrNull {it.name == start}
                val endNode: MapNode? = nodeListRedux.firstOrNull {it.name == end}

                if (startNode != null && endNode != null){
                    var routeDetails = ReadRouteDetails(jArrayC.getJSONObject(i),"Walk")
                    routeListRedux.add(routeDetails)
                    var newEdge = mapGraphRedux.addEdge(startNode,endNode)
                    mapGraphRedux.setEdgeWeight(newEdge,routeDetails.distance.toDouble())
                    }
                else{
                    Log.v("Json", "Error Between "+ start + " and " + end)
                }
            }

            //Read The Bus Network
            val jArrayB = jObject.getJSONArray("Bus Connections")
            var startB = ""
            var endB = ""

            for (i in 0 until jArrayB.length()) {
                startB = jArrayB.getJSONObject(i).getString("Start")
                endB = jArrayB.getJSONObject(i).getString("End")
                val startNode: MapNode? = nodeListRedux.firstOrNull {it.name == startB}
                val endNode: MapNode? = nodeListRedux.firstOrNull {it.name == endB}

                if (startNode != null && endNode != null){
                    var routeDetailsBus = ReadBusDetails(jArrayB.getJSONObject(i))
                    routeListBus.add(routeDetailsBus)
                    var newEdge = mapGraphBus.addEdge(startNode,endNode)
                    mapGraphBus.setEdgeWeight(newEdge,routeDetailsBus.time.toDouble())
                    Log.v("Json", "Bus Between "+ startB + " and " + endB)
                }
                else{
                    Log.v("Json", "Error Between "+ startB + " and " + endB)
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun TraverseMap (startName: String, endName: String){
        RemoveAllTraversal()
        var startNode = nodeListRedux.firstOrNull {it.name == startName}
        var endNode = nodeListRedux.firstOrNull {it.name == endName}
        var heuristic = LakesDistanceEst()
        val astar = AStarShortestPath(mapGraphRedux,heuristic)
        val path = astar.getPath(startNode,endNode)
        Log.v("Path", path.toString())
        var combinedConetions = CombineConections(path.vertexList)
        val combinedRouteValue = SumConectioncDetails(combinedConetions, startName,endName, "Walk" )
        dispayRoutes.add(combinedRouteValue)
        ShowTraversalOnMap(combinedConetions,true)
    }

    fun TraverseBus (startName: String, endName: String){
        var startNode = nodeListRedux.firstOrNull {it.name == startName}
        var endNode = nodeListRedux.firstOrNull {it.name == endName}
        var heuristic = LakesDistanceEst()
        val astar = AStarShortestPath(mapGraphBus,heuristic)
        val path = astar.getPath(startNode,endNode)

        if (path != null){
            Log.v("Bus", path.toString())
            var combinedConetions = CombineBusConections(path.vertexList)
            val combinedRouteValue = SumBusDetails(combinedConetions, startName,endName, "Bus" )
            dispayRoutes.add(combinedRouteValue)

            ShowBusOnMap(combinedConetions)
        }

    }

    private fun SumConectioncDetails(list: ArrayList<RouteValue>, startName: String, endName: String, transportType: String) : RouteValue {
        val routeID = "$startName to $endName"
        var title = transportType
        Log.v("connection", list.count().toString())
        if (list.count() == 1){
            title = list[0].title
        }
        var distance = 0.0
        var time = 0.0
        var difficulty = "easy"
        var co2 = 0.0
        var tranquillity = 0.0
        var reliability = 0.0
        var health = 0.0

        for (e in list){
            distance += e.distance.toDouble()
            time += e.time.toDouble()
            co2 += e.Co2

            if (e.difficulty == "challenging"){
                difficulty = "challenging"
            }
            else if (e.difficulty == "difficult" && difficulty != "challenging")
            {
                difficulty = "difficult"
            }
            else if (e.difficulty == "moderate" && difficulty != "challenging" && difficulty != "difficult")
            {
                difficulty = "moderate"
            }
            health += e.health.toFloat()
            tranquillity += e.tranquility
            reliability += e.reliability
        }

        tranquillity /= list.count()
        reliability /= list.count()

        return RouteValue(routeID,title,transportType,distance,time,health,co2,tranquillity.roundToInt().toDouble(),reliability.roundToInt().toDouble(),difficulty,startName,endName)
    }

    private fun SumBusDetails(list: ArrayList<RouteValue>, startName: String, endName: String, transportType: String) : RouteValue {
        val routeID = "$startName to $endName"
        var title = transportType
        Log.v("connection", list.count().toString())
        var titleList = ArrayList<String>()
        var distance = 0.0
        var time = 0.0
        var difficulty = "Easy"
        var co2 = 0.0
        var tranquillity = 2.0
        var reliability = 4.0
        var health = 0.0

        for (e in list){
            distance += e.distance.toDouble()
            time += e.time.toDouble()
            co2 += e.CoSave
            if (!titleList.contains(e.title)){
                titleList.add(e.title)
                title += ", "+ e.title
            }
        }


        return RouteValue(routeID,title,transportType,distance,time,health,co2,tranquillity.roundToInt().toDouble(),reliability.roundToInt().toDouble(),difficulty,startName,endName)
    }

    fun ShowTraversalOnMap(list: ArrayList<RouteValue>, showInfo: Boolean){
        val bigList = ArrayList<Point>()
        val walkColours = requireContext().resources.getIntArray(R.array.walkColours)
        var colourIterator = 0

        for (e in list){
            //show element on the map
            val myGPX = GPXList.firstOrNull {it.id == e.id}
            if (myGPX != null){
                var pointList = myGPX.points
                if (e.inverted)
                    pointList.reverse()

                bigList += (pointList)
                //AddLineToStyle(pointList,e.id)

                val lineColour = walkColours[colourIterator]
                colourIterator++
                if (colourIterator > 4){
                    colourIterator = 0
                }


                val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                    .withPoints(pointList)
                    // Style the line that will be added to the map.
                    .withLineColor(lineColour)
                    .withLineWidth(5.0)
                    .withLineSortKey(1.0)

                val annotation = polylineAnnotationManagerWalk.create(polylineAnnotationOptions)
                //val onPolyClick = OnPolylineAnnotationClickListener(annotation)
            }
        }
        // Convert to a camera options to fit all neighbors and padding
        val cameraZoomOption : CameraOptions = CameraOptions.Builder()
            .zoom(11.0)
            .build()

        val cameraPosition = mapboxMap.cameraForCoordinates(bigList,EdgeInsets(100.0, 75.0, 850.0, 75.0),null,null)
        // Set camera position
        mapboxMap.setCamera(cameraPosition)
        if (mapboxMap.cameraState.zoom > 11)
            mapboxMap.setCamera(cameraZoomOption)

        if (showInfo){
            //PlaceRouteID(bigList[bigList.size * 1/2] , "Walk" ,"")
            CheckRouteForCoolStuff(bigList)
            ToggleFABMenu()
        }

    }

    fun ShowBusOnMap(list: ArrayList<RouteValue>){
        val bigList = ArrayList<Point>()

        for (e in list){
            //show element on the map
            val myGPX = GPXList.firstOrNull {it.id == e.id}
            if (myGPX != null){
                var pointList = myGPX.points
                if (e.inverted)
                    pointList.reverse()

                bigList += (pointList)
                //AddLineToStyle(pointList,e.id)

                val lineColour = e.hexColour
                val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                    .withPoints(pointList)
                    // Style the line that will be added to the map.
                    .withLineColor(lineColour)
                    .withLineWidth(5.0)
                    .withLineSortKey(1.0)
                    .withLineOpacity(0.2)

                val annotation = polylineAnnotationManagerBus.create(polylineAnnotationOptions)

                //val onPolyClick = OnPolylineAnnotationClickListener(annotation)
                //PlaceRouteID(pointList[pointList.size * 1/2] , "Bus" , e.title )
            }
        }
        // Convert to a camera options to fit all neighbors and padding
        val cameraZoomOption : CameraOptions = CameraOptions.Builder()
            .zoom(11.0)
            .build()

        val cameraPosition = mapboxMap.cameraForCoordinates(bigList,EdgeInsets(100.0, 75.0, 850.0, 75.0),null,null)
        // Set camera position
        mapboxMap.setCamera(cameraPosition)
        if (mapboxMap.cameraState.zoom > 11)
            mapboxMap.setCamera(cameraZoomOption)



    }



    fun RemoveAllTraversal(){
        for (e in routeListRedux){
            RemoveLineFromStyle(e.id)
        }
    }


    fun CombineConections(nodes : List<MapNode>) : ArrayList<RouteValue>{
        var i = 0
        var routeArray = ArrayList<RouteValue>()
        walkFileList = ArrayList<String>()
        while (i < nodes.count()-1){
            var myRoute  : RouteValue? = routeListRedux.firstOrNull { it.startName == nodes[i].name && it.endName == nodes [i+1].name  }
            if (myRoute == null){
                myRoute = routeListRedux.firstOrNull { it.endName == nodes[i].name && it.startName == nodes [i+1].name  }
                myRoute!!.inverted = true
            }
            if (myRoute != null){
                routeArray.add(myRoute)
                walkFileList.add(myRoute.id)
            }
            i++
        }
        Log.v("Traverse", routeArray.toString())
        return routeArray
    }

    fun RouteToCode(startName: String , endName: String ) : RouteValue? {
        var routeArray : RouteValue
            var myRoute  : RouteValue? = routeListRedux.firstOrNull { it.startName == startName && it.endName == endName  }
            if (myRoute == null){
                myRoute = routeListRedux.firstOrNull { it.endName == startName && it.startName == endName  }
                myRoute!!.inverted = true
            }
            if (myRoute != null){
                return myRoute
            }
        return null
    }

    fun CombineBusConections(nodes : List<MapNode>) : ArrayList<RouteValue>{
        var i = 0
        var routeArray = ArrayList<RouteValue>()

        while (i < nodes.count()-1){
            var myRoute  : RouteValue? = routeListBus.firstOrNull { it.startName == nodes[i].name && it.endName == nodes [i+1].name  }
            if (myRoute == null){
                myRoute = routeListBus.firstOrNull { it.endName == nodes[i].name && it.startName == nodes [i+1].name  }
                myRoute!!.inverted = true
            }
            if (myRoute != null){
                routeArray.add(myRoute)

            }
            i++
        }
        Log.v("Traverse", routeArray.toString())
        return routeArray
    }



    fun DownloadPermsions(){


        when {requireContext().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED -> {SaveWalkGPX()}
            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission_group.STORAGE)
                     -> {
                         //Show Rational
                Log.v("Permsions","Show Rational")
                         reqestPermissions.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                     }
            else -> {
                //Not Requested
                Log.v("Permsions","No Responce")
                reqestPermissions.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }




    @SuppressLint("MissingPermission")
    fun SaveWalkGPX(){
        var root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        //if you want to create a sub-dir
        var folder = File(root, "Lake_District_GPX")
        folder.mkdir()

        for (walk in walkFileList){

            val input: InputStream = requireContext().assets.open("GPXs/$walk" +".gpx")
            Log.v("SaveLoad",input.toString())




            // select the name for your file
            root = File(root, walk + ".gpx")


            Log.v("SaveLoad", "Trying to Save : " + walk )
            Log.v("SaveLoad", "Trying to Save to : " + root )

            try {
                val fileOutputStream = FileOutputStream(root)
//                val buf = ByteArray(1024)
//                var len: Int
//                while (input.read(buf).also { len = it } > 0) {
//                    fileOutputStream.write(buf, 0, len)
//                }
//                fileOutputStream.close()
//                input.close()
                fileOutputStream.write(input.readBytes())
                fileOutputStream.close()




            } catch (e: FileNotFoundException) {
                Log.v("SaveLoad", e.toString() )
            }
        }

        val intent = Intent()
        intent.action = Intent.ACTION_OPEN_DOCUMENT

        intent.data = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider",root)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.v("SaveLoad"," Saved to: "+ intent.data.toString())



        val pIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_MUTABLE)




        val builder = NotificationCompat.Builder(requireContext(), "Lakes Channel ID")
            .setSmallIcon(R.drawable.walk)
            .setContentTitle("Saved "+ walkFileList.count() + " GPX files to Downloads folder")
            .setContentText("Press to explore all saved walks")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pIntent)
            .setAutoCancel(true)

        Log.v("SaveLoad", "Trying to notify : " + builder.toString() )

        with(NotificationManagerCompat.from(requireContext())) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lakes Notification"
            val descriptionText = "Notifications from Lake District planning app"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Lakes Channel ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }




    class LakesDistanceEst : AStarAdmissibleHeuristic<MapNode> {
        override fun getCostEstimate(sourceVertex: MapNode, targetVertex: MapNode): Double {
            val location1 = Location("")
            location1.longitude = sourceVertex.longitude ; location1.latitude = sourceVertex.latitude
            val location2 = Location("")
            location2.longitude = targetVertex.longitude ; location2.latitude = targetVertex.latitude
            return location1.distanceTo(location2).toDouble()
        }

        override fun <E> isConsistent(graph: Graph<MapNode, E>): Boolean {
            return true
        }
    }


    fun ReadRouteDetails(obj: JSONObject, transportType: String): RouteValue {
        val routeID = obj.getString("Walk ID")
        val title = obj.getString("Title")
        val distance = obj.getDouble("Distance")
        val time = obj.getDouble("Time") + (obj.getDouble("Minutes") / 100)
        val difficulty = obj.getDouble("Difficulty")
        val co2 = obj.getString("Co2")
        val tranquillity = obj.getString("Tranquillity")
        val reliability = obj.getString("Reliability")
        val health = obj.getString("Health")
        val startName = obj.getString("Start")
        val endName = obj.getString("End")
        val myRoute = RouteValue(routeID,title,transportType,distance,time,health.toDouble(),co2.toDouble(),tranquillity.toDouble(),reliability.toDouble(),DifficultySwitch(difficulty.toInt()),startName,endName)
        myRoute.CoSave = distance * 0.15
        return myRoute
    }

    fun ReadBusDetails(obj: JSONObject): RouteValue {
        val routeID = obj.getString("ID")
        val colour = obj.getString("Hex")
        val title = obj.getString("Title")
        val distance = obj.getDouble("Distance")
        val time = obj.getDouble("Time") + (obj.getDouble("Minutes") / 100)
        val difficulty = "Easy"
        val co2 = distance * 0.06
        val tranquillity = 2
        val reliability = 4
        val health = 0
        val startName = obj.getString("Start")
        val endName = obj.getString("End")
        val CoSave = distance * 0.9
        return RouteValue(routeID,title,"Bus",distance,time,health.toDouble(),co2.toDouble(),tranquillity.toDouble(),reliability.toDouble(),difficulty,startName,endName,false,false,colour,CoSave)

    }

    fun DifficultySwitch (difficulty : Int) : String{
        var myDifficulty = ""
        when (difficulty) {
            1 -> myDifficulty = "easy"
            2 -> myDifficulty = "moderate"
            3 -> myDifficulty = "difficult"
            4 -> myDifficulty = "challenging"
            else -> { // Note the block
                myDifficulty = "unknown"
            }
        }
        return  myDifficulty
    }


    private fun ReadGraph() {
//        val IS : InputStream = resources.openRawResource(R.raw.heritage)
//        val scanner = Scanner(IS)
        val nameListToFindSolo = ArrayList<String>()
        val inputStream = resources.openRawResource(R.raw.graph)
        val byteArrayOutputStream = ByteArrayOutputStream()
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

            //Read Nodes
            val jArray = jObjectResult.getJSONArray("Nodes")
            var placeName = ""
            var latitude = 0.0
            var longitude = 0.0


            for (i in 0 until jArray.length()) {
                placeName = jArray.getJSONObject(i).getString("Name")
                latitude = jArray.getJSONObject(i).getDouble("Latitude")
                longitude = jArray.getJSONObject(i).getDouble("Longitude")

                Log.v("Place", placeName)

                //Add nodes to graph
                val mapNode = MapNode(placeName, latitude, longitude)
                mapGraph.addVertex(mapNode)
                NodeList.add(mapNode)
                nameListToFindSolo.add(placeName)
            }


            //Read Conections
            val jArrayC = jObjectResult.getJSONArray("Conections")
            var start = ""
            var end = ""
            var usable : Boolean = false

            for (i in 0 until jArrayC.length()) {
                start = jArrayC.getJSONObject(i).getString("Start")
                end = jArrayC.getJSONObject(i).getString("Finish")
                if (jArrayC.getJSONObject(i).getBoolean("Status"))
                    usable = true

                val startNode: MapNode? = NodeList.firstOrNull {it.name == start}
                val endNode: MapNode? = NodeList.firstOrNull {it.name == end}

                if (startNode != null && endNode != null){
                    mapGraph.addEdge(startNode,endNode)

                    var connectionDetails : ConnectionDetails? = null
                    if (usable){
                        connectionDetails = ReadConnectionDetails(jArrayC.getJSONObject(i))
                        Log.v("Msg",connectionDetails.toString())
                    }

                    if (nameListToFindSolo.contains(startNode.name)){
                        nameListToFindSolo.remove(startNode.name)
                    }
                    if (nameListToFindSolo.contains(endNode.name)){
                        nameListToFindSolo.remove(endNode.name)
                    }



                    val connection = Connection(Point.fromLngLat(startNode.longitude,startNode.latitude),Point.fromLngLat(endNode.longitude,endNode.latitude),usable,connectionDetails)
                    Connections.add(connection)
                    ConnectionVerboses.add(ConnectionVerbose(start,end,connection))
                }
                else{
                    Log.v("Json", "Error Between "+ start + " and " + end)
                }




            }
            //Log Windamere To prove something
            val windamere = NodeList.firstOrNull {it.name == "Rydal"}
            val neighbors = Graphs.neighborListOf(mapGraph,windamere)
            val neighborNames = neighbors.map { it.name }
            Log.v("Json", "Windermere has " + neighborNames )
            Log.v("Json", "Solo Icons are: " + nameListToFindSolo )


        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun ReadConnectionDetails(obj: JSONObject): ConnectionDetails {

        var walk : String?= null
        var car : String?= null
        var bike : String?= null
        var bus : String?= null
        var ferry : String?= null
        var train : String?= null
        if (obj.getString("Walk Route") != "no")
            walk = obj.getString("Walk Route")
        if (obj.getString("Car Route") != "no")
            car = obj.getString("Car Route")
        if (obj.getString("Bike Route") != "no")
            bike = obj.getString("Bike Route")
        if (obj.getString("Bus Route") != "no")
            bus = obj.getString("Bus Route")
        if (obj.getString("Ferry Route") != "no")
            ferry = obj.getString("Ferry Route")
        if (obj.getString("Train Route") != "no")
            train = obj.getString("Train Route")

        return ConnectionDetails(walk,car,bike,bus,ferry,train)
    }

    private fun AddGraphVisulisation() {

        //Load Marker
        var nodeMarker = resizeMapIcons(40,40,R.drawable.redcircle)


        //Throw down the lines
        for (connections in Connections){
            val points = listOf(
                connections.startPoint,
                connections.endPoint
            )
            // Set options for the resulting line layer.
                val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                .withPoints(points)
                // Style the line that will be added to the map.
                .withLineColor("#ee4e8b")
                .withLineWidth(5.0)
                    .withLineSortKey(1.0)

            // Add the resulting line to the map.
            polylineAnnotationManager.create(polylineAnnotationOptions)

        }

        //Add points at nodes
        for (point in NodeList){
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                // Define a geographic coordinate.
                .withPoint(fromLngLat(point.longitude, point.latitude))
                .withIconColor("#EC681C")
                .withSymbolSortKey(2.0)
                // Specify the bitmap you assigned to the point annotation
                // The bitmap will be added to map style automatically.
                .withIconImage(nodeMarker!!)
//                .withTextField(point.name)
//                .withTextSize(12.0)



            // Add the resulting pointAnnotation to the map.
            pointAnnotationManager.create(pointAnnotationOptions)
        }

    }

    //Return a bitmap icon with specific width abd height from some vector source
    fun resizeMapIcons(width: Int, height: Int, source: Int): Bitmap? {
        val imageBitmap = BitmapFactory.decodeResource(
            resources,
            source
        )
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }



    fun AddMarkers(pointAnnotationManager : PointAnnotationManager){
        val IS : InputStream = resources.openRawResource(R.raw.heritage)
        val scanner = Scanner(IS)
        val inputStream = resources.openRawResource(R.raw.heritage)
        val byteArrayOutputStream = ByteArrayOutputStream()
        var locationMarker = resizeMapIcons(20,20,R.drawable.location_circle)
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

                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    // Define a geographic coordinate.
                    .withPoint(fromLngLat(longitude, latitude))
                    .withIconColor("#EC681C")
                    // Specify the bitmap you assigned to the point annotation
                    // The bitmap will be added to map style automatically.
                    .withIconImage(locationMarker!!)
                    .withTextField(placeName)
                    .withTextSize(8.0)


                // Add the resulting pointAnnotation to the map.
                pointAnnotationManager.create(pointAnnotationOptions)

//                var herritageMarker = mMap.addMarker(
//                    MarkerOptions()
//                        .position(LatLng(latitude,longitude))
//                        .title(placeName)
//                        .icon(herritageMarker)
//                        .snippet(description)
//                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    fun AskToCompleatRoute(routeValue: RouteValue){
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            CompleateRoute(routeValue)
            AddToTotalGamification(routeValue.distance,routeValue.transportType,"Distance_")
            AddToTotalGamification(routeValue.health,routeValue.transportType,"Health_")
            AddToTotalGamification(routeValue.CoSave,routeValue.transportType,"CoSave_")
        }

        var newDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage("Completing a route will add this route to your log book, as well as provide information to researchers about how transport in the Lake District could be improved.")
            .setNeutralButton("Not Now",null)
            .setPositiveButton("Complete",positiveButtonClick)
            .show()

    }

    fun CompleateRoute(routeValue: RouteValue){

        val builder = AlertDialog.Builder(requireContext())
        val reasons = arrayOf("No Major Reason", "Carbon Savings", "Shortest Distance", "Quickest time",  "Most Scenic Route", "Low Cost","Most Convenient for me", "Other modes are too inconvenient", "Other" )

//        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
//            CalculateCarbon(view)
//        }
//        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
//            val CalculateButton : Button = view.findViewById(R.id.buttonCalculate)
//            CalculateButton.visibility = View.VISIBLE
//        }


        with(builder){
            setTitle("What was your primary reason for choosing this mode of transport?")
            setItems(reasons) { dialog : DialogInterface, which : Int ->
                val myReason = reasons[which]
                RouteLog(routeValue, myReason)
                EmojiThanks()
            }

            setNeutralButton("Go Back", null)
            show()
        }
    }

    fun RouteLog(routeValue: RouteValue, mainReason : String){
        getLocation()
        val locationInfo = myLocation.split("_")
        SendRouteToDatabase(routeValue.startName,routeValue.endName,locationInfo[0],locationInfo[1],"Completion",mainReason)

    }



    fun RateRoute(routeValue: RouteValue,locationLat: String, locationLng: String){
        AskEmoji(routeValue,locationLat,locationLng)
    }

    fun AskEmoji(routeValue: RouteValue,locationLat: String, locationLng: String){
        var viewToShow = layoutInflater.inflate( R.layout.rate_layout, null  )
        var newDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(viewToShow)

            .show()

        //Set up buttons
        with (newDialog){
            var emojiButton_sad = findViewById<CardView>(R.id.emojiCard_Sad)
            emojiButton_sad!!.setOnClickListener(){
                EmojiLog("Sad",routeValue,locationLat,locationLng)
                EmojiThanks()
                newDialog.dismiss()
            }

            var emojiButton_neutral = findViewById<CardView>(R.id.emojiCard_Neutral)
            emojiButton_neutral!!.setOnClickListener(){
                EmojiLog("Neutral",routeValue,locationLat,locationLng)
                EmojiThanks()
                newDialog.dismiss()
            }

            var emojiButton_happy = findViewById<CardView>(R.id.emojiCard_Happy)
            emojiButton_happy!!.setOnClickListener(){
                EmojiLog("Happy",routeValue,locationLat,locationLng)
                EmojiThanks()
                newDialog.dismiss()
            }

            var emojiButton_love = findViewById<CardView>(R.id.emojiCard_Love)
            emojiButton_love!!.setOnClickListener(){
                EmojiLog("Love",routeValue,locationLat,locationLng)
                EmojiThanks()
                newDialog.dismiss()
            }

            var emojiButton_tired = findViewById<CardView>(R.id.emojiCard_Tired)
            emojiButton_tired!!.setOnClickListener(){
                EmojiLog("Tired",routeValue,locationLat,locationLng)
                EmojiThanks()
                newDialog.dismiss()
            }

            var emojiButton_woried = findViewById<CardView>(R.id.emojiCard_Woried)
            emojiButton_woried!!.setOnClickListener(){
                EmojiLog("Woried",routeValue,locationLat,locationLng)
                EmojiThanks()
                newDialog.dismiss()
            }
        }
    }

    fun EmojiThanks()
    {
        var viewToShow = layoutInflater.inflate( R.layout.rate_thanks, null  )
        var newDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(viewToShow)

            .show()
    }


    fun EmojiLog(emoji: String, routeValue: RouteValue,locationLat: String, locationLng: String){
        val myRef = firebaseDatabase.getReference("Route Reactions")
        var newRequest = myRef.child("Emoji Reaction").push()

        var request = EmojiRate()
        var details = RouteDetails() ; details.startLocation = routeValue.startName; details.endLocation = routeValue.endName
        var location = RequestLocation() ; location.requestLat = locationLat ; location.requestLng = locationLng

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val current = formatter.format(time)

        request.survey = getSurveyDetails()
        request.routeDetails = details
        request.date = current
        request.emoji = emoji

        if (AmIInTheLakes(locationLat.toDouble(),locationLng.toDouble()) && getSurveyDetails().useLocation == true){
            request.areInLakes = "In Lake District"
            Log.v("Location", "Shared: I am in the Lakes")
        }
        newRequest.setValue(request)

    }

    private fun SendRouteToDatabase(startName: String, endName: String, locationLat: String, locationLng: String, code : String, reason : String?) {
        val myRef = firebaseDatabase.getReference("Route Requests")
        var newRequest = myRef.child(code).push()

        var request = RouteRequest()
        var details = RouteDetails() ; details.startLocation = startName; details.endLocation = endName
        var location = RequestLocation() ; location.requestLat = locationLat ; location.requestLng = locationLng

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val current = formatter.format(time)

        request.survey = getSurveyDetails()
        request.routeDetails = details

        request.date = current
        if (reason != null){
            request.mainReason = reason
        }
        if (AmIInTheLakes(locationLat.toDouble(),locationLng.toDouble()) && getSurveyDetails().useLocation == true){
            request.areInLakes = "In Lake District"
            Log.v("Location", "Shared: I am in the Lakes")
        }

        newRequest.setValue(request)
    }

    private fun SendVisitToDatabase(placeName: String, placeType : String, locationLat: String, locationLng: String) {
        val myRef = firebaseDatabase.getReference("POI Visits")
        var newRequest = myRef.child("Visit").push()

        var request = VisitDetails()

        var location = RequestLocation() ; location.requestLat = locationLat ; location.requestLng = locationLng

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val current = formatter.format(time)

        request.survey = getSurveyDetails()
        request.visitPlace = placeName
        request.visitType = placeType
        request.date = current

        if (AmIInTheLakes(locationLat.toDouble(),locationLng.toDouble()) && getSurveyDetails().useLocation == true){
            request.areInLakes = "In Lake District"
            Log.v("Location", "Shared: I am in the Lakes")
        }


        newRequest.setValue(request)
    }

    fun getSurveyDetails() : Survey{
        val survey = Survey()

        var context =  requireContext()
        var files: Array<String> = context.fileList()
        var mySurveyString : String = ""

        if (files.isNotEmpty()){
            for (e in files){//
                if (e.startsWith("Survey_Filled")){
                    try {
                        context.openFileInput(e).bufferedReader().useLines { lines ->
                            val readtext = lines.fold("") { some, text ->
                                "$some\n$text"}
                            mySurveyString = readtext
                        }
                    }catch (e: IOException) {
                    }
                }
            }
        }
        if (mySurveyString != ""){
            Log.v("survey", mySurveyString)
            val strings = mySurveyString.split("_")
            survey.age = strings[0] ; survey.gender = strings[1] ; survey.group = strings[2] ; survey.employment = strings[3] ; survey.arrival = strings[4] ;
            if (strings.count() > 4){
                survey.useLocation = strings[5] == "true"

            }
            else{
                survey.useLocation = false
            }


        }
        else{
            Log.v("survey", "No filled in Survey")
        }
        return survey
    }

    fun AmIInTheLakes(latitude: Double,longitude: Double) : Boolean {

        if (latitude != null){
            //Lower Bounds = 54.185567757414454, -3.6637388236082105
            //Upper Bounds = 54.766136795321124, -2.665023197058637
            Log.v("Location","My Lat Long = " + latitude + ", " +longitude )
            if (latitude > 54 && latitude < 55 && longitude > -3.8 && longitude < - 2.5  ){
                Log.v("Location","My location is in the lakes" )
                return true
            }
            else{
                Log.v("Location","My location is not in the lakes" )
                return false
            }
        }
        else{
            Log.v("Location","My location is null?" )
            return false
        }
    }



    @SuppressLint("MissingPermission")
    fun getLocation() : Location? {
        MyLoc = null
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    MyLoc = location
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        myLocation = location.latitude.toString() + "_" + location.longitude.toString()
                        Log.v("mylocation", myLocation.toString())


                    }
                    else{
                        Log.v("mylocation", "I Am Null?")
                        myLocation = "No Location_No Location"
                    }
                }
            //Log.v("mylocation", "I Tried")
        }
        return MyLoc
    }

    fun readLocationsToVisit(){
        val inputStream = resources.openRawResource(R.raw.locations_to_visit)
        val byteArrayOutputStream = ByteArrayOutputStream()
        locationsToVisit = ArrayList<LocationToVisit>()

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
            val jArray = jObjectResult.getJSONArray("Locations")
            var placeName = ""
            var latitude = 0.0
            var longitude = 0.0

            for (i in 0 until jArray.length()) {
                placeName = jArray.getJSONObject(i).getString("Name")
                latitude = jArray.getJSONObject(i).getDouble("Latitude")
                longitude = jArray.getJSONObject(i).getDouble("Longitude")

                val placeLocation : Location = Location("")
                    placeLocation.latitude = latitude
                    placeLocation.longitude = longitude


                locationsToVisit.add(LocationToVisit(placeName,placeLocation,0.0))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.v("Visit", "There are " + locationsToVisit.size + " Locations to visit")
    }

    fun CheckIfVisited(location: String, type: String) : Boolean {
        var fileName = type + location
        var fileContentList = ReadSavedData(type)
        Log.v("Visit", "Check " + fileName  )
        return fileContentList.contains(fileName)
    }

    fun VisitLocation(location: String, type: String){
        var context =  requireContext()
        var fileName = type + location
        var fileContents = "True"
        Log.v("Visit", "Visit " + fileName  )
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }

    fun UnvisitLocation(location: String, type: String){
        var context =  requireContext()
        var fileName = type + location
        var fileContentList = ReadSavedData(type)
        if (fileContentList.contains(fileName)){
            //AskToRemove(context,fileName)
            context.deleteFile(fileName)
        }
    }

    fun AskToRemove(context: Context, fileName : String){
        var newDialog = MaterialAlertDialogBuilder(context)
            .setMessage("Remove Location from logbook")
            .setNegativeButton("Remove") { dialog, which ->
                context.deleteFile(fileName)
            }
            .setNeutralButton("Go Back") { dialog, which ->
            }
            .show()
    }

    fun AddToTotalGamification(distance: Double, type: String, mode : String){

       //"Distance_"
       //"Health_"
       //"CoSave_"

        var context =  requireContext()
        var fileName = mode + type
        var fileContentList = ReadSavedData(fileName)

        var myDist = 0.0
        if (fileContentList.count() > 0){

            var readtext = ""
            context.openFileInput(fileContentList[0]).bufferedReader().useLines { lines ->
                readtext = lines.fold("") { some, text ->
                    "$some\n$text"
                };
            }
            myDist = readtext.toDouble()

        }

        Log.v("saveload", "Previos Distance = " + type + myDist )
        val newDitance = (myDist + distance).toString()

        Log.v("saveload", "New Distance = "+ type + newDitance )
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(newDitance.toByteArray())
        }
    }


    fun ReadSavedData(type: String) : ArrayList<String>{
        var context =  requireContext()
        var files: Array<String> = context.fileList()

        var fileContentList = ArrayList<String>()
        Log.v("saveload", files.count().toString())
        if (files.isNotEmpty()){
            for (e in files){

                if (e.startsWith(type))
                    fileContentList.add(e)
            }
        }
        return fileContentList
    }



    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mapView.onDestroy()
        
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapBoxFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapBoxFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}