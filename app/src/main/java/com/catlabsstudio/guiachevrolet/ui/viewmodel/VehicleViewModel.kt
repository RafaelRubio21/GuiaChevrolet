package com.catlabsstudio.guiachevrolet.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.catlabsstudio.guiachevrolet.data.local.entity.VehicleEntity
import com.catlabsstudio.guiachevrolet.data.repository.ChevroletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface StartupDestination {
    data object Loading : StartupDestination
    data object Onboarding : StartupDestination
    data object Home : StartupDestination
}

class VehicleViewModel(
    private val repository: ChevroletRepository
) : ViewModel() {

    private val _startupDestination = MutableStateFlow<StartupDestination>(StartupDestination.Loading)
    val startupDestination: StateFlow<StartupDestination> = _startupDestination.asStateFlow()

    val vehicles: StateFlow<List<VehicleEntity>> = repository.allVehicles
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        checkInitialRoute()
    }

    private fun checkInitialRoute() {
        viewModelScope.launch {
            val hasVehicles = repository.hasRegisteredVehicles()
            _startupDestination.value = if (hasVehicles) {
                StartupDestination.Home
            } else {
                StartupDestination.Onboarding
            }
        }
    }

    fun addVehicle(
        model: String,
        year: Int,
        nickname: String?,
        onSuccess: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val cleanNickname = nickname?.trim()?.takeIf { it.isNotEmpty() }
            val newId = repository.insertVehicle(
                VehicleEntity(
                    model = model,
                    year = year,
                    nickname = cleanNickname
                )
            )
            _startupDestination.value = StartupDestination.Home
            onSuccess(newId)
        }
    }

    fun updateVehicle(
        vehicle: VehicleEntity,
        model: String,
        year: Int,
        nickname: String?,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val cleanNickname = nickname?.trim()?.takeIf { it.isNotEmpty() }
            repository.updateVehicle(
                vehicle.copy(
                    model = model,
                    year = year,
                    nickname = cleanNickname
                )
            )
            onComplete()
        }
    }

    fun deleteVehicle(vehicle: VehicleEntity) {
        viewModelScope.launch {
            repository.deleteVehicle(vehicle)
        }
    }

    companion object {
        fun provideFactory(repository: ChevroletRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return VehicleViewModel(repository) as T
                }
            }
    }
}
