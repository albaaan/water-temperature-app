package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class BadevannId(
        @SerializedName("buoyID")
        val buoyID: String,
        @SerializedName("parameter")
        val parameter: String
)
