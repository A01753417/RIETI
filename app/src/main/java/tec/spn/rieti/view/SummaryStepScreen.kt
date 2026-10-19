package tec.spn.rieti.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tec.spn.rieti.model.AgeRange
import tec.spn.rieti.model.ChildrenCount
import tec.spn.rieti.model.GeoPoint
import tec.spn.rieti.model.ReportDraft
import tec.spn.rieti.model.WorkType

/**
 * Step 3: read-only review of everything captured, with "Editar" links back to
 * the step that owns each block, and the final send button.
 */
@Composable
fun SummaryStepScreen(
    draft: ReportDraft,
    isSubmitting: Boolean,
    onEditReporter: () -> Unit,
    onEditDetails: () -> Unit,
    onSend: () -> Unit,
    onBack: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    ReportStepScaffold(
        currentStep = 3,
        onBack = onBack,
        onDiscard = onDiscard,
        primaryButtonText = "Enviar reporte",
        primaryButtonIcon = Icons.AutoMirrored.Filled.Send,
        isBusy = isSubmitting,
        onPrimaryClick = onSend,
        modifier = modifier
    ) {
        Text(
            text = "Resumen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Revisa que la información sea correcta antes de enviar tu reporte.",
            style = MaterialTheme.typography.bodyMedium
        )

        // ---------- Reporter ----------
        SummarySection(
            title = "Denunciante",
            onEdit = onEditReporter,
            editEnabled = !isSubmitting
        ) {
            if (draft.isAnonymous) {
                AnonymousNotice()
            } else {
                SummaryField("Nombre", draft.name)
                if (draft.email.isNotEmpty()) SummaryField("Correo", draft.email)
                if (draft.phone.isNotEmpty()) SummaryField("Teléfono", formatPhone(draft.phone))
            }
        }

        // ---------- Case details ----------
        SummarySection(
            title = "Detalles del caso",
            onEdit = onEditDetails,
            editEnabled = !isSubmitting
        ) {
            SummaryField("Número de niños", draft.childrenCount?.label.orEmpty())
            SummaryField(
                "Edades de los niños",
                draft.childrenAges.joinToString(", ") { it.label }
            )
            SummaryField("Tipo de trabajo", draft.workType?.label.orEmpty())
            SummaryField(
                "Horario de trabajo",
                "${draft.startTime.toDisplayString()} – ${draft.endTime.toDisplayString()}"
            )
            SummaryField("Observaciones", draft.observations)
            SummaryField("¿Es una emergencia?", if (draft.isEmergency) "Sí" else "No")
        }

        // ---------- Location ----------
        SummarySection(
            title = "Ubicación",
            onEdit = onEditDetails,
            editEnabled = !isSubmitting
        ) {
            LocationMapPreview(location = draft.location, onClick = null)
            SummaryField("Dirección", draft.address)
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SummarySection(
    title: String,
    onEdit: () -> Unit,
    editEnabled: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Spacer(Modifier.height(24.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(12.dp))
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
    TextButton(onClick = onEdit, enabled = editEnabled) {
        Icon(
            imageVector = Icons.Outlined.Edit,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Editar")
    }
    HorizontalDivider()
}

/** Label above a read-only value box, like the prototype's summary fields. */
@Composable
private fun SummaryField(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, ReportTrack),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            )
        }
    }
}

/** Shown instead of the identity fields for anonymous reports. */
@Composable
private fun AnonymousNotice() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ReportInfoBackground,
        border = BorderStroke(1.dp, ReportInfoBorder),
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
                    text = "Reporte anónimo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "No se guardará ningún dato personal y no se generará un folio, por lo que no podrás consultar el estatus de este reporte.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/** "5512345678" -> "+52 55 1234 5678". The draft guarantees 10 digits when non-empty. */
private fun formatPhone(digits: String): String =
    "+52 ${digits.take(2)} ${digits.drop(2).take(4)} ${digits.drop(6)}"

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SummaryRegisteredPreview() {
    MaterialTheme {
        SummaryStepScreen(
            draft = ReportDraft(
                name = "Juan Pérez García",
                email = "juan.perez@gmail.com",
                phone = "5512345678",
                childrenCount = ChildrenCount.TWO,
                childrenAges = setOf(AgeRange.RANGE_12_14),
                workType = WorkType.LIMPIEZA_PARABRISAS,
                observations = "Se observa a dos menores trabajando en el crucero.",
                isEmergency = false,
                location = GeoPoint(19.2826, -99.6557),
                address = "Calle D, Renovación Jalapa, Ecatepec de Morelos"
            ),
            isSubmitting = false,
            onEditReporter = {},
            onEditDetails = {},
            onSend = {},
            onBack = {},
            onDiscard = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryAnonymousSubmittingPreview() {
    MaterialTheme {
        SummaryStepScreen(
            draft = ReportDraft(
                isAnonymous = true,
                childrenCount = ChildrenCount.ONE,
                childrenAges = setOf(AgeRange.RANGE_6_11),
                workType = WorkType.MENDICIDAD,
                observations = "Menor pidiendo dinero.",
                isEmergency = true,
                location = GeoPoint(19.2826, -99.6557),
                address = "Av. Principal s/n"
            ),
            isSubmitting = true,
            onEditReporter = {},
            onEditDetails = {},
            onSend = {},
            onBack = {},
            onDiscard = {}
        )
    }
}