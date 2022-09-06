package ui

import adapter.BadestedAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.team3.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import data.ApiCaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.Badevann
import utils.OnClickListener
import utils.tidUiUtil
import java.util.*

class MainActivity : AppCompatActivity(), OnClickListener {

    lateinit var badevannListe: List<Badevann>
    lateinit var adapter : BadestedAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Oppretter og kaller tidUiUtil for å sette bakgrunn og ukedags-textviewet
        val layout = findViewById<ConstraintLayout>(R.id.mainLayout)
        val tidUiUtil  = tidUiUtil(this, layout)
        tidUiUtil.setMain()

        //Henter BottomNavigationView via id og gjør den til en variabel:
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomnav)

        /* Gir Bottom Nav funksjoner: */
        bottomNav.setOnNavigationItemSelectedListener {
            //While loekke for hva brukeren klikker paa:
            when (it.itemId) {
                //Main: Ikke noe fordi man er allerede paa hjem
                R.id.mainActivity -> {
                }
                //Favorite: Sender brukeren til FavoriteActivity
                R.id.favoriteActivity -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    overridePendingTransition(0,0)
                    //Fjerner prev. aktivitet for aa hindre flere aktiviteter aapne:
                    finishAffinity()
                }
            }
            false
        }

        //Setter opp SearchView logikken
        val searchView = findViewById<SearchView>(R.id.searchView)
        //Setter en listener som venter til man skriver inn noe i searchviewet
        searchView.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextChange(newText: String): Boolean {
                //Når bruker endrer på det som er skrevet inn skal man filtrere listen basert på det brukeren skrev inn.
                //Får å oppnå dette opprettes det en ny liste, før man så itererer over alle badestedene og ser hvilket navn som passer med det brukeren skrev inn og legger det til i den nye listen.
                //Den nye listen blir så brukt til å oppdatere adapteren sitt datasett.
                filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })
        //Bruker coroutine for å gjennomføre et api-kall asynkront
        CoroutineScope(Dispatchers.IO).launch {
            val apiCaller = ApiCaller()
            val base = apiCaller.testKall()
            badevannListe = base?.data?.tseries!!
            //Test:
            Log.d("API Fetching", base.toString())

            adapter = BadestedAdapter(applicationContext, badevannListe, this@MainActivity)

            //Oppretter en recyclerview:
            runOnUiThread {
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
                recyclerView.setLayoutManager(LinearLayoutManager(applicationContext))
                recyclerView.setAdapter(adapter)
            }
        }
    }


    //Metoden kalles på fra searchviewet
    fun filter(text : String){
        val foresporsel = text.toLowerCase(Locale.getDefault())
        val filteredList = mutableListOf<Badevann>()

        badevannListe.forEach{
            //Hvis badevannet sitt navn inneholder det bruker søker etter så legges det til listen
            if (it.header.extra.name.toLowerCase(Locale.getDefault()).contains(foresporsel)){
                Log.d("ADD ITEM", it.header.extra.name)
                filteredList.add(it)
            }
        }
        if (filteredList.isEmpty()){

            CoroutineScope(Dispatchers.IO).launch {
                adapter = BadestedAdapter(applicationContext, filteredList, this@MainActivity)

                //Recyclerview for searchviewet:
                runOnUiThread {
                    val rv = findViewById<RecyclerView>(R.id.recyclerview)
                    rv.layoutManager = LinearLayoutManager(applicationContext)
                    rv.adapter = adapter
                    Log.d("UPDATING RV", "oppdaterer")
                    adapter.filterList(filteredList)
                }
            }
        }
        else{
            //Hvis ingen av badevann-objektene har et navn som stemmer med det bruker skrev inn så opprettes det en Toast som informerer bruker om dette
            CoroutineScope(Dispatchers.IO).launch {
                adapter = BadestedAdapter(applicationContext, filteredList, this@MainActivity)

                runOnUiThread {
                    val rv = findViewById<RecyclerView>(R.id.recyclerview)
                    rv.layoutManager = LinearLayoutManager(applicationContext)
                    rv.adapter = adapter
                    Log.d("UPDATING RV", "oppdaterer")
                    adapter.filterList(filteredList)
                }
            }
        }
    }


    //Lager en funksjon som videresender brukeren til DetailActivity etter aa ha klikket paa en spesifikk badested:
    override fun onItemClicked(position: Int) {
        //Oppretter en toast for aa fortelle brukeren hva han/hun har valgt:
        Toast.makeText(this, "Valgt: " + badevannListe[position].header.extra.name, Toast.LENGTH_SHORT).show()
        //Oppretter en intent som lager en ny aktivitet (Videresende brukeren fra Main- til DetailActivity:
        val intent = Intent(this, DetailActivity::class.java)
        //Sender informasjon om badesteder som navn og temperatur fra MainActivity til DetailActivity:
        intent.putExtra("name", badevannListe[position].header.extra.name)
        intent.putExtra("temp", badevannListe[position].observations.get(badevannListe[position].observations.size-1).body.value)
        //Starter aktiviteten:
        startActivity(intent)
    }
}
