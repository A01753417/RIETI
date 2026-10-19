package tec.spn.rieti.model

import retrofit2.HttpException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.cancellation.CancellationException

/**
 * Exception thrown when a requested report cannot be found.
 */
class ReportNotFoundException : Exception("Report not found")

/**
 * Repository responsible for handling network requests related to report status and submission.
 *
 * @property statusApi API interface for checking report status.
 * @property submitApi API interface for submitting new reports.
 */
class ReportRepository(
    private val statusApi: ReportStatusApi = RetrofitClient.reportStatusApi,
    private val submitApi: ReportSubmitApi = RetrofitClient.reportSubmitApi
) {

    /**
     * Retrieves a report by its folio number.
     *
     * @param folio The unique folio identifier of the report.
     * @return A Result containing the Report if successful, or an exception if it fails.
     */
    suspend fun getReport(folio: String): Result<Report> {
        return try {
            val cleanFolio = folio.trim().uppercase()
            val response = statusApi.getStatus(StatusRequest(cleanFolio))
            if (!response.success) return Result.failure(ReportNotFoundException())

            val status = ReportStatus.fromApi(response.estatus.orEmpty())
                ?: return Result.failure(
                    IllegalStateException("Unknown status from backend: ${response.estatus}")
                )

            Result.success(
                Report(
                    folio = cleanFolio,
                    status = status,
                    createdAt = formatCreatedAt(response.fecha)
                )
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            if (e.code() == 404) {
                Result.failure(ReportNotFoundException())
            } else {
                Result.failure(e)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Submits a new report draft to the backend.
     *
     * @param draft The ReportDraft containing all the information entered by the user.
     * @return A Result containing the generated folio string if successful, or an exception if it fails.
     */
    suspend fun submitReport(draft: ReportDraft): Result<String?> {
        return try {
            val response = submitApi.createReport(draft.toRequest())
            Result.success(response.folio?.takeIf { it.isNotBlank() })
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Converts a ReportDraft into a CreateReportRequest object ready for network submission.
 */
private fun ReportDraft.toRequest(): CreateReportRequest {
    val point = location ?: error("Location is required")
    val type = workType ?: error("Work type is required")
    val count = childrenCount ?: error("Children count is required")

    return CreateReportRequest(
        isAnonymous = isAnonymous,
        reporterName = if (isAnonymous) null else name.trim(),
        reporterEmail = if (isAnonymous) null else email.ifEmpty { null },
        reporterPhone = if (isAnonymous) null else phone.ifEmpty { null },
        childrenCount = count.apiValue,
        childrenAges = childrenAges.map { it.apiValue },
        workType = type.apiValue,
        startTime = startTime.toApiString(),
        endTime = endTime.toApiString(),
        observations = observations.trim(),
        isEmergency = isEmergency,
        latitude = point.latitude,
        longitude = point.longitude,
        address = address.trim()
    )
}

private fun formatCreatedAt(iso: String?): String {
    if (iso.isNullOrBlank()) return "—"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = parser.parse(iso.take(19)) ?: return iso
        SimpleDateFormat("d 'de' MMMM 'de' yyyy, h:mm a", Locale.forLanguageTag("es-MX")).format(date)
    } catch (e: ParseException) {
        iso
    }
}