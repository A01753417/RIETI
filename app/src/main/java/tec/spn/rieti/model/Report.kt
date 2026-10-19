package tec.spn.rieti.model

/**
 * The 8 statuses of a report defined by SIPINNA
 *
 * @property [apiValue] is the exact string stored in the SQL enum
 * @property [label] is what the user sees as the status
 * @property [description] is the text shown in the status card
 */
enum class ReportStatus(
    val apiValue: String,
    val label: String,
    val description: String
) {
    REGISTRADO(
        apiValue = "Registrado",
        label = "Registrado",
        description = "Expediente recién creado o registrado en el sistema. Pronto entrará en revisión por un funcionario público que analizará el caso y la información proporcionada."
    ),
    EN_REVISION(
        apiValue = "En revision",
        label = "En revisión",
        description = "La información del reporte esta siendo revisada por un funcionario público para evaluar el caso y empezar a tomar las medidas necesarias para darle seguimiento."
    ),
    EN_SEGUIMIENTO(
        apiValue = "En seguimiento",
        label = "En seguimiento",
        description = "El caso de tu reporte ya ha sido evaluado y esta en proceso de ser atendido; Un funcionario público ya esta tomando medidas para atender la situación reportada y encontrar una resolución."
    ),
    CANALIZADO(
        apiValue = "Canalizado",
        label = "Canalizado",
        description = "En base a la evaluación de un funcionario público, el expediente fue enviado a otra área o institución para su atención."
    ),
    CONCLUIDO(
        apiValue = "Concluido",
        label = "Concluido",
        description = "Las acciones correspondientes para darle resolución fueron realizadas y el expediente puede cerrarse."
    ),
    ARCHIVADO(
        apiValue = "Archivado",
        label = "Archivado",
        description = "El caso asociado al reporte ha concluido y pasa al archivo para consulta posterior."
    ),
    CANCELADO(
        apiValue = "Cancelado",
        label = "Cancelado",
        description = "En base a la evaluación de un funcionario público, el expediente ha dejado de tener continuidad por alguna causa justificada."
    ),
    REINCIDENTE(
        apiValue = "Reincidente",
        label = "Reincidente",
        description = "El caso asociado al reporte había previamente concluido, pero a vuelto a requerir atención por alguna situación."
    );

    companion object {
        fun fromApi(value: String): ReportStatus? =
            entries.firstOrNull { it.apiValue.equals(value.trim(), ignoreCase = true) }
    }
}

/** Data class the UI works with after being processed in the repository */
data class Report(
    val folio: String,
    val status: ReportStatus,
    val createdAt: String
)

