package tec.spn.rieti.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java
/**
 * Singleton object that provides configured Retrofit clients for network API calls.
 */
object RetrofitClient {

    private const val STATS_URL = "https://izxptav6puupl2fptym7g6svby0iltbs.lambda-url.us-east-1.on.aws/"
    private const val STATUS_URL = "https://it2duaov3fmvh4gb2mcfsuhq340mfomb.lambda-url.us-east-1.on.aws/"
    private const val SUBMIT_URL = "https://kplmqxxqrwx5mpz2ap3ss3c5cq0whirt.lambda-url.us-east-1.on.aws/"

    /**
     * Creates and configures a Retrofit instance for a given base URL.
     *
     * @param baseUrl The base URL for the API.
     * @return A configured Retrofit instance.
     */
    private fun retrofitFor(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val statsApi: StatsApi by lazy { retrofitFor(STATS_URL).create(StatsApi::class.java) }

    val reportStatusApi: ReportStatusApi by lazy { retrofitFor(STATUS_URL).create(ReportStatusApi::class.java) }

    val reportSubmitApi: ReportSubmitApi by lazy { retrofitFor(SUBMIT_URL).create(ReportSubmitApi::class.java) }
}