package com.GLAS.LakeDistrictNavigation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.GLAS.LakeDistrictNavigation.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GamificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GamificationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gamification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var MWSVisited = ReadVisitedLoc("mws_")
        var LocationsVisited = ReadVisitedLoc("location_")

        var MWSNum = view.findViewById<TextView>(R.id.MWS_num)
        var LocationNum = view.findViewById<TextView>(R.id.Visit_num)

        MWSNum.text = MWSVisited.count().toString() + "/49"
        LocationNum.text = LocationsVisited.count().toString() + "/107"

        //Handle total distances
        val walkDist = ReadTotalGamifiaction("Walk","Distance_")
        val bikeDist = ReadTotalGamifiaction("cycling","Distance_")
        val carDist = ReadTotalGamifiaction("driving","Distance_")
        val busDist = ReadTotalGamifiaction("Bus","Distance_")
        val totalDist = walkDist + bikeDist + carDist + busDist

        var walkText = view.findViewById<TextView>(R.id.textDistanceTraveled_walk)
        var bikeText = view.findViewById<TextView>(R.id.textDistanceTraveled_bike)
        var carText = view.findViewById<TextView>(R.id.textDistanceTraveled_car)
        var busText = view.findViewById<TextView>(R.id.textDistanceTraveled_bus)
        var totalText = view.findViewById<TextView>(R.id.textViewDistance_total)

        if (useMiles()){
            walkText.text = ((walkDist*0.621).round(1)).toString() + " M"
            bikeText.text = ((bikeDist*0.621).round(1)).toString() + " M"
            carText.text = ((carDist*0.621).round(1)).toString() + " M"
            busText.text = ((busDist*0.621).round(1)).toString() + " M"
            totalText.text = "Across all journeys, you have traveled a total of " +((totalDist*0.621).round(1)).toString() + " M"
        }
        else{
            walkText.text  = (walkDist.round(1)).toString() + " Km"
            bikeText.text  = (bikeDist.round(1)).toString() + " Km"
            carText.text  = (carDist.round(1)).toString() + " Km"
            busText.text  = (busDist.round(1)).toString() + " Km"
            totalText.text = "Across all journeys, you have traveled a total of " + (totalDist.round(1)).toString() + " Km"
        }



    }

    fun ReadVisitedLoc(type: String) : ArrayList<String>{
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

    fun ReadTotalGamifiaction(type: String, mode: String) : Double{
        var context =  requireContext()
        var fileName = "Distance_$type"
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
        return myDist
    }

    fun useMiles() : Boolean
    {
        var context = requireContext()
        var files: Array<String> = context.fileList()
        Log.v("Options", files.count().toString())
        if (files.isNotEmpty()){
            for (e in files){//
                if (e.startsWith("UseMiles"))
                    return true
            }
        }
        return false
    }

    fun ReadSavedData(type: String) : java.util.ArrayList<String> {
        var context =  requireContext()
        var files: Array<String> = context.fileList()

        var fileContentList = java.util.ArrayList<String>()
        Log.v("saveload", files.count().toString())
        if (files.isNotEmpty()){
            for (e in files){

                if (e.startsWith(type))
                    fileContentList.add(e)
            }
        }
        return fileContentList
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (kotlin.math.round(this * multiplier) / multiplier)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GamificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GamificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}