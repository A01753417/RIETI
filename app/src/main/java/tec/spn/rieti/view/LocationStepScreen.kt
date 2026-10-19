package tec.spn.rieti.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraUpdate
import org.maplibre.compose.interaction.MapInteractions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.rememberMapState
import org.maplibre.compose.style.BaseStyle
import org.maplibre.spatialk.geojson.Position
import tec.spn.rieti.model.DeviceLocation
import tec.spn.rieti.model.GeoPoint
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    initialLocation: GeoPoint?,
    onConfirm: (GeoPoint, String?) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val start = initialLocation ?: DefaultMapCenter

    val mapState = rememberMapState(
        initialCameraPosition = CameraPosition(
            center = Position(latitude = start.latitude, longitude = start.longitude),
            zoom = if (initialLocation != null) 17.0 else 10.0
        ),
        baseStyle = BaseStyle.Uri(MapStyleUrl)
    )

    var address by remember { mutableStateOf<String?>(null) }
    var isResolving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(mapState) {
        snapshotFlow { mapState.isCameraMoving }
            .distinctUntilChanged()
            .collectLatest { moving ->
                isResolving = true
                if (!moving) {
                    delay(300.milliseconds)
                    val center = mapState.cameraPosition.center
                    address = DeviceLocation.reverseGeocode(
                        context,
                        GeoPoint(center.latitude, center.longitude)
                    )
                    isResolving = false
                }
            }
    }

    val locationRequester = rememberLocationRequester(
        onLocation = { point ->
            message = null
            scope.launch {
                mapState.animateCamera(
                    CameraUpdate(
                        center = Position(latitude = point.latitude, longitude = point.longitude),
                        zoom = 17.0
                    )
                )
            }
        },
        onFailure = { failure ->
            message = when (failure) {
                LocationFailure.PermissionDenied ->
                    "Sin permiso de ubicación. Mueve el mapa para colocar el pin manualmente."
                LocationFailure.Unavailable ->
                    "No pudimos obtener tu ubicación. Revisa que el GPS esté activo o mueve el mapa."
            }
        }
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ubicación del caso",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MaplibreMap(
                state = mapState,
                modifier = Modifier.fillMaxSize(),
                interactions = MapInteractions {
                    camera {
                        rotate { enabled = false }
                        pitch { enabled = false }
                    }
                }
            )

            CenterPin()

            SmallFloatingActionButton(
                onClick = { locationRequester.request() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                if (locationRequester.isLocating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.MyLocation,
                        contentDescription = "Usar mi ubicación actual"
                    )
                }
            }

            val currentAddress = address
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Mueve el mapa para colocar el pin en el lugar exacto del caso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Dirección aproximada",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = when {
                            isResolving -> "Buscando dirección…"
                            currentAddress != null -> currentAddress
                            else -> "No se pudo obtener la dirección. Podrás escribirla en el formulario."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    message?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val center = mapState.cameraPosition.center
                            onConfirm(
                                GeoPoint(center.latitude, center.longitude),
                                if (isResolving) null else currentAddress
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ReportDark,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Confirmar ubicación",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}