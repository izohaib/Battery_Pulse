package com.example.battery_pulse.feature.history.presentaion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battery_pulse.feature.history.domain.model.ChargingSession
import com.example.battery_pulse.feature.history.domain.usecase.GetChargingHistoryUseCase
import com.example.battery_pulse.feature.history.domain.usecase.SaveChargingSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getChargingHistoryUseCase: GetChargingHistoryUseCase,
    private val saveChargingSessionUseCase: SaveChargingSessionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // Tracks pending deletes: sessionId → coroutine job that will commit the delete
    private val pendingDeletes = mutableMapOf<Int, Job>()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            getChargingHistoryUseCase().collect { sessions ->
                _uiState.value = when {
                    sessions.isEmpty() -> HistoryUiState.Empty
                    else -> HistoryUiState.Success(sessions)
                }
            }
        }
    }

    /**
     * Schedules a delete after 5 seconds, allowing undo within that window.
     * The item is removed from the UI immediately via the list state,
     * but the DB delete is deferred.
     */
    fun deleteSession(id: Int) {
        // Cancel any previous pending delete for same id (safety)
        pendingDeletes[id]?.cancel()

        // Remove from UI immediately
        val currentState = _uiState.value
        if (currentState is HistoryUiState.Success) {
            val updated = currentState.sessions.filter { it.id != id }
            _uiState.value = if (updated.isEmpty()) HistoryUiState.Empty
            else HistoryUiState.Success(updated)
        }



        // Commit to DB after 5 seconds
        val job = viewModelScope.launch {
            delay(5_000)
            saveChargingSessionUseCase.deleteSession(id)
            pendingDeletes.remove(id)
        }
        pendingDeletes[id] = job
    }

    /**
     * Cancels the pending DB delete and re-inserts the session into the UI list.
     */
    fun undoDelete(session: ChargingSession) {
        val job = pendingDeletes.remove(session.id) ?: return
        job.cancel()

        // Re-insert into the current list, sorted by startTime descending
        val currentState = _uiState.value
        val currentList = if (currentState is HistoryUiState.Success) currentState.sessions
        else emptyList()
        val restored = (currentList + session).sortedByDescending { it.startTime }
        _uiState.value = HistoryUiState.Success(restored)
    }
}