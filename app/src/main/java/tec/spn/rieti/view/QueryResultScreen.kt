package tec.spn.rieti.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import tec.spn.rieti.model.Report
import tec.spn.rieti.model.ReportStatus
import tec.spn.rieti.viewmodel.QueryResultUiState
import tec.spn.rieti.viewmodel.QueryResultVM
import tec.spn.rieti.viewmodel.StatsVM

private val DarkCard = Color(0xFF1B1B1B)
private val InfoBackground = Color(0xFFE8F1FB)
private val InfoBorder = Color(0xFFCFE0F3)
private val SuccessBackground = Color(0xFFE6F8EC)
private val SuccessBorder = Color(0xFFA8E6BD)
private val SuccessText = Color(0xFF1E9E4A)
private val ErrorBackground = Color(0xFFFDECEC)
private val ErrorBorder = Color(0xFFE5484D)
private val ErrorText = Color(0xFFD62828)

/**
 * Query Result Screen for UI
 * The folio arrives through the navigation argument, read by the ViewModel
 *
 * @param onBack Action triggered when the user taps the back navigation button
 * @param modifier Custom [Modifier] for layout styling
 * @param viewModel The [QueryResultVM] managing the statistics UI state
 */
@Composable
fun QueryResultScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: QueryResultVM = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QueryResultContent(
        uiState = uiState,
        onBack = onBack,
        onRetry = viewModel::loadReport,
        modifier = modifier
    )
}

/**
 * Stateless content for the query results screen, handles loading, error, and success states
 *
 * @param uiState Current [QueryResultUiState] representing data loading status
 * @param onBack Callback invoked when navigating back
 * @param onRetry Callback invoked to reload statistics when an error occurs
 * @param modifier Custom [Modifier] for layout styling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryResultContent(
    uiState: QueryResultUiState,
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
                        text = "Detalle del reporte",
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            when (uiState) {
                QueryResultUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is QueryResultUiState.Found -> FoundContent(uiState.report)

                QueryResultUiState.NotFound -> FailureContent(
                    bannerTitle = "Reporte no encontrado",
                    bannerMessage = "El folio ingresado es incorrecto.",
                    headline = "¡Error al encontrar el reporte!",
                    body = "Su reporte no ha podido ser encontrado en nuestro sistema. Favor de volver a intentarlo.",
                    buttonText = "Volver a intentar",
                    onButtonClick = onBack
                )

                QueryResultUiState.Error -> FailureContent(
                    bannerTitle = "No se pudo consultar",
                    bannerMessage = "Ocurrió un problema al conectar con el sistema.",
                    headline = "¡Algo salió mal!",
                    body = "No pudimos obtener la información de su reporte. Revise su conexión a internet e inténtelo de nuevo.",
                    buttonText = "Reintentar",
                    onButtonClick = onRetry
                )
            }
        }
    }
}

/**
 * Resulting screen for found report
 *
 * @param report Contains the report information retrieved from the repository
 */
@Composable
private fun FoundContent(report: Report) {
    val clipboard = LocalClipboardManager.current

    FeedbackBanner(
        icon = Icons.Outlined.CheckCircle,
        title = "Reporte encontrado",
        message = "Aquí puedes ver el estado y los detalles de tu reporte.",
        background = SuccessBackground,
        border = SuccessBorder,
        accent = SuccessText
    )

    Spacer(Modifier.height(20.dp))
    Text(
        text = "Folio del reporte",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = report.folio,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f, fill = false)
        )
        IconButton(onClick = { clipboard.setText(AnnotatedString(report.folio)) }) {
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = "Copiar folio"
            )
        }
    }
    /*
    Text(
        text = "Fecha de creación: ${report.createdAt}",
        style = MaterialTheme.typography.bodySmall
    )
    */

    Spacer(Modifier.height(20.dp))
    Text(
        text = "Estado actual",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium
    )
    Spacer(Modifier.height(12.dp))
    StatusCard(report.status)
}
/**
 * Card displaying the status of the report associated with the searched folio
 *
 * @param status Information about the status of the report
 */
@Composable
private fun StatusCard(status: ReportStatus) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = InfoBackground,
        border = BorderStroke(1.dp, InfoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = status.description,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
/**
 * Resulting screen for error or not found queries
 *
 * @param bannerTitle Title for the error card at the top of the screen
 * @param bannerMessage Message for the error card at the top of the screen
 * @param headline Title for the main body of the screen
 * @param body Body detailing the problem with the query
 * @param buttonText Text for the retry button
 * @param onButtonClick Action for when the retry button is pressed
 */
@Composable
private fun FailureContent(
    bannerTitle: String,
    bannerMessage: String,
    headline: String,
    body: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    FeedbackBanner(
        icon = Icons.Outlined.ErrorOutline,
        title = bannerTitle,
        message = bannerMessage,
        background = ErrorBackground,
        border = ErrorBorder,
        accent = ErrorText
    )

    Spacer(Modifier.height(20.dp))
    Text(
        text = headline,
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(12.dp))
    Text(
        text = body,
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = onButtonClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkCard,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = buttonText,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

/** Colored banner shown at the top */
@Composable
private fun FeedbackBanner(
    icon: ImageVector,
    title: String,
    message: String,
    background: Color,
    border: Color,
    accent: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = background,
        border = BorderStroke(1.dp, border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = accent
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Preview for the found state of [QueryScreen]
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun QueryResultFoundPreview() {
    MaterialTheme {
        QueryResultContent(
            uiState = QueryResultUiState.Found(
                Report(
                    folio = "RIETI-ECA-2026-0005",
                    status = ReportStatus.EN_REVISION,
                    //createdAt = "26 de agosto del 2026, 11:30 a. m."
                )
            ),
            onBack = {},
            onRetry = {}
        )
    }
}

/**
 * Preview for the not found state of [QueryScreen]
 */
@Preview(showBackground = true)
@Composable
private fun QueryResultNotFoundPreview() {
    MaterialTheme {
        QueryResultContent(QueryResultUiState.NotFound, onBack = {}, onRetry = {})
    }
}

/**
 * Preview for the error state of [QueryScreen]
 */
@Preview(showBackground = true)
@Composable
private fun QueryResultErrorPreview() {
    MaterialTheme {
        QueryResultContent(QueryResultUiState.Error, onBack = {}, onRetry = {})
    }
}

/**
 * Preview for the loading state of [QueryScreen]
 */
@Preview(showBackground = true)
@Composable
private fun QueryResultLoadingPreview() {
    MaterialTheme {
        QueryResultContent(QueryResultUiState.Loading, onBack = {}, onRetry = {})
    }
}