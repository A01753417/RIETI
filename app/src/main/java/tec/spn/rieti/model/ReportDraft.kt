package tec.spn.rieti.model

import java.util.Locale

private val EmailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

/**
 * Work types the reporter can choose from.
 * [apiValue] is exactly the string of the database ENUM.
 * [label] is what the user sees.
 */
enum class WorkType(val apiValue: String, val label: String) {
    VENTA_AMBULANTE("Venta_ambulante", "Venta ambulante"),
    LIMPIEZA_PARABRISAS("Limpieza_de_parabrisas", "Limpieza de parabrisas"),
    MENDICIDAD("Mendicidad", "Mendicidad"),
    CARGA_DESCARGA("Carga_y_descarga", "Carga y descarga"),
    TRABAJO_COMERCIO("Trabajo_en_comercio", "Trabajo en comercio"),
    CAMPO("Campo", "Campo"),
    CONSTRUCCION("Construccion", "Construcción"),
    TRABAJO_DOMESTICO("Trabajo_domestico", "Trabajo doméstico"),
    RECOLECCION_RESIDUOS("Recoleccion_de_residuos", "Recolección de residuos"),
    OTRA_ACTIVIDAD("Otra_actividad", "Otra actividad"),
    NO_SE("No_se", "No sé")
}

/** How many children are involved. The database stores a range, not a number. */
enum class ChildrenCount(val apiValue: String, val label: String) {
    ONE("1", "1"),
    TWO("2", "2"),
    THREE("3", "3"),
    FOUR("4", "4"),
    FIVE_OR_MORE("5_o_mas", "5 o más"),
    UNKNOWN("No_se", "No sé")
}

/** Age ranges. Several can apply to one report, except [UNKNOWN] which stands alone. */
enum class AgeRange(val apiValue: String, val label: String) {
    RANGE_0_5("0-5", "0 a 5 años"),
    RANGE_6_11("6-11", "6 a 11 años"),
    RANGE_12_14("12-14", "12 a 14 años"),
    RANGE_15_17("15-17", "15 a 17 años"),
    UNKNOWN("No_se", "No sé")
}

data class TimeOfDay(val hour: Int, val minute: Int) {

    /** For the UI, e.g. "8:00 AM" or "6:00 PM". */
    fun toDisplayString(): String {
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        val suffix = if (hour < 12) "AM" else "PM"
        return String.format(Locale.ROOT, "%d:%02d %s", hour12, minute, suffix)
    }

    fun toApiString(): String = String.format(Locale.ROOT, "%02d:%02d", hour, minute)
}

data class GeoPoint(val latitude: Double, val longitude: Double)

/**
 * Holds all the data entered by a user while creating a report draft.
 *
 * @property isAnonymous True if the report is submitted anonymously, false otherwise.
 * @property name The name of the reporter.
 * @property email The email address of the reporter.
 * @property phone The phone number of the reporter.
 * @property childrenCount The estimated number of children involved.
 * @property childrenAges The set of age ranges of the children involved.
 * @property workType The type of work observed.
 * @property startTime The starting time of the observed activity.
 * @property endTime The ending time of the observed activity.
 * @property observations Additional comments or details provided by the reporter.
 * @property isEmergency True if the situation is an emergency, false otherwise.
 * @property location The geographic coordinates of the incident.
 * @property address The text address corresponding to the location.
 */
data class ReportDraft(
    val isAnonymous: Boolean = false,
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val childrenCount: ChildrenCount? = null,
    val childrenAges: Set<AgeRange> = emptySet(),
    val workType: WorkType? = null,
    val startTime: TimeOfDay = TimeOfDay(8, 0),
    val endTime: TimeOfDay = TimeOfDay(18, 0),
    val observations: String = "",       // required
    val isEmergency: Boolean = false,

    val location: GeoPoint? = null,
    val address: String = ""
) {

    val emailError: String?
        get() = if (email.isNotEmpty() && !EmailRegex.matches(email)) "Ingresa un correo válido" else null

    val phoneError: String?
        get() = if (phone.isNotEmpty() && phone.length != 10) "El teléfono debe tener 10 dígitos" else null

    val isReporterStepValid: Boolean
        get() = isAnonymous || (
                name.isNotBlank() &&
                        (email.isNotEmpty() || phone.isNotEmpty()) &&
                        emailError == null &&
                        phoneError == null
                )

    val isDetailsStepValid: Boolean
        get() = childrenCount != null &&
                childrenAges.isNotEmpty() &&
                workType != null &&
                observations.isNotBlank() &&
                location != null &&
                address.isNotBlank()

    override fun toString(): String = "ReportDraft(redacted)"
}