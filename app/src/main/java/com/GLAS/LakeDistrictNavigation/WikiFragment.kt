package com.GLAS.LakeDistrictNavigation

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class WikiFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WikiEntryAdapter
    private var data = ArrayList<WikiViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wiki, container, false)


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())


    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Import Json
        val IS : InputStream = resources.openRawResource(R.raw.wiki)
        val scanner = Scanner(IS)
        val inputStream = resources.openRawResource(R.raw.wiki)
        val byteArrayOutputStream = ByteArrayOutputStream()



        var myLocation : Location? = null


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
            val jArray = jObjectResult.getJSONArray("Wiki")
            var placeName = ""
            var description = ""
            var latitude = 0.0
            var longitude = 0.0
            var link = ""

            for (i in 0 until jArray.length()) {


                placeName = jArray.getJSONObject(i).getString("Name")
                latitude = jArray.getJSONObject(i).getDouble("Latitude")
                longitude = jArray.getJSONObject(i).getDouble("Longitude")
                description = jArray.getJSONObject(i).getString("Description")
                var category = jArray.getJSONObject(i).getString("Catagory")
                link = jArray.getJSONObject(i).getString("Link").split("\n")[0]
                var shortDisc = description.split(".")[0]+"..."

                val placeLocation : Location = Location("")
                placeLocation.latitude = latitude
                placeLocation.longitude = longitude



                val tempLocation : Location = Location("")
                tempLocation.latitude = 0.0
                tempLocation.longitude = 0.0

                data.add(WikiViewModel(category, placeName,tempLocation,link,placeLocation,shortDisc,description,0f))
            }



                //Log.v("wiki", link)


        } catch (e: Exception) {
            e.printStackTrace()
        }




        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        myLocation = location
                        Log.v("mylocation", myLocation.toString())
                        for (element in data){
                            element.updateValues(location)
                            element.udpdateDistance()



                        }
                        recyclerView.adapter?.notifyDataSetChanged()

                    }
                    else{
                        Log.v("mylocation", "I Am Null?")
                    }
                }
            //Log.v("mylocation", "I Tried")
        }

        // getting the recyclerview by its id
        recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerview)

        // this creates a vertical layout Manager
        recyclerView?.layoutManager = LinearLayoutManager(activity)

        // ArrayList of class ItemsViewModel


        // This loop will create 20 Views containing
        // the image with the count of view


        // This will pass the ArrayList to our Adapter
        adapter = WikiEntryAdapter(data)
        adapter.setOnItemClickListener(object  : WikiEntryAdapter.onItemClickListner{


            override fun showOnMapClick(position: Location) {
                super.showOnMapClick(position)
                val zoomLocation : String = position.latitude.toString() +","+  position.longitude.toString()
                val action = WikiFragmentDirections.actionWikiFragmentToMapBoxFragment(zoomLocation)

                view.findNavController().navigate(action)


            }

            override fun linkClick(position: String) {
                super.linkClick(position)
                val uri: Uri =
                    Uri.parse(position) // missing 'http://' will cause crashed

                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

        })

        // Setting the Adapter with the recyclerview
        recyclerView?.adapter = adapter
        filterAlphabet()
        Log.d("Adapter" ,adapter.itemCount.toString())

        var editText : EditText = view?.findViewById(R.id.editTextSearch)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        //Spinner
        val spinner: Spinner = view?.findViewById(R.id.spinnerSort)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.SortArray,
            android.R.layout.simple_spinner_item
        ).also { sadapter ->
            // Specify the layout to use when the list of choices appears
            sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = sadapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val text: String = spinner.selectedItem.toString()
                when (text) {
                    "Distance" -> sortByDistance()
                    "Villages" -> sortByCatagory("village")
                    "Lakes" -> sortByCatagory("lake")
                    "Heritage" -> sortByCatagory("heritage")
                    "Activities" -> sortByCatagory("activity")
                    "Nature" -> sortByCatagory("nature")
                    else -> { // Note the block
                        filterAlphabet()
                    }
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



    }



    private fun filter(textIn: String) {
        var filteredList = ArrayList<WikiViewModel>()

        for (item in data){
            if (item.placeName.toLowerCase().contains(textIn.toLowerCase())){
                filteredList.add(item)
            }
        }
        adapter.filterList(filteredList)

    }

    private fun filterAlphabet() {
        adapter.notifyDataSetChanged()
        var filteredList = data
        filteredList.sortBy { it.placeName }



        adapter.filterList(filteredList)

    }


    private fun sortByCatagory(catagory: String) {
        var filteredList = ArrayList<WikiViewModel>()

        for (item in data){
            if (item.category == catagory){
                filteredList.add(item)
            }
        }
        adapter.filterList(filteredList)

    }

    private fun sortByDistance() {
        adapter.notifyDataSetChanged()
        var filteredList = data
        filteredList.sortBy { it.distance }



        adapter.filterList(filteredList)

    }



}