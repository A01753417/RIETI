package tec.spn.rieti.model

import kotlin.coroutines.cancellation.CancellationException

/**
 * Repository responsible for fetching statistics data from the API.
 *
 * @property api API interface for retrieving statistics.
 */
class StatsRepository(
    private val api: StatsApi = RetrofitClient.statsApi
) {
    /**
     * Fetches overall statistics from the backend.
     *
     * @return A Result containing the StatsResponse if successful, or an exception if it fails.
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