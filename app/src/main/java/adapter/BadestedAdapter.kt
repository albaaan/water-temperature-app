@file:Suppress("RecursivePropertyAccessor")

package adapter


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.team3.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import models.Badevann
import utils.OnClickListener

/* Dette er adapteren som brukes i MainActivity, den har en liste med Badevann-objekter, og bruker dataen fra dem for å lage CardViews som settes i RecyclerViewet til MainActivity
 * Adapteren henter en referanse til SharedPreferences for å kunne sette badesteder inn i favorittlisten
 */

class BadestedAdapter(val context: Context, val badelist : List<Badevann>, private val onClickListener: OnClickListener): RecyclerView.Adapter<BadestedAdapter.ViewHolder>(), Filterable {
    //Listen med badesteder som skal brukes for å opprette CardViews
    var data : MutableList<Badevann> = badelist as MutableList<Badevann>
    //Listen med favoritt-badesteder som hentes fra SharedPreferences
    lateinit var favorittListe : MutableList<Badevann>
    //Henter SharedPreferences
    val pref = context.getSharedPreferences("BADE_PREFS", Context.MODE_PRIVATE)!!
    @SuppressLint("CommitPrefEdits")
    //Editor for SharedPreferences verdiene
    val editor = pref.edit()!!
    //gson for å deserialisere favorittlisten fra en string til et objekt, siden favorittlisten lagres som streng i SharedPrefs.
    val gson = Gson()
    //verdi som skal bli Favorittlisten gjort om til en streng før den puttes inn i SharedPreferences
    lateinit var result : String

    //ViewHolderen har variabler som tilhører Viewsene i badelist.xml for å kunne sette innholdet til Viewsene
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        //navnet på badestedet
        val navn = itemView.findViewById<TextView>(R.id.badested)!!
        //Temperaturen til badestedet
        val vannTemp = itemView.findViewById<TextView>(R.id.vanntemperatur)!!
        //Knappen til CardViewet som angir om det er en favoritt eller ikke
        val favoritt = itemView.findViewById<ImageButton>(R.id.favorite)!!
    }

    //Når ViewHolderen lages henter den favorittlisten som er lagret i SharedPrefs og sjekker om det er opprettet en favorittliste eller ikke.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.badelist, parent, false)
        //henter favorittlisten i form av string
        val json = pref.getString("favoritter", "")
        //Hvis den er tom blir favorittListe en ny liste
        if (json.equals("")){
            favorittListe = mutableListOf()
        }
        //hvis ikke brukes gson for å gjøre om fra string til en liste med badevann
        else{
            val listType = object : TypeToken<List<Badevann>>() {}.type
            favorittListe = gson.fromJson(json, listType)
        }
        return ViewHolder(cardView)
    }

    @SuppressLint("SetTextI18n")
    //I denne funksjonen binder man verdiene fra listen med badevann til ViewHolderen for at det skal vises i form av et CardView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //henter et objekt fra listen med badevann
        val currentBadevann = data.get(position)
        //Henter de relevante verdiene, navnet til badestedet og den siste observasjonen
        val name = currentBadevann.header.extra.name
        val value = currentBadevann.observations.get(currentBadevann.observations.size -1).body.value
        //Setter verdiene
        holder.navn.text = name
        holder.vannTemp.text = "$value ℃"
        //Setter en onClickListener på CardViewet, dette brukes for når man skal komme til DetailActivity
        holder.itemView.setOnClickListener {
            onClickListener.onItemClicked(position)
        }
        //hvis listen med favoritter inneholder badestedet skal knappen ha on-ressursen, gul stjerne, og taggen være "on"
        if (favorittListe.contains(currentBadevann)){
            holder.favoritt.tag = "on"
            holder.favoritt.setImageResource(R.drawable.on)
        }
        //Hvis ikke skal bilde-knappen ha hvit stjerne som ressurs og ha taggen off.
        else{
            holder.favoritt.tag = "off"
            holder.favoritt.setImageResource(R.drawable.off)
        }
        //Setter en funksjon for å legge badestedet til en favorittliste for å så legge det i sharedpreferences
        holder.favoritt.setOnClickListener{
            //hvis tag er off er ikke badestedet i favorittlisten, skal derfor legges til
            if (holder.favoritt.tag.equals("off")){
                //endrer på bildet og tag
                holder.favoritt.setImageResource(R.drawable.on)
                holder.favoritt.tag = "on"
                //legger den til i listen
                favorittListe.add(currentBadevann)
                //gjør listen om til en streng og putter den i Shared Preferences
                result = gson.toJson(favorittListe)
                editor.putString("favoritter", result)
                editor.apply()
            }
            //Hvis tag er on er badestedet i favorittlisten allerede og man må få den ut
            else if (holder.favoritt.tag.equals("on")){
                //endrer på bildet og tag
                holder.favoritt.setImageResource(R.drawable.off)
                holder.favoritt.tag = "off"
                //fjerner badestedet fra listen
                favorittListe.remove(currentBadevann)
                //gjør listen om til en streng og putter den i Shared Preferences
                result = gson.toJson(favorittListe)
                editor.putString("favoritter", result)
                editor.apply()
            }
        }
    }

    //Metode som brukes sammen med searchviewet for å filtrere listen basert på navn
    //metoden tar inn en liste med badevann og setter datasettet til adapteren
    fun filterList(filtrertListe: MutableList<Badevann>){
        data = filtrertListe
        Log.d("Fått ny liste", data.toString())
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return badelist.size
    }

    override fun getFilter(): Filter {
        return filter
    }
}