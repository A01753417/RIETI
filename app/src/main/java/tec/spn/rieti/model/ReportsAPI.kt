package tec.spn.rieti.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Data model representing the JSON response structure for report query
 *
 * @property folio The folio of the searched report
 * @property status The status of the searched report
 */
data class ReportResponse(
    @SerializedName("folio") val folio: String,
    @SerializedName("status") val status: String,
    //@SerializedName("createdAt") val createdAt: String
)

/**
 * Retrofit API interface defining endpoints to access report status
 */
interface ReportsApi {
    /**
     * Fetches report data from the server
     *
     * @return A [ReportResponse] containing processed reports and protected minors data
     */
    @GET("reports/{folio}")
    suspend fun getReport(@Path("folio") folio: String): ReportResponse
}