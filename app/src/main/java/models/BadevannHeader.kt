package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class BadevannHeader(
        @SerializedName("id")
        val id: BadevannId,
        @SerializedName("extra")
        val extra: BadevannExtra
)