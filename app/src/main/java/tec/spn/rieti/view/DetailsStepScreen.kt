package tec.spn.rieti.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import tec.spn.rieti.model.DeviceLocation
import tec.spn.rieti.model.AgeRange
import tec.spn.rieti.model.ChildrenCount
import tec.spn.rieti.model.GeoPoint
import tec.spn.rieti.model.ReportDraft
import tec.spn.rieti.model.TimeOfDay
import tec.spn.rieti.model.WorkType

private enum class TimeTarget { Start, End }

/**
 * Step 2 of the report flow: case details, location and emergency flag.
 * Stateless: the draft and every change callback come from the ViewModel.
 */
@Composable
fun DetailsStepScreen(
    draft: ReportDraft,
    onChildrenCountChange: (ChildrenCount) -> Unit,
    onAgeRangeToggled: (AgeRange) -> Unit,
    onWorkTypeChange: (WorkType) -> Unit,
    onStartTimeChange: (TimeOfDay) -> Unit,
    onEndTimeChange: (TimeOfDay) -> Unit,
    onObservationsChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onLocationSelected: (GeoPoint, String?) -> Unit,
    onOpenMapPicker: () -> Unit,
    onEmergencyChange: (Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Which time picker dialog is open (null = none). Enums are saveable.
    var pickingTime by rememberSaveable { mutableStateOf<TimeTarget?>(null) }

    // "Usar mi ubicación actual": permission -> GPS fix -> reverse geocoding -> draft
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var locationMessage by rememberSaveable { mutableStateOf<String?>(null) }
    val locationRequester = rememberLocationRequester(
        onLocation = { point ->
            locationMessage = null
            scope.launch {
                onLocationSelected(point, DeviceLocation.reverseGeocode(context, point))
            }
        },
        onFailure = { failure ->
            locationMessage = when (failure) {
                LocationFailure.PermissionDenied ->
                    "Sin permiso de ubicación. Toca el mapa para elegir el punto manualmente."
                LocationFailure.Unavailable ->
                    "No pudimos obtener tu ubicación. Revisa que el GPS esté activo o elige el punto en el mapa."
            }
        }
    )

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
        ChoiceChips(
            options = ChildrenCount.entries,
            isSelected = { it == draft.childrenCount },
            label = { it.label },
            onClick = onChildrenCountChange
        )

        Spacer(Modifier.height(16.dp))
        ReportFieldLabel("Edades de los niños")
        Text(
            text = "Puedes elegir más de un rango.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ChoiceChips(
            options = AgeRange.entries,
            isSelected = { it in draft.childrenAges },
            label = { it.label },
            onClick = onAgeRangeToggled
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

        // ---------- Location ----------
        Spacer(Modifier.height(28.dp))
        Text(
            text = "Ubicación del caso",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(12.dp))
        LocationMapPreview(location = draft.location, onClick = onOpenMapPicker)

        TextButton(
            onClick = { locationRequester.request() },
            enabled = !locationRequester.isLocating
        ) {
            if (locationRequester.isLocating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                if (locationRequester.isLocating) "Obteniendo ubicación…" else "Usar mi ubicación actual"
            )
        }
        locationMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
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

        // ---------- Emergency ----------
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

    // Time picker dialogs live outside the scrolling content
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

/**
 * Row of selectable chips that wraps onto several lines. Used for both the single
 * choice (children count) and the multiple choice (age ranges): what "selected"
 * means is decided by the caller through [isSelected] and [onClick].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> ChoiceChips(
    options: List<T>,
    isSelected: (T) -> Boolean,
    label: (T) -> String,
    onClick: (T) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = isSelected(option),
                onClick = { onClick(option) },
                label = { Text(label(option)) }
            )
        }
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
            placeholder = "Seleccione un tipo de trabajo...",
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            // On Material3 older than 1.3 use Modifier.menuAnchor() without arguments
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

/** Material3 TimePicker inside a dialog (12-hour clock, like the prototype). */
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
        // ACTION_DIAL only opens the dialer with the number typed in, no permission needed
        TextButton(
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:911")))
                } catch (e: ActivityNotFoundException) {
                    // Device without a dialer app: nothing to do
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
                childrenCount = ChildrenCount.TWO,
                childrenAges = setOf(AgeRange.RANGE_6_11, AgeRange.RANGE_12_14),
                workType = WorkType.VENTA_AMBULANTE,
                isEmergency = true,
                location = GeoPoint(19.2826, -99.6557)
            ),
            onChildrenCountChange = {},
            onAgeRangeToggled = {},
            onWorkTypeChange = {},
            onStartTimeChange = {},
            onEndTimeChange = {},
            onObservationsChange = {},
            onAddressChange = {},
            onLocationSelected = { _, _ -> },
            onOpenMapPicker = {},
            onEmergencyChange = {},
            onNext = {},
            onBack = {},
            onDiscard = {}
        )
    }
}