package com.example.proyectokotlin.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectokotlin.R

class FavoritesAdapter (private val context: Context): RecyclerView.Adapter<FavoritesAdapter.MainViewHolder>() {

    private var datalist= mutableListOf<Favorites>()
    private lateinit var mListener: onItemClickListener

    fun setListData(data: MutableList<Favorites>){
        datalist = data
    }
    interface onItemClickListener{
        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesAdapter.MainViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.location_row,parent,false)
        return MainViewHolder(view,mListener)
    }

    override fun onBindViewHolder(holder: FavoritesAdapter.MainViewHolder, position: Int) {
        val favorite = datalist[position]
        holder.bindView(favorite)
    }

    override fun getItemCount(): Int {
        if(datalist.size>0){
            return datalist.size
        }
        else{
            return 0
        }
    }

    inner class MainViewHolder(itemView: View, listener: FavoritesAdapter.onItemClickListener):RecyclerView.ViewHolder(itemView){
        private lateinit var tvName: TextView
        private lateinit var tvInfo: TextView
        fun bindView(favorites: Favorites){
            Glide.with(context).load(favorites.img).into(itemView.findViewById(R.id.ivIlocation))
            tvName = itemView.findViewById<TextView?>(R.id.tvNameL)
            tvName.text = favorites.name
            tvInfo = itemView.findViewById(R.id.tvInfo)
            tvInfo.text = favorites.info
        }
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }

        }
    }
}