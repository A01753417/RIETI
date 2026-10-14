package tec.spn.rieti.model

import java.util.Locale

private val EmailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

enum class WorkType(val label: String) {
    VENTA_AMBULANTE("Venta ambulante"),
    LIMPIEZA_PARABRISAS("Limpieza de parabrisas"),
    MENDICIDAD("Mendicidad"),
    CARGA_DESCARGA("Carga y descarga"),
    TRABAJO_COMERCIO("Trabajo en comercio"),
    CAMPO("Campo"),
    CONSTRUCCION("Construcción"),
    TRABAJO_DOMESTICO("Trabajo doméstico"),
    RECOLECCION_RESIDUOS("Recolección de residuos"),
    OTRA_ACTIVIDAD("Otra actividad")
}

data class TimeOfDay(val hour: Int, val minute: Int) {

    fun toDisplayString(): String {
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val suffix = if (hour < 12) "AM" else "PM"
        return String.format(Locale.ROOT, "%d:%02d %s", hour12, minute, suffix)
    }

    fun toApiString(): String = String.format(Locale.ROOT, "%02d:%02d", hour, minute)
}

data class GeoPoint(val latitude: Double, val longitude: Double)

data class ReportDraft(
    val isAnonymous: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",

    val childrenCount: String = "",
    val childrenAges: String = "",
    val workType: WorkType? = null,
    val startTime: TimeOfDay = TimeOfDay(8, 0),
    val endTime: TimeOfDay = TimeOfDay(18, 0),
    val observations: String = "",
    val isEmergency: Boolean = false,

    val location: GeoPoint? = null,
    val address: String = ""
) {
    val emailError: String?
        get() = if (email.isNotEmpty() && !EmailRegex.matches(email)) "Ingresa un correo válido" else null

    val phoneError: String?
        get() = if (phone.isNotEmpty() && phone.length != 10) "El teléfono debe tener 10 dígitos" else null

    val isReporterStepValid: Boolean
        get() = isAnonymous || (name.isNotBlank() && (email.isNotEmpty() || phone.isNotEmpty()) && emailError == null && phoneError == null)

    val childrenCountError: String?
        get() = if (childrenCount.isNotEmpty() && (childrenCount.toIntOrNull() ?: 0) < 1) {
            "Ingresa un número válido"
        } else null

    val isDetailsStepValid: Boolean
        get() = (childrenCount.toIntOrNull() ?: 0) >= 1 && childrenAges.isNotBlank() && workType != null && observations.isNotBlank() && location != null && address.isNotBlank()

    override fun toString(): String = "ReportDraft(redacted)"
}