package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.kpfu.itis.liiceberg.github_storage.presentation.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(

) : BaseViewModel<MainScreenState, MainScreenEvent, MainScreenAction>(
    MainScreenState()
) {
    override fun obtainEvent(event: MainScreenEvent) {
        when(event) {
            MainScreenEvent.OnPullClicked -> pull()
            MainScreenEvent.OnPushClicked -> push()
        }
    }

    private fun push() {
        viewModelScope.launch {
            viewState = viewState.copy(pushLoading = true)
            delay(5000L)
            viewState = viewState.copy(pushLoading = false)
        }
    }

    private fun pull() {
        viewModelScope.launch {
            viewState = viewState.copy(pullLoading = true)
            delay(5000L)
            viewState = viewState.copy(pullLoading = false)
        }
    }
}