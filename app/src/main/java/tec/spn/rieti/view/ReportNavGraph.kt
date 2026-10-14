package tec.spn.rieti.view

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import tec.spn.rieti.model.ReportDraft
import tec.spn.rieti.viewmodel.ReportFormVM

object ReportRoutes {
    const val GRAPH = "report_flow"
    const val REPORTER = "report_flow/reporter"
    const val DETAILS = "report_flow/details"
    const val SUMMARY = "report_flow/summary"

}

fun NavGraphBuilder.reportFlowGraph(navController: NavHostController) {
    navigation(
        startDestination = ReportRoutes.REPORTER,
        route = ReportRoutes.GRAPH
    ) {
        composable(ReportRoutes.REPORTER) { entry ->
            val viewModel = rememberReportFormViewModel(navController, entry)
            val draft by viewModel.draft.collectAsStateWithLifecycle()

            ReporterStepScreen(
                draft = draft,
                onAnonymousChange = viewModel::onAnonymousChange,
                onNameChange = viewModel::onNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPhoneChange = viewModel::onPhoneChange,
                onNext = { navController.navigate(ReportRoutes.DETAILS) { launchSingleTop = true } },
                onDiscard = { navController.exitReportFlow() }
            )
        }

        composable(ReportRoutes.DETAILS) { entry ->
            val viewModel = rememberReportFormViewModel(navController, entry)
            val draft by viewModel.draft.collectAsStateWithLifecycle()

            StepPlaceholder(
                step = 2,
                title = "Información general (pendiente)",
                draft = draft,
                buttonText = "Siguiente",
                buttonIcon = Icons.AutoMirrored.Filled.ArrowForward,
                onBack = { navController.popBackStack() },
                onDiscard = { navController.exitReportFlow() },
                onPrimaryClick = { navController.navigate(ReportRoutes.SUMMARY) { launchSingleTop = true } }
            )
        }

        composable(ReportRoutes.SUMMARY) { entry ->
            val viewModel = rememberReportFormViewModel(navController, entry)
            val draft by viewModel.draft.collectAsStateWithLifecycle()

            StepPlaceholder(
                step = 3,
                title = "Resumen (pendiente)",
                draft = draft,
                buttonText = "Enviar reporte",
                buttonIcon = Icons.AutoMirrored.Filled.Send,
                onBack = { navController.popBackStack() },
                onDiscard = { navController.exitReportFlow() },
                onPrimaryClick = { TODO() }
            )
        }
    }
}

@Composable
private fun rememberReportFormViewModel(
    navController: NavHostController,
    entry: NavBackStackEntry
): ReportFormVM {
    val graphEntry = remember(entry) { navController.getBackStackEntry(ReportRoutes.GRAPH) }
    return viewModel<ReportFormVM>(graphEntry)
}


private fun NavHostController.exitReportFlow() {
    popBackStack(ReportRoutes.GRAPH, inclusive = true)
}

@Composable
private fun StepPlaceholder(
    step: Int,
    title: String,
    draft: ReportDraft,
    buttonText: String,
    buttonIcon: ImageVector,
    onBack: () -> Unit,
    onDiscard: () -> Unit,
    onPrimaryClick: () -> Unit
) {
    ReportStepScaffold(
        currentStep = step,
        onBack = onBack,
        onDiscard = onDiscard,
        primaryButtonText = buttonText,
        primaryButtonIcon = buttonIcon,
        onPrimaryClick = onPrimaryClick
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        Text("Anónimo: ${draft.isAnonymous}")
        Text("Nombre: ${draft.name}")
        Text("Correo: ${draft.email}")
        Text("Teléfono: ${draft.phone}")
    }
}