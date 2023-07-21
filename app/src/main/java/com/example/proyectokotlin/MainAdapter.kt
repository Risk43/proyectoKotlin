package com.example.proyectokotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainAdapter(private val context: Context): RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var datalist= mutableListOf<Locations>()
    private lateinit var mListener: onItemClickListener

    fun setListData(data: MutableList<Locations>){
        datalist = data
    }

    interface onItemClickListener{
        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.location_row,parent,false)
        return MainViewHolder(view,mListener)
    }
    override fun getItemCount(): Int {
        if(datalist.size>0){
            return datalist.size
        }
        else{
            return 0
        }
    }
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val location = datalist[position]
        holder.bindView(location)
    }
    inner class MainViewHolder(itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){

         private lateinit var tvName: TextView
         private lateinit var tvInfo: TextView

        fun bindView(locations: Locations){
            Glide.with(context).load(locations.img).into(itemView.findViewById(R.id.ivIlocation))
            tvName = itemView.findViewById<TextView?>(R.id.tvNameL)
            tvName.text = locations.name
            tvInfo = itemView.findViewById(R.id.tvInfo)
            tvInfo.text = locations.info
        }

        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

        }

    }
}
