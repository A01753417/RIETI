package tec.spn.rieti.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Represents a single step in the help guide process.
 *
 * @property title The step's title
 * @property description A short summary explaining what happens during this step
 * @property icon The icon visually representing the step
 */
private data class HelpStep(
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * List of steps detailing how the report submission process
 */
private val helpSteps = listOf(
    HelpStep(
        title = "1. Complete el formulario",
        description = "Proporcione la información solicitada, detalles del caso y ubicación GPS.",
        icon = Icons.AutoMirrored.Outlined.Assignment
    ),
    HelpStep(
        title = "2. Reciba confirmación",
        description = "Obtenga un folio único y confirmación por email, SMS y Whatsapp.",
        icon = Icons.Outlined.CheckCircle
    ),
    HelpStep(
        title = "3. Autoridades actúan",
        description = "Las autoridades competentes reciben la denuncia y toman acciones inmediatas.",
        icon = Icons.Outlined.Groups
    ),
    HelpStep(
        title = "4. Seguimiento",
        description = "Monitoree el progreso de su reporte usando el folio asignado.",
        icon = Icons.AutoMirrored.Outlined.TrendingUp
    )
)

/**
 * Help screen component for the UI
 *
 * @param onBack Action triggered when the user taps the back navigation button
 * @param modifier Custom [Modifier] to adjust the layout behavior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ayuda",
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
                text = "¿Cómo funciona?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "El proceso es simple, seguro y efectivo. Siga estos pasos para hacer la diferencia.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))
            helpSteps.forEach { step ->
                HorizontalDivider()
                HelpStepItem(step = step)
            }
        }
    }
}

/**
 * Individual help step component, shows its title, icon, and description
 *
 * @param step The [HelpStep] data object containing step details
 */
@Composable
private fun HelpStepItem(step: HelpStep) {
    Column(modifier = Modifier.padding(vertical = 20.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = step.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = step.description,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * Preview layout for [HelpScreen]
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HelpScreenPreview() {
    MaterialTheme {
        HelpScreen(onBack = {})
    }
}
