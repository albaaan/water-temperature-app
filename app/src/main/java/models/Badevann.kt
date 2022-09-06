package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class Badevann(
        @SerializedName("header")
        val header: BadevannHeader,
        @SerializedName("observations")
        val observations: List<BadevannObs>
)