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

class FavorittAdapter(val context: Context, val badelist : List<Badevann>, private val onClickListener: OnClickListener): RecyclerView.Adapter<FavorittAdapter.ViewHolder>(), Filterable {
    var data : MutableList<Badevann> = badelist as MutableList<Badevann>

    //Henter den lagrede listen, hvis det ikke er lagret noen liste enda så henter man en tom streng
    lateinit var favorittListe : MutableList<Badevann>
    val pref = context.getSharedPreferences("BADE_PREFS", Context.MODE_PRIVATE)!!
    @SuppressLint("CommitPrefEdits")
    val editor = pref.edit()!!
    val gson = Gson()
    lateinit var result : String

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val navn = itemView.findViewById<TextView>(R.id.badested)!!
        val vannTemp = itemView.findViewById<TextView>(R.id.vanntemperatur)!!
        val favoritt = itemView.findViewById<ImageButton>(R.id.favorite)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.badelist, parent, false)
        val json = pref.getString("favoritter", "")
        if (json.equals("")){
            favorittListe = mutableListOf()
        }
        else{
            val listType = object : TypeToken<List<Badevann>>() {}.type
            favorittListe = gson.fromJson(json, listType)
        }
        return ViewHolder(cardView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBadevann = data.get(position)
        val name = currentBadevann.header.extra.name
        val value = currentBadevann.observations.get(currentBadevann.observations.size -1).body.value
        holder.navn.text = name
        holder.vannTemp.text = "$value ℃"
        holder.itemView.setOnClickListener {
            onClickListener.onItemClicked(position)
        }
        if (favorittListe.contains(currentBadevann)){
            holder.favoritt.tag = "on"
            holder.favoritt.setImageResource(R.drawable.on)
        }
        //Setter en funksjon for å legge badestedet til en favorittliste for å så legge det i sharedpreferences
        holder.favoritt.setOnClickListener{
            if (holder.favoritt.tag.equals("off")){
                holder.favoritt.setImageResource(R.drawable.on)
                holder.favoritt.tag = "on"

                favorittListe.add(currentBadevann)
                result = gson.toJson(favorittListe)
                editor.putString("favoritter", result)
                editor.apply()
            }
            else if (holder.favoritt.tag.equals("on")){
                holder.favoritt.setImageResource(R.drawable.off)
                holder.favoritt.tag = "off"
                favorittListe.remove(currentBadevann)
                result = gson.toJson(favorittListe)
                editor.putString("favoritter", result)
                editor.apply()
                data.clear()
                data.addAll(favorittListe)
                this.notifyDataSetChanged()
            }
        }
    }

    /*
    //Metode som brukes sammen med searchviewet for å filtrere listen basert på navn
    fun filterList(filtrertListe: MutableList<Badevann>){
        data = filtrertListe
        Log.d("Fått ny liste", data.toString())
        notifyDataSetChanged()
    }
    */
    override fun getItemCount(): Int {
        return badelist.size
    }

    override fun getFilter(): Filter {
        return filter
    }
}