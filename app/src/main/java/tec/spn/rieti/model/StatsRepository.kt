package tec.spn.rieti.model

import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository responsible for fetching statistics data from API
 *
 * @param api The Retrofit [StatsApi] client used to perform network requests
 */
class StatsRepository(
    private val api: StatsApi = RetrofitClient.statsApi
) {
    /**
     * Fetches system statistics asynchronously, handles exceptions and wraps the outcome in a [Result]
     *
     * @return A [Result] containing [StatsResponse] on success, or an error/exception on failure
     */
    suspend fun getStats(): Result<StatsResponse> =
        try {
            Result.success(api.getStats())
        } catch (e: CancellationException) {

            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}