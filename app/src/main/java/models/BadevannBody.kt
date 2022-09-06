package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class BadevannBody(
        @SerializedName("value")
        val value: String
)