package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class BadevannObs(
        @SerializedName("time")
        val time: String,
        @SerializedName("body")
        val body: BadevannBody
)
