package tec.spn.rieti.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.NoteAdd
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tec.spn.rieti.viewmodel.MainViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import tec.spn.rieti.viewmodel.MainUiState
import tec.spn.rieti.viewmodel.StatItem

private val DarkCard = Color(0xFF1B1B1B)
private val ChipBackground = Color(0xFFF1F1F5)
private val InfoBackground = Color(0xFFE8F1FB)
private val InfoBorder = Color(0xFFCFE0F3)

/**
 * Quick navigation item displayed on the main screen
 *
 * @property label Text label for the quick action
 * @property icon Icon associated with the action
 * @property onClick Callback triggered when the item is tapped
 */
private data class QuickAccess(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

/**
 * Main Screen component for UI
 *
 * @param onNewReport Callback to navigate to the new report creation flow
 * @param onTracking Callback to navigate to report tracking
 * @param onStatistics Callback to navigate to statistics screen
 * @param onHelp Callback to navigate to the help screen
 * @param onMore Callback to navigate to additional resources screen
 * @param modifier Custom [Modifier] for layout adjustments
 * @param viewModel The ViewModel providing UI state and data.
 */
@Composable
fun MainScreen(
    onNewReport: () -> Unit,
    onTracking: () -> Unit,
    onStatistics: () -> Unit,
    onHelp: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainScreenContent(
        uiState = uiState,
        onNewReport = onNewReport,
        onTracking = onTracking,
        onStatistics = onStatistics,
        onHelp = onHelp,
        onMore = onMore,
        modifier = modifier
    )
}

/**
 * Stateless content version of the main dashboard screen, renders the UI based on provided state
 *
 * @param uiState Current state containing dashboard stats and values
 * @param onNewReport Callback invoked when tapping the new report card
 * @param onTracking Callback invoked when tapping tracking quick access
 * @param onStatistics Callback invoked when tapping statistics quick access
 * @param onHelp Callback invoked when tapping help quick access
 * @param onMore Callback invoked when tapping more quick access
 * @param modifier Custom [Modifier] for layout styling
 */
@Composable
fun MainScreenContent(
    uiState: MainUiState,
    onNewReport: () -> Unit,
    onTracking: () -> Unit,
    onStatistics: () -> Unit,
    onHelp: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAccess = listOf(
        QuickAccess("Seguimiento", Icons.Outlined.Search, onTracking),
        QuickAccess("Estadísticas", Icons.Outlined.BarChart, onStatistics),
        QuickAccess("Ayuda", Icons.AutoMirrored.Outlined.HelpOutline, onHelp),
        QuickAccess("Más", Icons.Outlined.MoreHoriz, onMore)
    )

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            OfficialHeader()
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Sistema RIETI",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))
            Text(
                text = "Protegiendo los Derechos de los Menores",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(14.dp))
            Text(
                text = "Reporte casos de trabajo infantil de manera segura, confidencial y anónima. Su denuncia puede cambiar vidas.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))
            NewReportCard(onClick = onNewReport)

            Spacer(Modifier.height(24.dp))
            Text(
                text = "Accesos rápidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(quickAccess) { item ->
                    QuickAccessChip(item)
                }
            }

            Spacer(Modifier.height(16.dp))
            ProtectedInfoCard()

            Spacer(Modifier.height(24.dp))
            uiState.stats.chunked(2).forEach { rowStats ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    rowStats.forEach { stat ->
                        StatBlock(
                            stat = stat,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Banner header
 */
@Composable
private fun OfficialHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "Sistema oficial del Estado de México",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Card component for creating a new report
 *
 * @param onClick Callback triggered when the card is clicked
 */
@Composable
private fun NewReportCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        contentColor = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.NoteAdd,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nuevo reporte",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Reporta un caso de forma rápida y sencilla",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

/**
 * Individual quick access button
 *
 * @param item The [QuickAccess] configuration details
 */
@Composable
private fun QuickAccessChip(item: QuickAccess) {
    Surface(
        onClick = item.onClick,
        shape = RoundedCornerShape(16.dp),
        color = ChipBackground,
        modifier = Modifier
            .height(64.dp)
            .widthIn(min = 80.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Renders a card about user security
 */
@Composable
private fun ProtectedInfoCard() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = InfoBackground,
        border = BorderStroke(1.dp, InfoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tu información está protegida",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tus reportes son confidenciales y serán tratados con seguridad.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.size(12.dp))
            Icon(
                imageVector = Icons.Outlined.VerifiedUser,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Displays a single statistical metric block with a value and label
 *
 * @param stat The [StatItem] holding the metric value and description label
 * @param modifier Custom [Modifier] for sizing/weight
 */
@Composable
private fun StatBlock(stat: StatItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = stat.value,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stat.label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Preview layout for [MainScreenContent]
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    MaterialTheme {
        MainScreenContent(
            uiState = MainUiState(
                stats = listOf(
                    StatItem("24/7", "Disponibilidad"),
                    StatItem("14", "Municipios cubiertos"),
                    StatItem("100%", "Confidencial"),
                    StatItem("3", "Canales de comunicación")
                )
            ),
            onNewReport = {},
            onTracking = {},
            onStatistics = {},
            onHelp = {},
            onMore = {}
        )
    }
}