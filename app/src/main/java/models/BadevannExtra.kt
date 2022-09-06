package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class BadevannExtra(
        @SerializedName("source")
        val source: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("pos")
        val pos: BadevannPos
)