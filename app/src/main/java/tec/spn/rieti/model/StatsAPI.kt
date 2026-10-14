package tec.spn.rieti.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

/**
 * Retrofit API interface defining endpoints to access statistics
 */
interface StatsApi {
    /**
     * Fetches general system statistics from the server
     *
     * @return A [StatsResponse] containing processed reports and protected minors data
     */
    @GET("stats")
    suspend fun getStats(): StatsResponse
}

/**
 * Data model representing the JSON response structure for system statistics
 *
 * @property reportsProcessed Total number of reports processed by the system
 * @property minorsProtected Total number of completed reports
 */
data class StatsResponse(
    @SerializedName("reportsProcessed") val reportsProcessed: Int,
    @SerializedName("minorsProtected") val minorsProtected: Int
)
