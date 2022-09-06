package data

import android.annotation.SuppressLint
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitString
import com.google.gson.Gson
import models.Base
import java.text.SimpleDateFormat
import java.util.*

//Interface for å gjennomføre et API-kall til havvarsel
interface ApiService {
    @SuppressLint("SimpleDateFormat")
    suspend fun testKall(): Base? {
        val gson = Gson()
        //Eksempel på hvordan api søk linken vil se ut:
        //val path ="http://havvarsel-frost.met.no/api/v1/obs/badevann/get?time=2021-04-16T00%3A00%3A00Z%2F2021-04-16T23%3A59%3A59Z&incobs=true"

        /*
        Kotlin tillater ikke modeifisering av strenger, altså å bytte en char verdi i en streng, kan
        bare legge til char. Derfor blir vi nødt til å lage 2 strenger som skal bygges opp med
        start intervallet som er en time fra nå og slutt intervallet som er nå.
         */
        var path = "http://havvarsel-frost.met.no/api/v1/obs/badevann/get?time="
        var pathslutt = ""
        // 1000 millisekund * 60 sekunder* 60 minutter * 24 timer = 1 døgn på millisekundform
        // Vi henter fra observasjoner fra de siste 2 døgn for å få med oss mest mulig av ny data
        val startTid = SimpleDateFormat("yyyy-MM-dd#HH.mm.ss").format(Calendar.getInstance().timeInMillis - (1000*60*60*48))
        val sluttTid = SimpleDateFormat("yyyy-MM-dd#HH.mm.ss").format(Calendar.getInstance().time)
        var i = 0
        //Lager en løkke som skal erstatte tegnene i formatet med riktig strenger fra formatet til havvarsel
        while (i < startTid.length) {
            //when er basically switch
            when (startTid[i]) {
                '.' -> {
                    path += "%3A"
                    pathslutt += "%3A"

                } '#' -> {
                    path += "T"
                    pathslutt += "T"

                } else -> {
                    path += startTid[i]
                    pathslutt += sluttTid[i]
                }
            }
            i++
        }
        path += "Z%2F" + pathslutt + "Z&incobs=true"

        //Kaller på API-et med modifiserte path-linken:
        val jsonString = Fuel.get(path).awaitString()
        return gson.fromJson(jsonString, Base::class.java)
    }
}