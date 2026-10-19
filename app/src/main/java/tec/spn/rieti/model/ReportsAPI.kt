package tec.spn.rieti.model

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Request body for checking the status of a report using its folio.
 *
 * @property folio The unique folio identifier of the report.
 */
data class StatusRequest(
    @SerializedName("folio") val folio: String
)

/**
 * Response received when checking a report's status.
 *
 * @property success Indicates whether the request was successful.
 * @property estatus The current status of the report.
 * @property fecha The creation date and time of the report.
 */
data class StatusResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("estatus") val estatus: String?,
    @SerializedName("fecha") val fecha: String?
)

/**
 * API interface for checking report status endpoints.
 */
interface ReportStatusApi {
    @POST(".")
    suspend fun getStatus(@Body request: StatusRequest): StatusResponse
}

/**
 * Request body sent to the backend when creating a new report.
 */
data class CreateReportRequest(
    @SerializedName("isAnonymous") val isAnonymous: Boolean,
    @SerializedName("reporterName") val reporterName: String?,
    @SerializedName("reporterEmail") val reporterEmail: String?,
    @SerializedName("reporterPhone") val reporterPhone: String?,
    @SerializedName("childrenCount") val childrenCount: String,
    @SerializedName("childrenAges") val childrenAges: List<String>,
    @SerializedName("workType") val workType: String,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("observations") val observations: String,
    @SerializedName("isEmergency") val isEmergency: Boolean,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("address") val address: String
)

/**
 * Response received after successfully creating a new report.
 *
 * @property success Indicates whether the creation was successful.
 * @property folio The generated folio assigned to the new report.
 */
data class CreateReportResponse(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("folio") val folio: String?
)

/**
 * API interface for submitting new reports.
 */
interface ReportSubmitApi {
    @POST(".")
    suspend fun createReport(@Body request: CreateReportRequest): CreateReportResponse
}