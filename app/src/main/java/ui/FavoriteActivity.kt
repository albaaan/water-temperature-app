package ui


import adapter.FavorittAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team3.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.Badevann
import utils.OnClickListener
import utils.tidUiUtil

class FavoriteActivity : AppCompatActivity(), OnClickListener {

    lateinit var favorittListe : MutableList<Badevann>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        //Tom Actionbar (Ingen tittel):
        title = ""

        val layout = findViewById<ConstraintLayout>(R.id.favoriteLayout)
        val tidUiUtil  = tidUiUtil(this, layout)
        tidUiUtil.setFavorites()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomnav)

        /* Gir Bottom Nav funksjoner: */
        bottomNav.setOnNavigationItemSelectedListener {
            //While loekke for hva brukeren klikker paa:
            when (it.itemId) {
                //Main: Sender brukeren til Main
                R.id.mainActivity -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0,0)
                    //Fjerner prev. aktivitet for aa hindre flere aktiviteter aapne:
                    finishAffinity()
                }
                //Favorite: Ingen ting fordi man er allerede paa Favorite
                R.id.favoriteActivity -> {
                }
            }
            false
        }

        val rv = findViewById<RecyclerView>(R.id.favoritesRecycler)
        val noData = findViewById<TextView>(R.id.empty_view)

        //Prøver å hente listen med favoritt badesteder fra Shared Preferences
        val pref = getSharedPreferences("BADE_PREFS", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = pref.getString("favoritter", "")
        if (json.equals("")){
            //Hvis favorittlisten er tom skal recyclerviewet bli usynlig og textviewet med meldingen om at recyclerviewet er tomt skal gjøres synlig
            rv.visibility = View.GONE
            noData.visibility = View.VISIBLE
        }
        else{
            rv.visibility = View.VISIBLE
            noData.visibility = View.GONE
            val listType = object : TypeToken<List<Badevann>>() {}.type
            favorittListe = gson.fromJson(json, listType)
        }

        //Gjennomfører ikke noe api-kall, oppdaterer kun aktiviteten med badestedene som er i FavorittListe.kt
        CoroutineScope(Dispatchers.IO).launch {
            val adapter = FavorittAdapter(applicationContext, favorittListe, this@FavoriteActivity)

            runOnUiThread {
                //val recyclerView = findViewById<RecyclerView>(R.id.favoritesRecycler)
                rv.layoutManager = LinearLayoutManager(applicationContext)
                rv.adapter = adapter
            }
        }
    }

    //Lager en funksjon som videresender brukeren til DetailActivity etter aa ha klikket paa en spesifikk badested:
    override fun onItemClicked(position: Int) {
        //Oppretter en toast for aa fortelle brukeren hva han/hun har valgt:
        Toast.makeText(this, "Valgt: " + favorittListe[position].header.extra.name, Toast.LENGTH_SHORT).show()
        //Oppretter en intent som lager en ny aktivitet (Videresende brukeren fra Favorite- til DetailActivity:
        val intent = Intent(this, DetailActivity::class.java)
        //Sender informasjon om badesteder som navn og temperatur fra FavoriteActivity til DetailActivity:
        intent.putExtra("name", favorittListe[position].header.extra.name)
        intent.putExtra("temp", favorittListe[position].observations.get(favorittListe[position].observations.size-1).body.value)
        //Starter aktiviteten:
        startActivity(intent)
    }
}