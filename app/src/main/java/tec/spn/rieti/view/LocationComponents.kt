package tec.spn.rieti.view

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.map.MapUiOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import tec.spn.rieti.model.DeviceLocation
import tec.spn.rieti.model.GeoPoint

internal const val MapStyleUrl = "https://tiles.openfreemap.org/styles/liberty"
internal val DefaultMapCenter = GeoPoint(19.543288, -99.234779)
private val PinColor = Color(0xFFD62828)

enum class LocationFailure { PermissionDenied, Unavailable }
@Stable
class LocationRequester internal constructor() {
    var isLocating by mutableStateOf(false)
        internal set

    internal var start: () -> Unit = {}
    fun request() {
        if (!isLocating) start()
    }
}

@Composable
fun rememberLocationRequester(
    onLocation: (GeoPoint) -> Unit,
    onFailure: (LocationFailure) -> Unit
): LocationRequester {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val latestOnLocation by rememberUpdatedState(onLocation)
    val latestOnFailure by rememberUpdatedState(onFailure)
    val requester = remember { LocationRequester() }

    val fetchLocation: () -> Unit = {
        requester.isLocating = true
        scope.launch {
            val point = DeviceLocation.getCurrentLocation(context)
            requester.isLocating = false
            if (point != null) latestOnLocation(point) else latestOnFailure(LocationFailure.Unavailable)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) fetchLocation() else latestOnFailure(LocationFailure.PermissionDenied)
    }

    SideEffect {
        requester.start = {
            if (DeviceLocation.hasPermission(context)) {
                fetchLocation()
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    return requester
}

@Composable
fun BoxScope.CenterPin() {
    Icon(
        imageVector = Icons.Filled.LocationOn,
        contentDescription = null,
        tint = PinColor,
        modifier = Modifier
            .align(Alignment.Center)
            .size(48.dp)
            .offset(y = (-24).dp)
    )
}

@Composable
fun LocationMapPreview(
    location: GeoPoint?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(ReportFieldBackground)
    ) {
        if (location == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Toca para elegir la ubicación en el mapa",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                )
            }
        } else {
            val mapState = rememberMapState(
                initialCameraPosition = previewCamera(location),
                baseStyle = BaseStyle.Uri(MapStyleUrl)
            )
            LaunchedEffect(location) { mapState.setCameraPosition(previewCamera(location)) }

            MaplibreMap(
                state = mapState,
                modifier = Modifier.matchParentSize(),
                interactions = MapInteractions.None,
                uiOptions = MapUiOptions.None
            )
            CenterPin()

            if (onClick != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Toca para ajustar",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (onClick != null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClickLabel = "Elegir ubicación en el mapa", onClick = onClick)
            )
        }
    }
}

private fun previewCamera(point: GeoPoint) = CameraPosition(
    center = Position(latitude = point.latitude, longitude = point.longitude),
    zoom = 16.0
)