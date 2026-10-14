package tec.spn.rieti.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import tec.spn.rieti.model.GeoPoint
import tec.spn.rieti.model.ReportDraft
import tec.spn.rieti.model.TimeOfDay
import tec.spn.rieti.model.WorkType

private enum class TimeTarget { Start, End }

@Composable
fun DetailsStepScreen(
    draft: ReportDraft,
    onChildrenCountChange: (String) -> Unit,
    onChildrenAgesChange: (String) -> Unit,
    onWorkTypeChange: (WorkType) -> Unit,
    onStartTimeChange: (TimeOfDay) -> Unit,
    onEndTimeChange: (TimeOfDay) -> Unit,
    onObservationsChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onUseCurrentLocation: () -> Unit,
    onEmergencyChange: (Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pickingTime by rememberSaveable { mutableStateOf<TimeTarget?>(null) }

    ReportStepScaffold(
        currentStep = 2,
        onBack = onBack,
        onDiscard = onDiscard,
        primaryButtonText = "Siguiente",
        primaryButtonEnabled = draft.isDetailsStepValid,
        onPrimaryClick = onNext,
        modifier = modifier
    ) {
        Text(
            text = "Información general",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // ---------- Case details ----------
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Detalles del caso",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(16.dp))
        ReportFieldLabel("Número de niños")
        ReportTextField(
            value = draft.childrenCount,
            onValueChange = onChildrenCountChange,
            placeholder = "1",
            errorMessage = draft.childrenCountError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )

        Spacer(Modifier.height(16.dp))
        ReportFieldLabel("Edades de los niños")
        ReportTextField(
            value = draft.childrenAges,
            onValueChange = onChildrenAgesChange,
            placeholder = "Ej. 12, 14, 16 años",
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(Modifier.height(16.dp))
        ReportFieldLabel("Tipo de trabajo")
        WorkTypeDropdown(selected = draft.workType, onSelected = onWorkTypeChange)

        Spacer(Modifier.height(16.dp))
        ReportFieldLabel("Horario de trabajo")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimeField(
                label = "Inicio",
                time = draft.startTime,
                onClick = { pickingTime = TimeTarget.Start },
                modifier = Modifier.weight(1f)
            )
            TimeField(
                label = "Fin",
                time = draft.endTime,
                onClick = { pickingTime = TimeTarget.End },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
        ReportFieldLabel("Observaciones detalladas")
        ReportTextField(
            value = draft.observations,
            onValueChange = onObservationsChange,
            placeholder = "Describa detalladamente lo que ha observado...",
            singleLine = false,
            minLines = 4,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Spacer(Modifier.height(28.dp))
        Text(
            text = "Ubicación del caso",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))
        LocationPreview(location = draft.location)

        TextButton(onClick = onUseCurrentLocation) {
            Icon(
                imageVector = Icons.Outlined.NearMe,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Usar mi ubicación actual")
        }

        Spacer(Modifier.height(8.dp))
        ReportFieldLabel("Dirección completa")
        ReportTextField(
            value = draft.address,
            onValueChange = onAddressChange,
            placeholder = "Ej. Calle D, Renovación Jalapa, Ecatepec de Morelos",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            )
        )

        Spacer(Modifier.height(28.dp))
        Text(
            text = "Detalles adicionales",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))
        Text(
            text = "¿El caso es una emergencia?",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Column(modifier = Modifier.selectableGroup()) {
            ReportRadioOption(
                label = "Sí",
                selected = draft.isEmergency,
                onClick = { onEmergencyChange(true) }
            )
            ReportRadioOption(
                label = "No",
                selected = !draft.isEmergency,
                onClick = { onEmergencyChange(false) }
            )
        }

        if (draft.isEmergency) {
            Spacer(Modifier.height(8.dp))
            EmergencyWarning()
        }

        Spacer(Modifier.height(16.dp))
    }

    when (pickingTime) {
        TimeTarget.Start -> ReportTimePickerDialog(
            title = "Hora de inicio",
            initial = draft.startTime,
            onConfirm = {
                onStartTimeChange(it)
                pickingTime = null
            },
            onDismiss = { pickingTime = null }
        )

        TimeTarget.End -> ReportTimePickerDialog(
            title = "Hora de fin",
            initial = draft.endTime,
            onConfirm = {
                onEndTimeChange(it)
                pickingTime = null
            },
            onDismiss = { pickingTime = null }
        )

        null -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkTypeDropdown(
    selected: WorkType?,
    onSelected: (WorkType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        ReportTextField(
            value = selected?.label ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = "Seleccione un tipo de trabajo.",
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WorkType.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun TimeField(
    label: String,
    time: TimeOfDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = ReportFieldBackground,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = time.toDisplayString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTimePickerDialog(
    title: String,
    initial: TimeOfDay,
    onConfirm: (TimeOfDay) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initial.hour,
        initialMinute = initial.minute,
        is24Hour = false
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier.width(IntrinsicSize.Min)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                TimePicker(state = state)
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    TextButton(onClick = { onConfirm(TimeOfDay(state.hour, state.minute)) }) {
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationPreview(location: GeoPoint?) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ReportFieldBackground,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.height(8.dp))
                if (location == null) {
                    Text(
                        text = "Aún no has seleccionado la ubicación",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Ubicación seleccionada",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "%.5f, %.5f".format(location.latitude, location.longitude),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyWarning() {
    val context = LocalContext.current

    Column {
        Row {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "El sistema RIETI no sustituye servicios de emergencia. En caso de ser una emergencia el reporte se registrará con alta prioridad.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        TextButton(
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:911")))
                } catch (e: ActivityNotFoundException) {
                }
            }
        ) {
            Text("Llamar al 911")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailsStepPreview() {
    MaterialTheme {
        DetailsStepScreen(
            draft = ReportDraft(
                childrenCount = "2",
                childrenAges = "12, 14 años",
                workType = WorkType.VENTA_AMBULANTE,
                isEmergency = true,
                location = GeoPoint(19.2826, -99.6557)
            ),
            onChildrenCountChange = {},
            onChildrenAgesChange = {},
            onWorkTypeChange = {},
            onStartTimeChange = {},
            onEndTimeChange = {},
            onObservationsChange = {},
            onAddressChange = {},
            onUseCurrentLocation = {},
            onEmergencyChange = {},
            onNext = {},
            onBack = {},
            onDiscard = {}
        )
    }
}