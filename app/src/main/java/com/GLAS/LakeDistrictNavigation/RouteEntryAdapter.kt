package com.GLAS.LakeDistrictNavigation

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.GLAS.LakeDistrictNavigation.ui.RouteValue
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.IOException


class RouteEntryAdapter(private var mList: ArrayList<RouteValue>,var faveFrag : Boolean) : RecyclerView.Adapter<RouteEntryAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListner
    lateinit var holder: RouteEntryAdapter.ViewHolder

    interface onItemClickListner{

        fun chooseRoute (string: String)

        fun removeme()


        fun downloadRoute (string: String)

        fun completeRoute (myDetails: RouteValue)

        fun rateRoute (myDetails: RouteValue)

        fun fillSurvey()
    }

    fun setOnItemClickListener(listner: onItemClickListner){
        mListener = listner
    }

    fun CheckIfCanAddToFaveorates(transportOption : String,startNode: String, endNode: String, routeDetails: RouteValue){
        if (ReadFavorates().count() > 2 && getSurveyDetails() == null ){
            AskToFillSurvey(transportOption,startNode,endNode,routeDetails)

        }
        else{
            AddToFavorates(transportOption,startNode,endNode,routeDetails)
        }
    }

    fun AddToFavorates(transportOption : String,startNode: String, endNode: String, routeDetails: RouteValue){
        var context =  holder.distanceValue.context

        //val fileContents = "$transportOption"+"_"+"$transportCode"
        val fileContents = "$transportOption"+"_"+"$startNode"+"_"+"$endNode"

        var fileName = "Favorite_"+fileContents
        val routeDetailsText  : String = routeDetails.id +"_"+ routeDetails.title +"_"+routeDetails.transportType+"_"+
                routeDetails.distance+"_"+routeDetails.time+"_"+routeDetails.health+"_"+routeDetails.Co2+"_"+routeDetails.tranquility+
                "_"+routeDetails.reliability+"_"+routeDetails.difficulty+"_"+routeDetails.startName+"_"+routeDetails.endName+"_"+routeDetails.inverted+"_"+true

        Log.v("saveload", routeDetailsText)

        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(routeDetailsText.toByteArray())
        }
    }

    fun AskToFillSurvey(transportOption : String,startNode: String, endNode: String, routeDetails: RouteValue){
        var context =  holder.distanceValue.context

        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            AddToFavorates(transportOption,startNode,endNode,routeDetails)
            mListener.fillSurvey()
        }

        val neutralButtonClick =  {dialog: DialogInterface, which: Int ->
            holder.heartButton.setImageResource(R.drawable.ic_heart)
        }


        var newDialog = MaterialAlertDialogBuilder(context)
            .setMessage("In order to save more than 3 routes, please fill in our survey.")
            .setNeutralButton("Not Now",neutralButtonClick)
            .setPositiveButton("Ok",positiveButtonClick)
            .show()
    }


    fun RemoveFromFavorates(transportOption : String,startNode: String, endNode: String, position: Int)
    {
        var context =  holder.distanceValue.context
        val fileContents = "$transportOption"+"_"+"$startNode"+"_"+"$endNode"
        var fileName = "Favorite_"+fileContents
        var fileContentList = ReadFavorates()

        if (fileContentList.contains(fileName)){
            AskToRemove(context,fileName,position)
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

    fun CheckIfFavorate(transportOption : String,startNode: String, endNode: String) : FavorateID
    {
        var context =  holder.distanceValue.context
        val fileContents = "$transportOption"+"_"+"$startNode"+"_"+"$endNode"
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

    fun AskToRemove(context: Context, fileName : String, position: Int){
        var newDialog = MaterialAlertDialogBuilder(context)
            .setMessage("Remove route from favorites?")
            .setNegativeButton("Remove") { dialog, which ->
                context.deleteFile(fileName)
                mList[position].favorate = false
                holder.heartButton.setImageResource(R.drawable.ic_heart)
                if (faveFrag){
                    mList.removeAt(position)
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size)
                }
                else {
                    notifyDataSetChanged()
                }

            }
            .setNeutralButton("Go Back") { dialog, which ->
            }
            .show()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteEntryAdapter.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.journey_entry, parent, false)

        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: RouteEntryAdapter.ViewHolder, position: Int) {
        this.holder = holder
        val myRoute = mList[position]

        holder.transportName.text = myRoute.startName + " to "+ myRoute.endName +", "+ myRoute.transportType

//        if (myRoute.title == myRoute.transportType){
//            holder.transportName.text = myRoute.startName + " to "+ myRoute.endName +", "+ myRoute.title.capitalize()
//        }
//        else{
//            holder.transportName.text = myRoute.title.capitalize()
//        }

        setItemIcon(myRoute.transportType,holder.transportIcon)
        if (useMiles()){
            holder.distanceValue.text = ((myRoute.distance*0.621).round(1)).toString() + " M"
        }
        else{
            holder.distanceValue.text = (myRoute.distance.round(1)).toString() + " Km"
        }

        holder.timeValue.text = timeToText(myRoute.time)
        holder.healthValue.text = myRoute.health.round(1).toString() + " kcal"
        holder.co2Value.text = myRoute.Co2.round(1).toString() + " Kg"

        if (myRoute.transportType == "driving"){
            // 5.4 L per 100 Km, or 0.054L/KM
            // About £1.50 per L or petrol
            holder.costValue.text = "£" + (myRoute.distance * 0.054 * 1.5).round(2)
        }
        else if (myRoute.transportType == "Bus"){
            holder.costValue.text = "From £9.50"
            //About 60g per Km
            holder.co2Value.text = (myRoute.distance*0.06).round(1).toString() + " Kg"
        }
        else{
            holder.costValue.text = "Free"

        }



        holder.tranquilityValue.text = myRoute.tranquility.toInt().toString()  + "/5"
        holder.dificultyValue.text = myRoute.difficulty
        holder.reliabilityValue.text = myRoute.reliability.toInt().toString() + "/5"
        holder.co2Saved.text = myRoute.CoSave.round(1).toString() + "Kg"

        //handle the heart button
        with(holder.heartButton){
            val favorateID = CheckIfFavorate(myRoute.transportType,myRoute.startName,myRoute.endName)
            if (favorateID.isFavorate){
                setImageResource(R.drawable.heart_filled)
            }
            else{
                setImageResource(R.drawable.ic_heart)
            }
            setOnClickListener(){
                val favorateID = CheckIfFavorate(myRoute.transportType,myRoute.startName,myRoute.endName)
                if (!favorateID.isFavorate){
                    CheckIfCanAddToFaveorates(myRoute.transportType,myRoute.startName,myRoute.endName,myRoute)
                    setImageResource(R.drawable.heart_filled)
                }
                else{
                    RemoveFromFavorates(myRoute.transportType,myRoute.startName,myRoute.endName, position)
                }
            }
        }

        with(holder.chooseButton){
            if (faveFrag){
                text = "View"
                setOnClickListener(){
                    Log.v("Button", myRoute.transportType)
                    mListener.chooseRoute("${myRoute.transportType}"+"_"+"${myRoute.startName}"+"_"+"${myRoute.endName}")
                }
            }
            else {
                text = "Show"
                setOnClickListener(){
                    Log.v("Button", myRoute.transportType)
                    mListener.chooseRoute("${myRoute.transportType}"+"_"+"${myRoute.startName}"+"_"+"${myRoute.endName}")
                }
            }
        }

        with(holder.moreOptionsButton){
            Log.v("Button","I Exist")
            if (faveFrag){
                visibility = View.GONE
            }

            setOnClickListener(){
                Log.v("Button","Click")
                if (holder.moreOptionsLayout.visibility == View.VISIBLE){
                    Log.v("Button","Hide Layout")
                    holder.moreOptionsLayout.visibility = View.GONE
                }
                else{
                    Log.v("Button","Show Layout")
                    holder.moreOptionsLayout.visibility = View.VISIBLE
                }
            }
        }

        with(holder.downloadButton){
            if (myRoute.transportType == "Walk"){
                setOnClickListener(){
                    mListener.downloadRoute("${myRoute.transportType}"+"_"+"${myRoute.startName}"+"_"+"${myRoute.endName}")
                }
            }
            else {
                visibility = View.GONE
            }
        }

        with(holder.completeButton){
            setOnClickListener(){
                mListener.completeRoute(myRoute)
            }
        }

        with(holder.rateButton){
            setOnClickListener(){
                mListener.rateRoute(myRoute)
            }
        }



    }

    fun setItemIcon(trnsportOption: String, icon: ImageView){
        Log.v("saveload",trnsportOption)
        if (trnsportOption == "Walk") {
            icon.setImageResource(R.drawable.walk)}
        if (trnsportOption == "cycling") {
            icon.setImageResource(R.drawable.bike)}
        if (trnsportOption == "driving") {
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
    class ViewHolder(ItemView: View, listner: RouteEntryAdapter.onItemClickListner) : RecyclerView.ViewHolder(ItemView) {


        val transportIcon : ImageView = itemView.findViewById(R.id.transport_icon)
        val transportName: TextView = itemView.findViewById(R.id.transport_name)

        val distanceValue : TextView = itemView.findViewById(R.id.details_distance_value)
        val timeValue : TextView = itemView.findViewById(R.id.details_time_value)
        val healthValue : TextView = itemView.findViewById(R.id.details_health_value)


        val costValue : TextView = itemView.findViewById(R.id.details_cost_value)
        val co2Value : TextView = itemView.findViewById(R.id.details_co2_value)
        val tranquilityValue : TextView = itemView.findViewById(R.id.details_tranq_value)
        val dificultyValue : TextView = itemView.findViewById(R.id.details_difficulty_value)
        val reliabilityValue : TextView = itemView.findViewById(R.id.details_reliability_value)
        val co2Saved : TextView = itemView.findViewById(R.id.details_coSave_value)

        val heartButton : ImageButton = itemView.findViewById(R.id.heartButton)
        val chooseButton : Button = itemView.findViewById(R.id.transport_button)
        val moreOptionsButton : Button = itemView.findViewById(R.id.moreOptions_button)
        val moreOptionsLayout : View = itemView.findViewById(R.id.moreOptions_Layout)

        val downloadButton : Button = itemView.findViewById(R.id.transport_button_download)
        val completeButton : Button = itemView.findViewById(R.id.transport_button_compleat)
        val rateButton : Button = itemView.findViewById(R.id.transport_button_rate)


        var favorateID = FavorateID(false,0)
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return (kotlin.math.round(this * multiplier) / multiplier)
    }

    fun useMiles() : Boolean
    {
        var context =  holder.distanceValue.context
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

    fun timeToText(time : Double) : String{

        val doubleAsString = time.toString()
        val substring = doubleAsString.split(".")
        val hours = substring[0]
        val minsD = "0." + substring[1]
        val mins = (60.0 * minsD.toDouble()).toInt().toString()

        val textTime : String
        if (hours.toDouble() > 1.0){
            textTime = hours + " Hr, " + mins + " Min"
        }
        else{
            textTime = mins + " Min"
        }

        return textTime
    }

    fun getSurveyDetails() : Survey?{
        val survey = Survey()

        var context =  holder.distanceValue.context
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
            survey.age = strings[0] ; survey.gender = strings[1] ; survey.group = strings[2] ; survey.employment = strings[3] ; survey.arrival = strings[4]
        }
        else{
            Log.v("survey", "No filled in Survey")
            return null
        }
        return survey
    }

}