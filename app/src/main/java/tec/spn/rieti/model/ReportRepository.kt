package tec.spn.rieti.model

import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

class ReportNotFoundException : Exception("Report not found")

/**
 * Repository responsible for fetching query result data from API
 *
 * @param api The Retrofit [ReportsApi] client used to perform network requests
 */
class ReportRepository(
    private val api: ReportsApi = RetrofitClient.reportsApi
) {
    /**
     * Fetches report data asynchronously, handles exceptions and wraps the outcome in a [Result]
     *
     * @param folio The folio that the user searched for
     * @return A [Result] containing [Report]
     */
    suspend fun getReport(folio: String): Result<Report> {
        return try {
            val response = api.getReport(folio)

            val status = ReportStatus.fromApi(response.status)
                ?: return Result.failure(
                    IllegalStateException("Unknown status from backend: ${response.status}")
                )

            Result.success(
                Report(
                    folio = response.folio,
                    status = status,
                    //createdAt = response.createdAt
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
}
