package tec.spn.rieti.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tec.spn.rieti.viewmodel.StatsUiState
import tec.spn.rieti.viewmodel.StatsVM

/**
 * List of covered municipalities in the State of Mexico where the system is active
 */
private val municipalities = listOf(
    "Atizapán de Zaragoza",
    "Naucalpan de Juárez",
    "Tlanepantla de Baz",
    "Cuautitlán Izcalli",
    "Nicolás Romero",
    "Villa del Carbón",
    "Jilotzingo",
    "Cuatitlán",
    "Tultitlán",
    "Huixquilucan",
    "Isidro Fabela",
    "Tepotzotlán",
    "Coacalco",
    "Ecatepec"
)

/**
 * Screen that handles displaying system statistics and coverage by connecting to [StatsVM]
 *
 * @param onBack Callback triggered when tapping the back button
 * @param modifier Custom [Modifier] for layout styling
 * @param viewModel The [StatsVM] managing the statistics UI state
 */
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatsVM = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatsScreenContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadStats,
        modifier = modifier
    )
}

/**
 * Stateless content container for the statistics screen, handles loading, error, and success states
 *
 * @param uiState Current [StatsUiState] representing data loading status
 * @param onBack Callback invoked when navigating back
 * @param onRetry Callback invoked to reload statistics when an error occurs
 * @param modifier Custom [Modifier] for layout styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreenContent(
    uiState: StatsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Estadísticas",
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Resumen general de reportes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Consulta estadísticas del sistema RIETI. Cada reporte hace la diferencia",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))

            when (uiState) {
                StatsUiState.Loading -> LoadingSection()
                StatsUiState.Error -> ErrorSection(onRetry)
                is StatsUiState.Success -> {
                    StatBlock(
                        value = "%,d".format(uiState.reportsProcessed),
                        label = "Reportes procesados"
                    )
                    Spacer(Modifier.height(24.dp))
                    StatBlock(
                        value = "%,d".format(uiState.minorsProtected),
                        label = "Reportes finalizados"
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Cobertura estatal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "El sistema RIETI opera en los principales municipios del Estado de México, garantizando una cobertura integral de la región",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))

            municipalities.forEach { name ->
                HorizontalDivider()
                MunicipalityRow(name)
            }
            HorizontalDivider()

            Spacer(Modifier.height(16.dp))
        }
    }
}

/**
 * Displays a single large stats block with a numerical value and label
 *
 * @param value Numerical string value
 * @param label Description label explaining the metric
 */
@Composable
private fun StatBlock(value: String, label: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Renders a row indicating a specific municipality and its active status indicator
 *
 * @param name The name of the municipality
 */
@Composable
private fun MunicipalityRow(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color(0xFF64FC49), CircleShape)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Activo",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Displays a loading spinner when data is being fetched
 */
@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Displays an error message and a retry button when data loading fails
 *
 * @param onRetry Callback triggered when tapping the retry button
 */
@Composable
private fun ErrorSection(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "No se pudieron cargar las estadísticas.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

/**
 * Preview for the success state of [StatsScreenContent]
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun StatsScreenSuccessPreview() {
    MaterialTheme {
        StatsScreenContent(
            uiState = StatsUiState.Success(reportsProcessed = 1284, minorsProtected = 396),
            onBack = {},
            onRetry = {}
        )
    }
}

/**
 * Preview for the loading state of [StatsScreenContent]
 */
@Preview(showBackground = true)
@Composable
private fun StatsScreenLoadingPreview() {
    MaterialTheme {
        StatsScreenContent(uiState = StatsUiState.Loading, onBack = {}, onRetry = {})
    }
}

/**
 * Preview for the error state of [StatsScreenContent]
 */
@Preview(showBackground = true)
@Composable
private fun StatsScreenErrorPreview() {
    MaterialTheme {
        StatsScreenContent(uiState = StatsUiState.Error, onBack = {}, onRetry = {})
    }
}
