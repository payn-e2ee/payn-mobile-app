package com.example.payn.profile.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.payn.app.Route
import com.example.payn.core.domain.onSuccess
import com.example.payn.profile.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val profileId = savedStateHandle.toRoute<Route.Profile>().username

    private val _state = MutableStateFlow(ProfileState())
    val state = _state
        .onStart {
            profileRepository
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private fun fetchProfile() {
        viewModelScope.launch {
            profileRepository
                .getProfileById(profileId)
                .onSuccess { description ->
                    _state.update {
                        it.copy(
                            id = it.id,
                            name = it.name
                        )
                    }
                }
        }
    }
}