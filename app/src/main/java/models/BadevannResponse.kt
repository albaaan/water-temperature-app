package models

import com.google.gson.annotations.SerializedName

//Oppretter data fra API-et ved aa serialisere dem:
data class BadevannResponse(
        @SerializedName("tstype")
        val tstype: String,
        @SerializedName("tseries")
        val tseries: List<Badevann>
)