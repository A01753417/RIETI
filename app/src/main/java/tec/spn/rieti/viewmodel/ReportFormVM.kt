package tec.spn.rieti.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tec.spn.rieti.model.ReportRepository
import tec.spn.rieti.model.AgeRange
import tec.spn.rieti.model.ChildrenCount
import tec.spn.rieti.model.GeoPoint
import tec.spn.rieti.model.ReportDraft
import tec.spn.rieti.model.TimeOfDay
import tec.spn.rieti.model.WorkType

/** One-shot result of a submission attempt. Consumed exactly once by the summary screen. */
sealed interface ReportSubmitEvent {
    /** [folio] is null for anonymous reports. */
    data class Success(val folio: String?) : ReportSubmitEvent
    data object Failure : ReportSubmitEvent
}

class ReportFormVM(
    private val repository: ReportRepository = ReportRepository()
) : ViewModel() {

    private val _draft = MutableStateFlow(ReportDraft())

    /** Flow exposing the current report draft state. */
    val draft: StateFlow<ReportDraft> = _draft.asStateFlow()

    /**
     * Updates whether the report is submitted anonymously.
     */
    fun onAnonymousChange(isAnonymous: Boolean) {
        _draft.update {
            if (isAnonymous) {
                it.copy(isAnonymous = true, name = "", email = "", phone = "")
            } else {
                it.copy(isAnonymous = false)
            }
        }
    }

    /**
     * Updates the reporter's name.
     */
    fun onNameChange(value: String) {
        _draft.update { it.copy(name = value) }
    }

    /**
     * Updates the reporter's email, removing any whitespace.
     */
    fun onEmailChange(value: String) {
        _draft.update { it.copy(email = value.filterNot { c -> c.isWhitespace() }) }
    }

    /**
     * Updates the reporter's phone number, restricting it to digits and a maximum of 10 characters.
     */
    fun onPhoneChange(value: String) {
        _draft.update { it.copy(phone = value.filter { c -> c.isDigit() }.take(10)) }
    }

    /**
     * Updates the estimated number of children involved.
     */
    fun onChildrenCountChange(value: ChildrenCount) {
        _draft.update { it.copy(childrenCount = value) }
    }

    /**
     * Toggles an age range in the set of children ages.
     */
    fun onAgeRangeToggled(range: AgeRange) {
        _draft.update { draft ->
            val current = draft.childrenAges
            val updated = when {
                range == AgeRange.UNKNOWN ->
                    if (AgeRange.UNKNOWN in current) emptySet() else setOf(AgeRange.UNKNOWN)
                range in current -> current - range
                else -> (current - AgeRange.UNKNOWN) + range
            }
            draft.copy(childrenAges = updated)
        }
    }

    /**
     * Updates the selected work type.
     */
    fun onWorkTypeChange(value: WorkType) {
        _draft.update { it.copy(workType = value) }
    }

    /**
     * Updates the start time of the observed activity.
     */
    fun onStartTimeChange(value: TimeOfDay) {
        _draft.update { it.copy(startTime = value) }
    }

    /**
     * Updates the end time of the observed activity.
     */
    fun onEndTimeChange(value: TimeOfDay) {
        _draft.update { it.copy(endTime = value) }
    }

    /**
     * Updates additional observations, limited to 1000 characters.
     */
    fun onObservationsChange(value: String) {
        _draft.update { it.copy(observations = value.take(1000)) }
    }

    /**
     * Updates whether the situation is an emergency.
     */
    fun onEmergencyChange(value: Boolean) {
        _draft.update { it.copy(isEmergency = value) }
    }

    /**
    - Sets the chosen point. If
    [address] is not blank (reverse geocoding found one)
    - it replaces the address line; the user can still edit it afterwards.

     */
    fun onLocationSelected(value: GeoPoint, address: String? = null) {
        _draft.update {
            it.copy(
                location = value,
                address = if (address.isNullOrBlank()) it.address else address.take(200)
            )
        }
    }

    /**
     * Updates the address string, limited to 200 characters.
     */
    fun onAddressChange(value: String) {
        _draft.update { it.copy(address = value.take(200)) }
    }
    private val _isSubmitting = MutableStateFlow(false)

    /** Flow indicating whether a submission request is currently in progress. */
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()
    private val _events = Channel<ReportSubmitEvent>(Channel.BUFFERED)

    /** Flow exposing one-shot submission events. */
    val events: Flow<ReportSubmitEvent> = _events.receiveAsFlow()

    /**
     * Submits the report draft if validation passes and a submission is not already active.
     */
    fun submit() {
        if (_isSubmitting.value) return // ignore double taps

        val snapshot = _draft.value
        if (!snapshot.isReporterStepValid || !snapshot.isDetailsStepValid) return

        _isSubmitting.value = true
        viewModelScope.launch {
            val result = repository.submitReport(snapshot)
            _isSubmitting.value = false
            if (result.isSuccess) {
                _events.send(ReportSubmitEvent.Success(result.getOrNull()))
            } else {
                _events.send(ReportSubmitEvent.Failure)
            }
        }
    }
}