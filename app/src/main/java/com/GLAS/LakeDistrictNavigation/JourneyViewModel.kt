package com.GLAS.LakeDistrictNavigation

import com.GLAS.LakeDistrictNavigation.ui.RouteValue

data class JourneyViewModel(val transportOption : String, val transportCode: String, val routeValue: RouteValue, val favouriteMenu : Boolean) {
}