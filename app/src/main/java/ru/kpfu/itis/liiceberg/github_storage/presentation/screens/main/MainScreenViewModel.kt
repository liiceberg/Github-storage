package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetFolderUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetRepositoryUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetTokenUseCase
import ru.kpfu.itis.liiceberg.github_storage.presentation.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getFolderUseCase: GetFolderUseCase,
    private val getTokenUseCase: GetTokenUseCase,
) : BaseViewModel<MainScreenState, MainScreenEvent, MainScreenAction>(
    MainScreenState()
) {
    init {
        initAll()
    }

    override fun obtainEvent(event: MainScreenEvent) {
        when(event) {
            MainScreenEvent.OnPullClicked -> pull()
            MainScreenEvent.OnPushClicked -> push()
        }
    }

    private fun initAll() {
        viewModelScope.launch {
            viewState =
                viewState.copy(
                    repository = getRepositoryUseCase.invoke(),
                    folderPath = getFolderUseCase.invoke(),
                    accessDate = getTokenUseCase.invoke()?.activePeriod
                )
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