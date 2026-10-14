package tec.spn.rieti.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tec.spn.rieti.model.ReportDraft


@Composable
fun ReporterStepScreen(
    draft: ReportDraft,
    onAnonymousChange: (Boolean) -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onNext: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    ReportStepScaffold(
        currentStep = 1,
        onDiscard = onDiscard,
        primaryButtonText = "Siguiente",
        primaryButtonEnabled = draft.isReporterStepValid,
        onPrimaryClick = onNext,
        modifier = modifier
    ) {
        Text(
            text = "Datos del denunciante",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))
        Text(
            text = "Tus datos se mantendrán confidenciales y se usarán solo para dar seguimiento a tu reporte. Si deseas realizar el reporte de manera anónima, favor de seleccionar Reporte anónimo",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(12.dp))
        Column(modifier = Modifier.selectableGroup()) {
            ReportRadioOption(
                label = "Reporte anónimo",
                selected = draft.isAnonymous,
                onClick = { onAnonymousChange(true) }
            )
            ReportRadioOption(
                label = "Reporte con seguimiento",
                selected = !draft.isAnonymous,
                onClick = { onAnonymousChange(false) }
            )
        }

        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.padding(start = 4.dp)) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "En caso de seleccionar reporte anónimo, no se podrá dar seguimiento por correo electrónico, SMS o WhatsApp.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (!draft.isAnonymous) {
            Spacer(Modifier.height(20.dp))
            FieldLabel("Nombre completo")
            ReportTextField(
                value = draft.name,
                onValueChange = onNameChange,
                placeholder = "Ej. Juan Pérez García",
                leadingIcon = Icons.Outlined.Person,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(Modifier.height(16.dp))
            FieldLabel("Correo electrónico")
            ReportTextField(
                value = draft.email,
                onValueChange = onEmailChange,
                placeholder = "Ej. juan.perez@gmail.com",
                leadingIcon = Icons.Outlined.Email,
                errorMessage = draft.emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(Modifier.height(16.dp))
            FieldLabel("Teléfono de contacto")
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ReportFieldBackground,
                    modifier = Modifier.height(56.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+52",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(Modifier.width(8.dp))
                ReportTextField(
                    value = draft.phone,
                    onValueChange = onPhoneChange,
                    placeholder = "55 1234 5678",
                    errorMessage = draft.phoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Proporciona al menos un correo o un teléfono de contacto.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(20.dp))
        ConfidentialityCard()
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun ConfidentialityCard() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ReportInfoBackground,
        border = BorderStroke(1.dp, ReportInfoBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Confidencialidad garantizada",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Tu identidad estará protegida y tu información no será compartida con terceros.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReporterStepRegisteredPreview() {
    MaterialTheme {
        ReporterStepScreen(
            draft = ReportDraft(),
            onAnonymousChange = {},
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onNext = {},
            onDiscard = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReporterStepAnonymousPreview() {
    MaterialTheme {
        ReporterStepScreen(
            draft = ReportDraft(isAnonymous = true),
            onAnonymousChange = {},
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onNext = {},
            onDiscard = {}
        )
    }
}