package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class BadevannPos(
        @SerializedName("lon")
        val lon: String,
        @SerializedName("lat")
        val lat: String
)
