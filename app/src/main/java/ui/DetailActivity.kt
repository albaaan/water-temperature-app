@file:Suppress("ControlFlowWithEmptyBody")

package ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.team3.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import utils.tidUiUtil
import java.util.*

class DetailActivity: AppCompatActivity() {
    lateinit var name : String
    lateinit var layout: ConstraintLayout
    lateinit var tidUiUtil: tidUiUtil
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail)
        //Tom Actionbar (Ingen tittel):
        title = ""

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomnav)

        /* Gir Bottom Nav funksjoner: */
        bottomNav.setOnNavigationItemSelectedListener {
            //While loekke for hva brukeren klikker paa:
            when (it.itemId) {
                //Main: Sender brukeren til Main
                R.id.mainActivity -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0,0)
                    true
                }
                //Favorite: Sender brukeren til Favoritter
                R.id.favoriteActivity -> {
                    startActivity(Intent(this, FavoriteActivity::class.java))
                    overridePendingTransition(0,0)
                    true
                }
            }
            false
        }

        val kalender = Calendar.getInstance()

        //Henter referanse til detailactivity sin bakgrunn
        layout = findViewById(R.id.detailLayout)
        title = ""
        //Oppretter og kaller tidUiUtil for å sette bakgrunnen til aktiviteten.
        tidUiUtil = tidUiUtil(this, layout)
        tidUiUtil.setDetail()

        //Henter string som skal bli videresendt fra MainActivity via intent:
        name = intent.getStringExtra("name").toString()
        val value = intent.getStringExtra("temp").toString()

        //Oppretter variabler som henter inn id-ene fra detail.xml:
        val nameSrc = findViewById<TextView>(R.id.badested)
        val valueSrc = findViewById<TextView>(R.id.vanntemperatur)
        val badeSrc = findViewById<TextView>(R.id.antallBad)

        //Henter verdien fra SharedPreferences
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val defaultValue = sharedPref.getInt(name, 0)

        //Connecter variablene med StringExtra fra Intent:
        nameSrc.text = name
        valueSrc.text = "$value ℃"
        badeSrc.text = defaultValue.toString()
    }


    //Funksjonen som kalles når bruker trykker på 'Registrer bad' knappen
    fun registrer(view: View){
        val badeSrc = findViewById<TextView>(R.id.antallBad)
        if (badeSrc.text.toString().equals("")){
        }
        else{
            val antall = badeSrc.text.toString().toInt()
            val ny = antall+1
            badeSrc.text = ny.toString()
            //Lagrer den nye verdien i SharedPreferences, med badestedsnavnet som nøkkel og antall bad som verdi
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putInt(name, ny)
                apply()
            }
            //TEST: Prøver å lese fra shared preferences.
            val defaultValue = sharedPref.getInt(name, 0)
            Log.d("BUTTON PRESS", "Knapp trykket")
            Log.d("TESTET SHAREDPREF", defaultValue.toString())
        }
    }
}