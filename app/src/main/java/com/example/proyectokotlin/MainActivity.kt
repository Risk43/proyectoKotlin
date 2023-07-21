package com.example.proyectokotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.newFixedThreadPoolContext
import java.util.Locale


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var toogle: ActionBarDrawerToggle
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


        showMenu()
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

    private fun showMenu(){

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navView)

        findViewById<ImageView>(R.id.ivMenu).setOnClickListener(View.OnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        })

        toogle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_profile -> Toast.makeText(applicationContext,"Perfil",Toast.LENGTH_SHORT).show()
                R.id.nav_fav -> Toast.makeText(applicationContext,"Favoritos",Toast.LENGTH_SHORT).show()
                R.id.nav_exit -> Toast.makeText(applicationContext,"Salir",Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toogle.onOptionsItemSelected(item)){
            return  true
        }

        return super.onOptionsItemSelected(item)
    }
}