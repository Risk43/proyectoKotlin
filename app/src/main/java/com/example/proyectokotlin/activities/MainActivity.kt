package com.example.proyectokotlin.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.example.proyectokotlin.models.Locations
import com.example.proyectokotlin.models.LocationsAdapter
import com.example.proyectokotlin.models.MainViewModel
import com.example.proyectokotlin.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
enum class ProviderType{
    EMAIL_PASSWORD
}

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var favorite: Boolean? = false

    private lateinit var toogle: ActionBarDrawerToggle
    private lateinit var rvLocations: RecyclerView
    private lateinit var adapter: LocationsAdapter
    private lateinit var svSearch: SearchView
    private var globalList = mutableListOf<Locations>()
    private val viewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        adapter = LocationsAdapter(this)
        rvLocations = findViewById(R.id.rvLocations)
        svSearch = findViewById(R.id.svSearch)



        rvLocations.layoutManager = LinearLayoutManager(this)
        rvLocations.adapter = adapter

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider", provider)
        prefs.apply()

        val prefsRec = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val session = prefsRec.getString("email", null)

        if (session != null) {
            modules(session)
        }
        globalList.clear()


    }

    fun modules(session: String){
        showMenu(session)
        observeData(session)
    }

    private fun observeData(session: String){
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

            adapter.setOnItemClickListener(object : LocationsAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                        if (globalList.isEmpty()){
                            val intent = Intent(this@MainActivity, LocationsActivity::class.java)
                            intent.putExtra("img",it[position].img)
                            intent.putExtra("info",it[position].info)
                            intent.putExtra("name",it[position].name)
                            if (favorite != null){
                                intent.putExtra("favorite", getFavoriteLocation(session, it[position].name))
                            }
                            intent.putExtra("email", session)
                            intent.putExtra("provider", ProviderType.EMAIL_PASSWORD)
                            startActivity(intent)
                        }else{
                            val intent = Intent(this@MainActivity, LocationsActivity::class.java)
                            intent.putExtra("img",globalList[position].img)
                            intent.putExtra("info",globalList[position].info)
                            intent.putExtra("name",globalList[position].name)
                            if (favorite != null){
                                intent.putExtra("favorite", getFavoriteLocation(session, globalList[position].name))
                            }
                            intent.putExtra("email", session)
                            intent.putExtra("provider", ProviderType.EMAIL_PASSWORD)
                            startActivity(intent)
                        }
                }
            })
        })
    }

    private fun getFavoriteLocation(session: String, name: String): Boolean?{
        FirebaseFirestore.getInstance().collection("users").document(session).collection("favorites").document(name).get().addOnSuccessListener { document ->
            if (document != null){
                favorite = document.data?.get("favorite") as Boolean?
            }else{
                favorite = false
            }
        }
        return favorite
    }

    private fun showMenu(session: String){

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navView)

        findViewById<ImageView>(R.id.ivMenu).setOnClickListener(View.OnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        })

        toogle = ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_profile -> goProfile(session, ProviderType.EMAIL_PASSWORD)
                R.id.nav_fav -> goFavorites(session, ProviderType.EMAIL_PASSWORD)
                R.id.nav_exit -> singOut()
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

    private fun goProfile(session: String, provider: ProviderType){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("email", session)
            intent.putExtra("provider", provider.name)
            startActivity(intent)
    }

    private fun goFavorites(session: String, provider: ProviderType){
        val intent = Intent(this, FavoritesActivity::class.java)
        intent.putExtra("email", session)
        intent.putExtra("provider", provider.name)
        startActivity(intent)
    }

    private fun singOut(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}


