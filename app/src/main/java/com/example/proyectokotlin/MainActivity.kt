package com.example.proyectokotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {


    private lateinit var rvLocations: RecyclerView
    private lateinit var adapter: MainAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        adapter = MainAdapter(this)
        rvLocations = findViewById(R.id.rvLocations)



        rvLocations.layoutManager = LinearLayoutManager(this)
        rvLocations.adapter = adapter


        observeData()


    }

    private fun observeData(){
        var container: ShimmerFrameLayout = findViewById(R.id.shimmer_view_container)
        container.startShimmer()
        viewModel.fetchLocationData().observe(this, Observer {
            container.stopShimmer()
            container.visibility = View.GONE
            adapter.setListData(it)
            adapter.notifyDataSetChanged()

            adapter.setOnItemClickListener(object : MainAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                    val intent = Intent(this@MainActivity,LocationsActivity::class.java)

                    intent.putExtra("img",it[position].img)
                    intent.putExtra("info",it[position].info)
                    intent.putExtra("name",it[position].name)
                    startActivity(intent)
                }
            })
        })
    }
}