package tec.spn.rieti.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import tec.spn.rieti.view.DetailsStepScreen
import tec.spn.rieti.view.LocationPickerScreen
import tec.spn.rieti.view.ReportErrorScreen
import tec.spn.rieti.view.ReportSuccessScreen
import tec.spn.rieti.view.ReporterStepScreen
import tec.spn.rieti.view.SummaryStepScreen
import tec.spn.rieti.viewmodel.ReportFormVM
import tec.spn.rieti.viewmodel.ReportSubmitEvent

object ReportRoutes {
    const val GRAPH = "report_flow"
    const val REPORTER = "report_flow/reporter"
    const val DETAILS = "report_flow/details"
    const val SUMMARY = "report_flow/summary"
    const val PICKER = "report_flow/location_picker"
    const val ERROR = "report_flow/error"

    // Outside the graph, so the form is gone from the back stack once the report is
    // sent. The folio is optional because anonymous reports don't get one.
    const val SUCCESS = "report_success?folio={folio}"

    fun successRoute(folio: String?): String =
        if (folio == null) "report_success" else "report_success?folio=${Uri.encode(folio)}"
}

/**
 * Registers the whole report flow.
 * In your NavHost: reportFlowGraph(navController)
 * To open it:      navController.navigate(ReportRoutes.GRAPH)
 */
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

            DetailsStepScreen(
                draft = draft,
                onChildrenCountChange = viewModel::onChildrenCountChange,
                onAgeRangeToggled = viewModel::onAgeRangeToggled,
                onWorkTypeChange = viewModel::onWorkTypeChange,
                onStartTimeChange = viewModel::onStartTimeChange,
                onEndTimeChange = viewModel::onEndTimeChange,
                onObservationsChange = viewModel::onObservationsChange,
                onAddressChange = viewModel::onAddressChange,
                onLocationSelected = viewModel::onLocationSelected,
                onOpenMapPicker = {
                    navController.navigate(ReportRoutes.PICKER) { launchSingleTop = true }
                },
                onEmergencyChange = viewModel::onEmergencyChange,
                onNext = { navController.navigate(ReportRoutes.SUMMARY) { launchSingleTop = true } },
                onBack = { navController.popBackStack() },
                onDiscard = { navController.exitReportFlow() }
            )
        }

        composable(ReportRoutes.PICKER) { entry ->
            val viewModel = rememberReportFormViewModel(navController, entry)
            val draft by viewModel.draft.collectAsStateWithLifecycle()

            LocationPickerScreen(
                initialLocation = draft.location,
                onConfirm = { point, address ->
                    viewModel.onLocationSelected(point, address)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(ReportRoutes.SUMMARY) { entry ->
            val viewModel = rememberReportFormViewModel(navController, entry)
            val draft by viewModel.draft.collectAsStateWithLifecycle()
            val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

            // Reacts to the result of a submission, exactly once per event
            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ReportSubmitEvent.Success -> navController.navigate(
                            ReportRoutes.successRoute(event.folio)
                        ) {
                            // Removes the whole form (and its ViewModel) from the back stack
                            popUpTo(ReportRoutes.GRAPH) { inclusive = true }
                        }

                        ReportSubmitEvent.Failure -> navController.navigate(ReportRoutes.ERROR) {
                            launchSingleTop = true
                        }
                    }
                }
            }

            SummaryStepScreen(
                draft = draft,
                isSubmitting = isSubmitting,
                // The steps are still on the back stack with their data, so "Editar"
                // just pops back to the right one
                onEditReporter = { navController.popBackStack(ReportRoutes.REPORTER, inclusive = false) },
                onEditDetails = { navController.popBackStack(ReportRoutes.DETAILS, inclusive = false) },
                onSend = viewModel::submit,
                onBack = { navController.popBackStack() },
                onDiscard = { navController.exitReportFlow() }
            )
        }

        composable(ReportRoutes.ERROR) { entry ->
            val viewModel = rememberReportFormViewModel(navController, entry)

            ReportErrorScreen(
                onRetry = {
                    // Starts the request first (the summary will show the spinner),
                    // then returns to it
                    viewModel.submit()
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }

    // Not inside the graph on purpose
    composable(
        route = ReportRoutes.SUCCESS,
        arguments = listOf(
            navArgument("folio") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { entry ->
        ReportSuccessScreen(
            folio = entry.arguments?.getString("folio"),
            // The form was popped off the stack, so this lands on the main menu
            onFinish = { navController.popBackStack() }
        )
    }
}

/**
 * Returns the ViewModel owned by the nested graph's back stack entry, so every
 * screen inside the graph gets the SAME instance (and the same draft).
 */
@Composable
private fun rememberReportFormViewModel(
    navController: NavHostController,
    entry: NavBackStackEntry
): ReportFormVM {
    val graphEntry = remember(entry) { navController.getBackStackEntry(ReportRoutes.GRAPH) }
    return viewModel<ReportFormVM>(graphEntry)
}

/** Pops the entire graph, which also destroys the shared ViewModel and its draft. */
private fun NavHostController.exitReportFlow() {
    popBackStack(ReportRoutes.GRAPH, inclusive = true)
}