package com.GLAS.LakeDistrictNavigation

import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng

data class WikiViewModel(val category: String, val placeName: String, var myLocation: Location, val mainLink: String, val placeLocation : Location, val shortDisc: String, val longDisc : String, var distance : Float   ) {

    fun udpdateDistance(){
        distance = myLocation?.distanceTo(placeLocation)

    }




    fun updateValues(newLocation:Location){
        myLocation = newLocation

    }


}