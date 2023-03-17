package com.GLAS.LakeDistrictNavigation

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.GLAS.LakeDistrictNavigation.ui.RouteValue
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException

data class ConnectionViewModel(val startLocation : String, val endLocation: String) {
}

class ConnectionEntryAdapter(private var mList: List<ConnectionViewModel>) : RecyclerView.Adapter<ConnectionEntryAdapter.ViewHolder>(){

    private lateinit var mListener : onItemClickListner
    lateinit var holder: ConnectionEntryAdapter.ViewHolder

    interface onItemClickListner{

        fun chooseConnectoion (startLocation: String, endLocation: String )
    }

    fun setOnItemClickListener(listner: onItemClickListner){
        mListener = listner
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectionEntryAdapter.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.conection_entry, parent, false)

        return ViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ConnectionEntryAdapter.ViewHolder, position: Int) {
        this.holder = holder
        val ConnectionViewModel = mList[position]

        holder.startLocation.text = ConnectionViewModel.startLocation
        holder.endLocation.text = ConnectionViewModel.endLocation


        with(holder.viewButton){
            setOnClickListener(){
                mListener.chooseConnectoion(ConnectionViewModel.startLocation, ConnectionViewModel.endLocation)
            }

        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, listner: ConnectionEntryAdapter.onItemClickListner) : RecyclerView.ViewHolder(ItemView) {

        val startLocation : TextView = itemView.findViewById(R.id.conection_From_Details)
        val endLocation : TextView = itemView.findViewById(R.id.conection_To_Details)
        val viewButton : Button = itemView.findViewById(R.id.conection_Button)
    }



}