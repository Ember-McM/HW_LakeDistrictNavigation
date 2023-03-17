package com.GLAS.LakeDistrictNavigation

import android.content.Context
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException

data class FavorateID (val isFavorate : Boolean, val ID : Int)
class JourneyEntryAdapter(private var mList: List<JourneyViewModel>) : RecyclerView.Adapter<JourneyEntryAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListner
    lateinit var holder: JourneyEntryAdapter.ViewHolder

    interface onItemClickListner{

        fun chooseRoute (string: String)

    }

    fun setOnItemClickListener(listner: onItemClickListner){
        mListener = listner
    }

    fun AddToFavorates(transportOption : String,transportCode: String){
        var context =  holder.distanceValue.context


        val fileContents = "$transportOption"+"_"+"$transportCode"

        var fileName = "Favorite_"+fileContents



        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContents.toByteArray())
        }
    }


    fun RemoveFromFavorates(transportOption : String,transportCode: String)
    {
        var context =  holder.distanceValue.context
        val fileContents = "$transportOption"+"_"+"$transportCode"
        var fileName = "Favorite_"+fileContents
        var fileContentList = ReadFavorates()

        if (fileContentList.contains(fileName)){
            AskToRemove(context,fileName)

        }




    }

    fun ReadFavorates() : ArrayList<String>
    {
        var context =  holder.distanceValue.context
        var files: Array<String> = context.fileList()


        var fileContentList = ArrayList<String>()
        Log.v("saveload", files.count().toString())
        if (files.isNotEmpty()){
            for (e in files){
//                try {
//                    context.openFileInput(e).bufferedReader().useLines { lines ->
//                        val readtext = lines.fold("") { some, text ->
//                            "$some\n$text"}
//                        fileContentList.add(readtext)
//                        Log.v("saveload", e)
//                    }
//                }catch (e: IOException) {
//
//                }
                if (e.startsWith("Favorite"))
                    fileContentList.add(e)

            }
        }

        return fileContentList
    }

    fun CheckIfFavorate(transportOption : String,transportCode: String) : FavorateID
    {
        var context =  holder.distanceValue.context
        val fileContents = "$transportOption"+"_"+"$transportCode"
        var fileName = "Favorite_"+fileContents

        var fileContentList = ReadFavorates()

        return if (fileContentList.contains(fileName)){
            val faveNum = fileContentList.indexOf(fileName)

            Log.v("saveload", fileName + " Is a fave")
            FavorateID(true,faveNum)
        } else{
            Log.v("saveload",fileName)
            FavorateID(false,0)
        }
    }

    fun AskToRemove(context: Context, fileName : String){
        var newDialog = MaterialAlertDialogBuilder(context)
            .setMessage("Remove route from favorites?")
            .setNegativeButton("Remove") { dialog, which ->
                context.deleteFile(fileName)
                holder.heartButton.setImageResource(R.drawable.ic_heart)
            }
            .setNeutralButton("Go Back") { dialog, which ->
            }
            .show()
    }







    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyEntryAdapter.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.journey_entry, parent, false)

        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: JourneyEntryAdapter.ViewHolder, position: Int) {
        this.holder = holder
        val JourneyViewModel = mList[position]

        holder.transportName.text = JourneyViewModel.transportOption
        setItemIcon(JourneyViewModel.transportOption,holder.transportIcon)
        holder.distanceValue.text = JourneyViewModel.routeValue.distance.toString()
        holder.timeValue.text = JourneyViewModel.routeValue.time.toString()
        holder.healthValue.text = JourneyViewModel.routeValue.health.toString()

        holder.costValue.text = JourneyViewModel.transportCode
        holder.co2Value.text = JourneyViewModel.routeValue.Co2.toString()
        holder.tranquilityValue.text = JourneyViewModel.routeValue.tranquility.toString()
        holder.dificultyValue.text = JourneyViewModel.routeValue.difficulty
        holder.reliabilityValue.rating = JourneyViewModel.routeValue.reliability.toFloat()

        //handle the heart button
        with(holder.heartButton){
            val favorateID = CheckIfFavorate(JourneyViewModel.transportOption,JourneyViewModel.transportCode)
            if (favorateID.isFavorate){
                setImageResource(R.drawable.heart_filled)
            }
            setOnClickListener(){
                val favorateID = CheckIfFavorate(JourneyViewModel.transportOption,JourneyViewModel.transportCode)
                if (!favorateID.isFavorate){
                    AddToFavorates(JourneyViewModel.transportOption,JourneyViewModel.transportCode)
                    setImageResource(R.drawable.heart_filled)
                }
                else{
                    RemoveFromFavorates(JourneyViewModel.transportOption,JourneyViewModel.transportCode)

                }
            }
        }

        with(holder.chooseButton){
            if (JourneyViewModel.favouriteMenu){
                text = "View"
                setOnClickListener(){
                    mListener.chooseRoute(JourneyViewModel.transportOption +","+JourneyViewModel.transportCode)
                }
            }
            else {
                text = "Choose"
            }
        }





    }

    fun setItemIcon(trnsportOption: String, icon: ImageView){
        Log.v("saveload",trnsportOption)
        if (trnsportOption == "Walk") {
            icon.setImageResource(R.drawable.walk)}
        if (trnsportOption == "Bike") {
            icon.setImageResource(R.drawable.bike)}
        if (trnsportOption == "Car") {
            icon.setImageResource(R.drawable.car)}
        if (trnsportOption == "Bus") {
            icon.setImageResource(R.drawable.bus)}
        if (trnsportOption == "Train") {
            icon.setImageResource(R.drawable.train)}
        if (trnsportOption == "Ferry") {
            icon.setImageResource(R.drawable.ferry)}



    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, listner: JourneyEntryAdapter.onItemClickListner) : RecyclerView.ViewHolder(ItemView) {


        val transportIcon : ImageView = itemView.findViewById(R.id.transport_icon)
        val transportName: TextView = itemView.findViewById(R.id.transport_name)

        val distanceValue : TextView = itemView.findViewById(R.id.details_distance_value)
        val timeValue : TextView = itemView.findViewById(R.id.details_time_value)
        val healthValue : TextView = itemView.findViewById(R.id.details_health_value)


        val costValue : TextView = itemView.findViewById(R.id.details_cost_value)
        val co2Value : TextView = itemView.findViewById(R.id.details_co2_value)
        val tranquilityValue : TextView = itemView.findViewById(R.id.details_tranq_value)
        val dificultyValue : TextView = itemView.findViewById(R.id.details_difficulty_value)
        val reliabilityValue : RatingBar = itemView.findViewById(R.id.details_reliability_value)

        val heartButton : ImageButton = itemView.findViewById(R.id.heartButton)
        val chooseButton : Button = itemView.findViewById(R.id.transport_button)



        var favorateID = FavorateID(false,0)
    }



}