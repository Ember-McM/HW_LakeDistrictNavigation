package com.GLAS.LakeDistrictNavigation

import android.location.Location
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.round


class WikiEntryAdapter(private var mList: List<WikiViewModel>) : RecyclerView.Adapter<WikiEntryAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListner
    lateinit var holder: ViewHolder

    interface onItemClickListner{

        fun showOnMapClick(position: Location){

        }

        fun linkClick(link: String)
        {


        }

    }

    fun setOnItemClickListener(listner: onItemClickListner){
        mListener = listner
    }



    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.wiki_entry, parent, false)

        return ViewHolder(view,mListener)
    }

    fun readMoreText(myholder : ViewHolder){
        if (myholder.shortDesc.visibility == View.GONE){
            myholder.longDesc.visibility = View.GONE
            myholder.shortDesc.visibility = View.VISIBLE
        }
        else{
            myholder.shortDesc.visibility = View.GONE
            myholder.longDesc.visibility = View.VISIBLE
        }
    }




    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        this.holder = holder
        val WikiViewModel = mList[position]
        val icon = ReturnWikiIcon(WikiViewModel.category);
        holder.imageView.setImageResource(icon)
        //TODO
        //Do some Catagory Stuff here
        holder.placeName.text = WikiViewModel.placeName
        holder.shortDesc.text = WikiViewModel.shortDisc
        holder.shortDesc.visibility = View.VISIBLE
        holder.longDesc.text = WikiViewModel.longDisc
        holder.longDesc.visibility = View.GONE

        if (WikiViewModel.myLocation != null && WikiViewModel.myLocation.latitude != 0.0 ){
            var meterDist = WikiViewModel.myLocation?.distanceTo(WikiViewModel.placeLocation)
            var kiloDist = roundOffDecimal(meterDist/1000.0)




            holder.distanceText.text = kiloDist.toString() + " Km"
        }

        holder.readMore.setOnClickListener(){
            readMoreText(holder)
        }
        holder.locateButton.setOnClickListener(){
            mListener.showOnMapClick(WikiViewModel.placeLocation)
        }

        holder.linkButton.setOnClickListener(){
            mListener.linkClick(WikiViewModel.mainLink)
        }




    }

    fun ReturnWikiIcon(iconName: String) : Int{
        val Icon : Int

        when (iconName) {
            "village" -> Icon = R.drawable.villageicon;
            "lake" -> Icon = R.drawable.lakeicon;
            "heritage"-> Icon = R.drawable.mueseumicon;
            "activity"-> Icon = R.drawable.routeicon;
            "boat"-> Icon = R.drawable.boaticon;
            "nature"-> Icon = R.drawable.treeicon;
            "writer"-> Icon = R.drawable.writericon;
            "museum"-> Icon = R.drawable.mueseumicon;
            "garden"-> Icon = R.drawable.flowericon;
            "castle"-> Icon = R.drawable.castleicon;
            "waterfall"-> Icon = R.drawable.waterfallicon;
            else -> { // Note the block
                Icon = R.drawable.villageicon;
            }
        }
        return Icon

    }

    fun roundOffDecimal(number: Double): Double? {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, listner: onItemClickListner) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.CatagoryIcon)
        val placeName: TextView = itemView.findViewById(R.id.Title)
        val shortDesc: TextView = itemView.findViewById(R.id.ShortText)
        val longDesc: TextView = itemView.findViewById(R.id.LongText)
        val distanceText: TextView = itemView.findViewById(R.id.Distance)
        val readMore : CardView = itemView.findViewById(R.id.ReadMoreCard)

        val linkButton : ImageView = itemView.findViewById(R.id.LinkButton)
        val locateButton : ImageView = itemView.findViewById(R.id.LocateButton)


    }

    public fun filterList(list : ArrayList<WikiViewModel>){
        mList = list
        notifyDataSetChanged()
    }
}
