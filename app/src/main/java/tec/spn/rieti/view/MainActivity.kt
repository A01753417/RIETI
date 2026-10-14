package tec.spn.rieti.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object Routes {
    const val MAIN = "main"
    const val HELP = "help"
    const val QUERY = "query"
    const val RESULT_BASE = "result"
    const val RESULT = "$RESULT_BASE/{folio}"
    const val STATS = "stats"
    const val MORE = "more"

    fun resultRoute(folio: String) = "$RESULT_BASE/$folio"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.MAIN
                ) {
                    composable(Routes.MAIN) {
                        MainScreen(
                            onHelp = { navController.navigate(Routes.HELP) },
                            onNewReport = { navController.navigate(ReportRoutes.GRAPH) },
                            onTracking = { navController.navigate(Routes.QUERY) },
                            onStatistics = { navController.navigate(Routes.STATS) },
                            onMore = { navController.navigate(Routes.MORE) }
                        )
                    }
                    composable(Routes.HELP) {
                        HelpScreen(onBack = { navController.popBackStack() })
                    }
                    composable(Routes.QUERY) {
                        QueryScreen(
                            onBack = { navController.popBackStack() },
                            onSearch = { folio ->
                                navController.navigate(Routes.resultRoute(folio))
                            }
                        )
                    }
                    composable(
                        route = Routes.RESULT,
                        arguments = listOf(
                            navArgument("folio") { type = NavType.StringType }
                        )
                    ) {
                        QueryResultScreen(
                            onBack = { navController.popBackStack() },
                            viewModel = viewModel()
                        )
                    }
                    composable(Routes.STATS) {
                        StatsScreen(onBack = { navController.popBackStack() })
                    }
                    composable(Routes.MORE) {
                        InfoScreen(onBack = { navController.popBackStack() })
                    }
                    reportFlowGraph(navController)
                }
            }
        }
    }
}

