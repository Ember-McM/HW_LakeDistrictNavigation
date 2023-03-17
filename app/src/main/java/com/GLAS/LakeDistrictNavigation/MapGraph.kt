package com.GLAS.LakeDistrictNavigation
import android.util.Log
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class MapGraph() {

    var g : Graph<String, DefaultWeightedEdge> =  WeightedMultigraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

    fun ReadJson() {


    }






}

data class MapNode(val name: String, val latitude : Double, val longitude : Double )