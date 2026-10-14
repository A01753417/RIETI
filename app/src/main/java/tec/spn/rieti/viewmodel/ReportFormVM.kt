package tec.spn.rieti.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import tec.spn.rieti.model.GeoPoint
import tec.spn.rieti.model.ReportDraft
import tec.spn.rieti.model.TimeOfDay
import tec.spn.rieti.model.WorkType

class ReportFormVM : ViewModel() {

    private val _draft = MutableStateFlow(ReportDraft())
    val draft: StateFlow<ReportDraft> = _draft.asStateFlow()

    fun onAnonymousChange(isAnonymous: Boolean) {
        _draft.update {
            if (isAnonymous) {
                it.copy(isAnonymous = true, name = "", email = "", phone = "")
            } else {
                it.copy(isAnonymous = false)
            }
        }
    }

    fun onNameChange(value: String) {
        _draft.update { it.copy(name = value) }
    }

    fun onEmailChange(value: String) {
        _draft.update { it.copy(email = value.filterNot { c -> c.isWhitespace() }) }
    }

    fun onPhoneChange(value: String) {
        _draft.update { it.copy(phone = value.filter { c -> c.isDigit() }.take(10)) }
    }

    fun onChildrenCountChange(value: String) {
        _draft.update { it.copy(childrenCount = value.filter { c -> c.isDigit() }.take(2)) }
    }

    fun onChildrenAgesChange(value: String) {
        _draft.update { it.copy(childrenAges = value.take(100)) }
    }

    fun onWorkTypeChange(value: WorkType) {
        _draft.update { it.copy(workType = value) }
    }

    fun onStartTimeChange(value: TimeOfDay) {
        _draft.update { it.copy(startTime = value) }
    }

    fun onEndTimeChange(value: TimeOfDay) {
        _draft.update { it.copy(endTime = value) }
    }

    fun onObservationsChange(value: String) {
        _draft.update { it.copy(observations = value.take(1000)) }
    }

    fun onEmergencyChange(value: Boolean) {
        _draft.update { it.copy(isEmergency = value) }
    }

    fun onLocationSelected(value: GeoPoint) {
        _draft.update { it.copy(location = value) }
    }

    fun onAddressChange(value: String) {
        _draft.update { it.copy(address = value.take(200)) }
    }

}