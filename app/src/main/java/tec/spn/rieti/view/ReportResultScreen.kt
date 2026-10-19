package tec.spn.rieti.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val ResultSuccessBackground = Color(0xFFE6F8EC)
private val ResultSuccessBorder = Color(0xFFA8E6BD)
private val ResultSuccessText = Color(0xFF1E9E4A)
private val ResultErrorBackground = Color(0xFFFDECEC)
private val ResultErrorBorder = Color(0xFFE5484D)
private val ResultErrorText = Color(0xFFD62828)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportSuccessScreen(
    folio: String?,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    BackHandler(onBack = onFinish)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Reporte enviado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
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
            ResultBanner(
                icon = Icons.Outlined.CheckCircle,
                title = "Reporte enviado",
                message = "Tu reporte fue recibido correctamente.",
                background = ResultSuccessBackground,
                border = ResultSuccessBorder,
                accent = ResultSuccessText
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = "¡Gracias por tu reporte!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))
            if (folio != null) {
                Text(
                    text = "Folio del reporte",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = folio,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    IconButton(onClick = { clipboard.setText(AnnotatedString(folio)) }) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copiar folio"
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Guarda este folio: es la única forma de consultar el estatus de tu reporte.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = "Tu reporte anónimo fue enviado. Al ser anónimo no recibes el folio asociado, por lo que no será posible consultar su estatus.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(24.dp))
            DarkButton(
                text = "Volver al menú",
                icon = null,
                onClick = onFinish
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportErrorScreen(
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Envío del reporte",
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
            ResultBanner(
                icon = Icons.Outlined.ErrorOutline,
                title = "No se pudo enviar el reporte",
                message = "Ocurrió un problema al conectar con el sistema.",
                background = ResultErrorBackground,
                border = ResultErrorBorder,
                accent = ResultErrorText
            )

            Spacer(Modifier.height(20.dp))
            Text(
                text = "¡No se pudo enviar!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Tu reporte no ha sido enviado. Revisa tu conexión a internet e inténtalo de nuevo. La información que capturaste se conserva.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))
            DarkButton(
                text = "Reintentar",
                icon = Icons.Filled.Refresh,
                onClick = onRetry
            )
        }
    }
}

@Composable
private fun DarkButton(
    text: String,
    icon: ImageVector?,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ReportDark,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ResultBanner(
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReportSuccessRegisteredPreview() {
    MaterialTheme {
        ReportSuccessScreen(folio = "RIETI-ECA-2026-0005", onFinish = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportSuccessAnonymousPreview() {
    MaterialTheme {
        ReportSuccessScreen(folio = null, onFinish = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportErrorPreview() {
    MaterialTheme {
        ReportErrorScreen(onRetry = {}, onBack = {})
    }
}