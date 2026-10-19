package tec.spn.rieti.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Represents a single statistical metric item.
 *
 * @property value The numerical or text value of the stat.
 * @property label The description label for the stat.
 */
data class StatItem(
    val value: String,
    val label: String
)

/**
 * UI State holder for the main dashboard screen.
 *
 * @property stats List of statistical items to display on the main screen.
 */
data class MainUiState(
    val stats: List<StatItem> = emptyList()
)

/**
 * ViewModel responsible for managing and providing state to the main menu screen.
 */
class MainViewModel : ViewModel() {

    // API Placeholder if ever necessary
    private val _uiState = MutableStateFlow(
        MainUiState(
            stats = listOf(
                StatItem("24/7", "Disponibilidad"),
                StatItem("14", "Municipios cubiertos"),
                StatItem("100%", "Confidencial"),
                StatItem("3", "Canales de comunicación")
            )
        )
    )

    /** Flow to expose current dashboard UI state. */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
}