package tec.spn.rieti.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Object responsible for configuring and providing Retrofit service for connectivity with the backend
 */
object RetrofitClient {
    /** Base URL used for network requests */
    private const val BASE_URL = "https://placeholder.com/"

    /** Retrofit instance */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Implementation of the [StatsApi] interface. */
    val statsApi: StatsApi by lazy { retrofit.create(StatsApi::class.java) }

    val reportsApi: ReportsApi by lazy { retrofit.create(ReportsApi::class.java) }
}
