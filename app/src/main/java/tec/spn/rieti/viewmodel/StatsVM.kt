package tec.spn.rieti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tec.spn.rieti.model.StatsRepository

/**
 * Represents the different UI states for the statistics screen.
 */
sealed interface StatsUiState {
    /** State indicating that statistics are currently being loaded. */
    data object Loading : StatsUiState

    /**
     * State indicating that statistics were successfully loaded.
     *
     * @property reportsProcessed The total number of reports processed.
     * @property minorsProtected The total number of minors protected.
     */
    data class Success(
        val reportsProcessed: Int,
        val minorsProtected: Int
    ) : StatsUiState

    /** State indicating that an error occurred while loading statistics. */
    data object Error : StatsUiState
}

/**
 * ViewModel responsible for managing statistics state and fetching statistics data.
 *
 * @property repository The repository used to fetch statistics data.
 */
class StatsVM(
    private val repository: StatsRepository = StatsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)

    /** Flow exposing the current statistics UI state. */
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    /**
     * Fetches statistics from the repository and updates the UI state accordingly.
     */
    fun loadStats() {
        _uiState.value = StatsUiState.Loading
        viewModelScope.launch {
            repository.getStats()
                .onSuccess { response ->
                    _uiState.value = StatsUiState.Success(
                        reportsProcessed = response.reportsProcessed,
                        minorsProtected = response.minorsProtected
                    )
                }
                .onFailure {
                    _uiState.value = StatsUiState.Error
                }
        }
    }
}