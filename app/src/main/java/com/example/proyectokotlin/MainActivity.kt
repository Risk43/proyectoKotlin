package com.example.proyectokotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import java.util.Locale


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {


    private lateinit var rvLocations: RecyclerView
    private lateinit var adapter: MainAdapter
    private lateinit var svSearch: SearchView
    private var globalList = mutableListOf<Locations>()
    private val viewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        adapter = MainAdapter(this)
        rvLocations = findViewById(R.id.rvLocations)
        svSearch = findViewById(R.id.svSearch)



        rvLocations.layoutManager = LinearLayoutManager(this)
        rvLocations.adapter = adapter


        observeData()
        globalList.clear()


    }

    private fun observeData(){
        var container: ShimmerFrameLayout = findViewById(R.id.shimmer_view_container)
        container.startShimmer()
        viewModel.fetchLocationData().observe(this, Observer {
            container.stopShimmer()
            container.visibility = View.GONE
            adapter.setListData(it)
            adapter.notifyDataSetChanged()

            svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        val locationsList = mutableListOf<Locations>()
                        for (i in it) {
                            if (i.name.lowercase(Locale.ROOT).contains(newText)){
                                    locationsList.add(i)
                            }
                        }
                        if (locationsList.isEmpty()) {
                            Toast.makeText(this@MainActivity,"No se encontraron resultados", Toast.LENGTH_SHORT).show()
                        } else {
                            globalList = locationsList
                            adapter.setListData(locationsList)
                            adapter.notifyDataSetChanged()
                        }
                    }
                    return true
                }
            })

            adapter.setOnItemClickListener(object : MainAdapter.onItemClickListener{
                override fun onItemClick(position: Int) {
                        if (globalList.isEmpty()){
                            val intent = Intent(this@MainActivity,LocationsActivity::class.java)
                            intent.putExtra("img",it[position].img)
                            intent.putExtra("info",it[position].info)
                            intent.putExtra("name",it[position].name)
                            startActivity(intent)
                        }else{
                            val intent = Intent(this@MainActivity,LocationsActivity::class.java)
                            intent.putExtra("img",globalList[position].img)
                            intent.putExtra("info",globalList[position].info)
                            intent.putExtra("name",globalList[position].name)
                            startActivity(intent)
                        }
                }
            })
        })
    }
}