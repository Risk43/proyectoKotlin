package com.example.proyectokotlin.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectokotlin.R
import com.example.proyectokotlin.models.Favorites
import com.example.proyectokotlin.models.FavoritesAdapter
import com.example.proyectokotlin.models.LocationsAdapter
import com.example.proyectokotlin.models.MainViewModel
import com.facebook.shimmer.ShimmerFrameLayout

class FavoritesActivity : AppCompatActivity() {
    private lateinit var session: String

    private lateinit var tvEmptyFavs: TextView

    private lateinit var ivBack: ImageView

    private lateinit var rvFavLocations: RecyclerView
    private lateinit var adapter: FavoritesAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        initVars()

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider", provider)
        prefs.apply()

        val prefsRec = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        session = prefsRec.getString("email", null).toString()

        initComponnets(session, ProviderType.EMAIL_PASSWORD)
    }

    private fun initVars(){
        tvEmptyFavs = findViewById(R.id.tvEmptyFavs)

        adapter = FavoritesAdapter(this)
        rvFavLocations = findViewById(R.id.rvFavLocations)
        rvFavLocations.layoutManager = LinearLayoutManager(this)
        rvFavLocations.adapter = adapter

        tvEmptyFavs.visibility = View.INVISIBLE
    }

    private fun initComponnets(session: String, provider: ProviderType){
        observeData(session)
        goMain(session, provider)
    }

    private fun observeData(session: String){
        var container: ShimmerFrameLayout = findViewById(R.id.shimmer_view_container_fav)
        container.startShimmer()

        viewModel.fetchFavoriteData(session).observe(this, Observer {
            if (it.isNotEmpty()){
                container.visibility = View.GONE
                adapter.setListData(it)
                adapter.notifyDataSetChanged()

                clickItems(it)
            }else{
                container.visibility = View.GONE
                tvEmptyFavs.visibility = View.VISIBLE
            }
        })
    }

    private fun clickItems(it: MutableList<Favorites>){
        adapter.setOnItemClickListener(object: FavoritesAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@FavoritesActivity, LocationsActivity::class.java)
                intent.putExtra("img",it[position].img)
                intent.putExtra("info",it[position].info)
                intent.putExtra("name",it[position].name)
                intent.putExtra("favorite",it[position].fav)
                intent.putExtra("email", session)
                intent.putExtra("provider", ProviderType.EMAIL_PASSWORD)
                startActivity(intent)
            }
        })

    }

    private fun goMain(session: String, provider: ProviderType) {
        ivBack = findViewById(R.id.ivBackFav)
        ivBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", session)
            intent.putExtra("provider", provider.name)
            startActivity(intent)
        }
    }
}