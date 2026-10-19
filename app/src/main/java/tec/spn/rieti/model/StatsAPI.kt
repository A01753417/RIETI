package tec.spn.rieti.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

interface StatsApi {

    @GET(".")
    suspend fun getStats(): StatsResponse
}
data class StatsResponse(
    @SerializedName("reportsProcessed") val reportsProcessed: Int,
    @SerializedName("minorsProtected") val minorsProtected: Int
)