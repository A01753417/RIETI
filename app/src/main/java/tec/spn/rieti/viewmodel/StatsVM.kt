package tec.spn.rieti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tec.spn.rieti.model.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Represents the different UI loading states for the statistics screen
 */
sealed interface StatsUiState {
    /** Indicates that data is currently being fetched */
    data object Loading : StatsUiState

    /** Indicates that data was successfully loaded */
    data class Success(
        val reportsProcessed: Int,
        val minorsProtected: Int
    ) : StatsUiState

    /** Indicates that an error occurred while fetching data */
    data object Error : StatsUiState
}

/**
 * ViewModel responsible for fetching and managing statistics data for the statistics screen
 *
 * @param repository The data repository used to fetch stats. Defaults to a new instance of [StatsRepository]
 */
class StatsVM(private val repository: StatsRepository = StatsRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<StatsUiState>(StatsUiState.Loading)

    /** Flow exposing the current statistics UI state */
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadStats()
    }

    /**
     * Fetches statistics data from the repository and updates the UI state accordingly
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