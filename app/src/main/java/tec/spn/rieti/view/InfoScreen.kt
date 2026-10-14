package tec.spn.rieti.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri

private val DarkCard = Color(0xFF1B1B1B)

/**
 * Represents an informational resource
 *
 * @property title Name of the service or resource
 * @property description Overview of what the service offers
 * @property icon Visual icon associated with the service category
 * @property link Web URL directing users to full details in external webpage
 */
private data class InfoElement(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val link: String
)

/**
 * List of external resources
 */
private val infoElements = listOf(
    InfoElement(
        title = "Educación y prevención",
        description = "Programas educativos para concientizar sobre los derechos de los menores y prevenir el trabajo infantil.",
        icon = Icons.Filled.School,
        link = "https://difem.edomex.gob.mx/platicas-talleres-violencia-familiar"
    ),
    InfoElement(
        title = "Apoyo y rehabilitación",
        description = "Servicios de apoyo psicológico y programas de rehabilitación para menores afectados por el trabajo infantil.",
        icon = Icons.Filled.Handshake,
        link = "https://difem.edomex.gob.mx/servicio-orientacion-psicologica-SOS-jovenes"
    ),
    InfoElement(
        title = "Asesoría legal",
        description = "Asesoría legal gratuita para familias afectadas y orientación sobre los derechos de los menores.",
        icon = Icons.Filled.Balance,
        link = "https://difem.edomex.gob.mx/representacipn-asistenciajuridica"
    )

)

/**
 * Info Screen component for the UI
 *
 * @param onBack Action triggered when the user taps the back navigation button
 * @param modifier Custom [Modifier] applied to the top-level layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Más información",
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
                text = "Servicios y recursos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text = "Información acerca de una gama de servicios para combatir el trabajo infantil",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(20.dp))
            infoElements.forEach { elem ->
                HorizontalDivider()
                InfoElementItem(elem = elem)
            }
        }
    }
}

/**
 * Individual resource card with a title, description, icon, and an external link button.
 *
 * @param elem The [InfoElement] model containing its information
 */
@Composable
private fun InfoElementItem(elem: InfoElement) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(vertical = 20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = elem.icon,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = elem.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = elem.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(Modifier.height(12.dp))
        Surface(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, elem.link.toUri())
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(12.dp),
            color = DarkCard,
            contentColor = Color.White,
            modifier = Modifier.width(200.dp).height(44.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Conocer más",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}

/**
 * Preview layout for [InfoScreen]
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HelpScreenPreview() {
    MaterialTheme {
        InfoScreen(onBack = {})
    }
}