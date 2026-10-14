package tec.spn.rieti.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tec.spn.rieti.model.Report
import tec.spn.rieti.model.ReportNotFoundException
import tec.spn.rieti.model.ReportRepository
import tec.spn.rieti.model.StatsRepository

/**
 * Represents the different UI loading states for the query result screen
 */
sealed interface QueryResultUiState {
    /** Indicates that data is currently being fetched */
    data object Loading : QueryResultUiState
    /** Indicates that data was found */
    data class Found(val report: Report) : QueryResultUiState
    /** Indicates that data was not found */
    data object NotFound : QueryResultUiState
    /** Indicates that an error occured when fetching the data*/
    data object Error : QueryResultUiState
}

/**
 * ViewModel responsible for fetching and managing query results data for the query results screen
 *
 * @param savedStateHandle Saved state holding the user query from the query screen
 * @param repository The data repository used to fetch stats. Defaults to a new instance of [ReportRepository]
 */
class QueryResultVM @JvmOverloads constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ReportRepository = ReportRepository()
) : ViewModel() {

    private val folio: String = checkNotNull(savedStateHandle["folio"])

    private val _uiState = MutableStateFlow<QueryResultUiState>(QueryResultUiState.Loading)
    val uiState: StateFlow<QueryResultUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    fun loadReport() {
        _uiState.value = QueryResultUiState.Loading
        viewModelScope.launch {
            repository.getReport(folio)
                .onSuccess { report ->
                    _uiState.value = QueryResultUiState.Found(report)
                }
                .onFailure { error ->
                    _uiState.value =
                        if (error is ReportNotFoundException) QueryResultUiState.NotFound
                        else QueryResultUiState.Error
                }
        }
    }
}