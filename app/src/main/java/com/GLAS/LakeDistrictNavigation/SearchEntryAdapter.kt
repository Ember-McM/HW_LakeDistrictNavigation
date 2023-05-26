package com.GLAS.LakeDistrictNavigation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.GLAS.LakeDistrictNavigation.ui.MapNode

class SearchEntryAdapter(private var mList: ArrayList<MapNode>) : RecyclerView.Adapter<SearchEntryAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListner
    lateinit var holder: SearchEntryAdapter.ViewHolder

    interface onItemClickListner{
        fun chooseLocation (mapNode: MapNode)
    }

    fun setOnItemClickListener(listner: onItemClickListner){
        mListener = listner
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchEntryAdapter.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_entry, parent, false)

        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: SearchEntryAdapter.ViewHolder, position: Int) {
        this.holder = holder
        val myNode = mList[position]

        if (myNode.location){
            holder.pinSpacer.visibility = View.GONE
            holder.pinIcon.setImageResource(R.drawable.pin)
            holder.searchCard.setCardBackgroundColor(holder.itemView.context.resources.getColor(R.color.seachBG))
        }
        else {
            holder.pinSpacer.visibility = View.VISIBLE
            holder.searchCard.setCardBackgroundColor(holder.itemView.context.resources.getColor(R.color.white))
            holder.pinIcon.setImageResource(R.drawable.routeicon)
        }

        holder.searchText.text = myNode.name
        holder.searchCard.setOnClickListener(){
            mListener.chooseLocation(myNode)
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, listner: onItemClickListner) : RecyclerView.ViewHolder(ItemView) {

        val searchCard : CardView = itemView.findViewById(R.id.SearchEntryCard)
        val searchText : TextView = itemView.findViewById(R.id.SeachEntryText)
        val pinIcon : ImageView = itemView.findViewById(R.id.pinIcon)
        val pinSpacer : Space = itemView.findViewById(R.id.pinSpacer)

    }

    public fun filterList(list : ArrayList<MapNode>){
        mList = list
        notifyDataSetChanged()
    }
}