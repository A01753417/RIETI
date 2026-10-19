package tec.spn.rieti.model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

/**
 * Helper object for handling device location and geocoding.
 */
object DeviceLocation {
    private const val LOCATION_TIMEOUT_MS = 15_000L

    /**
     * Checks if the app has permission to access device location.
     *
     * @param context The application context.
     * @return True if fine or coarse location permission is granted, false otherwise.
     */
    fun hasPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    /**
     * Gets the current location of the device.
     *
     * @param context The application context.
     * @return A GeoPoint representing the current latitude and longitude, or null if it fails or times out.
     */
    suspend fun getCurrentLocation(context: Context): GeoPoint? {
        val client = LocationServices.getFusedLocationProviderClient(context)

        return withTimeoutOrNull(LOCATION_TIMEOUT_MS.milliseconds) {
            suspendCancellableCoroutine { continuation ->
                val cancellation = CancellationTokenSource()
                continuation.invokeOnCancellation { cancellation.cancel() }

                try {
                    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellation.token)
                        .addOnSuccessListener { location ->
                            if (continuation.isActive) {
                                continuation.resume(location?.let { GeoPoint(it.latitude, it.longitude) })
                            }
                        }
                        .addOnFailureListener {
                            if (continuation.isActive) continuation.resume(null)
                        }
                } catch (e: SecurityException) {
                    if (continuation.isActive) continuation.resume(null)
                }
            }
        }
    }

    /**
     * Converts latitude and longitude coordinates into a readable address string.
     *
     * @param context The application context.
     * @param point The GeoPoint containing the latitude and longitude.
     * @return A string with the address, or null if it cannot be found.
     */
    suspend fun reverseGeocode(context: Context, point: GeoPoint): String? {
        if (!Geocoder.isPresent()) return null
        val geocoder = Geocoder(context, Locale.forLanguageTag("es-MX"))

        return try {
            val results: List<Address>? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(
                            point.latitude,
                            point.longitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: MutableList<Address>) {
                                    if (continuation.isActive) continuation.resume(addresses)
                                }

                                override fun onError(errorMessage: String?) {
                                    if (continuation.isActive) continuation.resume(null)
                                }
                            }
                        )
                    }
                } else {
                    withContext(Dispatchers.IO) {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(point.latitude, point.longitude, 1)
                    }
                }

            results?.firstOrNull()?.getAddressLine(0)?.takeIf { it.isNotBlank() }
        } catch (e: IOException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}