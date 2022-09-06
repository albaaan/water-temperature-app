package utils

import android.app.Activity
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.team3.R
import java.util.*

//SAMUEL : Dette er en klasse som brukes for å sette bakgrunn basert på nåværende klokkeslett
//Den holder også styr på hvilken dag i uken det er og oppdaterer et TextView med dette
//Klassen tar inn en referanse til aktiviteten som opprettet et objekt av klassen og aktiviteten som kalte den sitt layout
//Den har forskjellige funksjoner, der
class tidUiUtil(callerActivity : Activity, callerLayout: ConstraintLayout){
    val activity = callerActivity
    val layout : ConstraintLayout = callerLayout
    val kalender = Calendar.getInstance()

   fun setMain(){
        val dag = this.activity.findViewById<TextView>(R.id.ukedag)
        val kalenderDag = kalender.get(Calendar.DAY_OF_WEEK)

        when (kalenderDag){
            Calendar.MONDAY -> dag.setText(R.string.mandag)
            Calendar.TUESDAY -> dag.setText(R.string.tirsdag)
            Calendar.WEDNESDAY -> dag.setText(R.string.onsdag)
            Calendar.THURSDAY -> dag.setText(R.string.torsdag)
            Calendar.FRIDAY -> dag.setText(R.string.fredag)
            Calendar.SATURDAY -> dag.setText(R.string.lørdag)
            Calendar.SUNDAY -> dag.setText(R.string.søndag)
        }

        //Henter hvilken time det er fra kalender, bruker så en mengde med if-tester for å sette bakgrunnen
        val tid = kalender.get(Calendar.HOUR_OF_DAY)
        if (tid in 4..11){
            layout.setBackgroundResource(R.drawable.morning)
        }
        else if (tid in 12..21){
            layout.setBackgroundResource(R.drawable.afternoon)
        }
        else if (tid in 22..24){
            layout.setBackgroundResource(R.drawable.night)
        }
        else if (tid in 1..3){
            layout.setBackgroundResource(R.drawable.night)
        }
    }

    //Detaljeaktiviteten har en annerledes bakgrunn enn main og favoritt. Siden den ikke har en oversikt over klokkeslett eller dag trenger den kun å oppdatere bakgrunnsressursen
     fun setDetail(){
        val tid = kalender.get(Calendar.HOUR_OF_DAY)
        if (tid in 4..11){
            layout.setBackgroundResource(R.drawable.detail_morning)
        }
        else if (tid in 12..21){
            layout.setBackgroundResource(R.drawable.detail_afternoon)
        }
        else if (tid in 22..24){
            layout.setBackgroundResource(R.drawable.detail_night)
        }
        else if (tid in 1..3){
            layout.setBackgroundResource(R.drawable.detail_night)
        }
    }

    //Favorittaktiviteten har ikke noen oversikt over klokkeslett eller dag så den skal kun oppdatere bakgrunnen basert på tid. Innholdet i funksjonen er for nå helt identisk til setDetail
    //men de er separert i forskjellige funksjoner i tilfelle man ønsker å endre på aktivitetene
     fun setFavorites(){
        val tid = kalender.get(Calendar.HOUR_OF_DAY)
        if (tid in 4..11){
            layout.setBackgroundResource(R.drawable.morning)
        }
        else if (tid in 12..21){
            layout.setBackgroundResource(R.drawable.afternoon)
        }
        else if (tid in 22..24){
            layout.setBackgroundResource(R.drawable.night)
        }
        else if (tid in 1..3){
            layout.setBackgroundResource(R.drawable.night)
        }
    }
}