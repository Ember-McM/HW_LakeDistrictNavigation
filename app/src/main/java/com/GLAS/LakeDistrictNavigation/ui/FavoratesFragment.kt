package com.GLAS.LakeDistrictNavigation.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.GLAS.LakeDistrictNavigation.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var RoutesValueList = ArrayList<RouteValue>()

/**
 * A simple [Fragment] subclass.
 * Use the [FavoratesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoratesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView : RecyclerView

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
        return inflater.inflate(R.layout.fragment_favorates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.favorate_recycler)


        var faveCodes = ReadFavorates()
        val routeList = ArrayList<RouteValue>()
        for (e in faveCodes) {
            val strings = e.split("_")
            Log.v("saveload", strings.toString())
            routeList.add(RouteValue(strings[0],strings[1], strings[2],strings[3].toDouble(),strings[4].toDouble(),strings[5].toDouble(),strings[6].toDouble(),strings[7].toDouble(),strings[8].toDouble(),strings[9],strings[10],strings[11],strings[12].toBoolean(),strings[13].toBoolean()))
        }

        val data = routeList
        for (transportType in routeList) {
//            data.add(
//
//                )
//            )
        }
        val adapter = RouteEntryAdapter(data,true)

        adapter.setOnItemClickListener(object : RouteEntryAdapter.onItemClickListner {
            override fun chooseRoute(string: String) {
                val action = FavoratesFragmentDirections.actionFavoratesFragmentToMapBoxFragment("0.0,0.0",string)
                view.findNavController().navigate(action)
            }

            override fun removeme() {
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun downloadRoute(string: String) {

            }

            override fun completeRoute(myDetails: RouteValue) {

            }

            override fun rateRoute(myDetails: RouteValue) {

            }

            override fun fillSurvey() {

            }
        })


        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)


    }




    fun ReadFavorates() : ArrayList<String>
    {
        var context =  requireContext()
        var files: Array<String> = context.fileList()


        var fileContentList = ArrayList<String>()
        Log.v("saveload", files.count().toString())
        if (files.isNotEmpty()){
            for (e in files){

                if (e.startsWith("Favorite"))
                    try {
                        context.openFileInput(e).bufferedReader().useLines { lines ->
                            val readtext = lines.fold("") { some, text ->
                                "$some\n$text"}
                            fileContentList.add(readtext)
                            Log.v("saveload", readtext)
                        }
                    }catch (e: IOException) {

                    }
            }
        }

        return fileContentList
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoratesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoratesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}