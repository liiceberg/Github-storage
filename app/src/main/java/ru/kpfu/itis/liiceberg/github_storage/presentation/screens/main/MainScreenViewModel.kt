package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main

import android.content.Context
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import ru.kpfu.itis.liiceberg.github_storage.R
import ru.kpfu.itis.liiceberg.github_storage.data.remote.model.GitStatus
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetChangesUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetFolderUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetHistoryUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetRepositoryUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetTokenUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GitHubPullUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GitHubPushUseCase
import ru.kpfu.itis.liiceberg.github_storage.presentation.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getFolderUseCase: GetFolderUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val gitHubPullUseCase: GitHubPullUseCase,
    private val gitHubPushUseCase: GitHubPushUseCase,
    private val getHistoryUseCase: GetHistoryUseCase,
    private val getChangesUseCase: GetChangesUseCase,
    @ApplicationContext private val context: Context
) : BaseViewModel<MainScreenState, MainScreenEvent, MainScreenAction>(
    MainScreenState()
) {
    init {
        initAll()
    }

    override fun obtainEvent(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnPullClicked -> pull()
            MainScreenEvent.OnPushClicked -> push()
        }
    }

    private fun initAll() {
        viewModelScope.launch {
            viewState =
                viewState.copy(
                    repository = getRepositoryUseCase.invoke(),
                    folderPath = getFolderUseCase.invoke(absolute = true),
                    accessDate = getTokenUseCase.invoke()?.activePeriod,
                    history = getHistoryUseCase.invoke(),
                    status = getChangesUseCase.invoke()
                )
        }
    }

    private fun push() {
        viewModelScope.launch {
            try {
                viewState = viewState.copy(pushLoading = true)
                gitHubPushUseCase.invoke()
                viewState = viewState.copy(
                    pushLoading = false,
                    history = getHistoryUseCase.invoke(),
                    status = GitStatus.empty()
                )
            } catch (ex: Exception) {
                viewAction = MainScreenAction.ShowError(
                    ex.message ?: context.resources.getString(R.string.unknown_error)
                )
                viewState = viewState.copy(
                    pushLoading = false,
                )
            }
        }
    }

    private fun pull() {
        viewModelScope.launch {
            try {
                viewState = viewState.copy(pullLoading = true)
                gitHubPullUseCase.invoke()
                viewState = viewState.copy(
                    pullLoading = false,
                    history = getHistoryUseCase.invoke(),
                    status = GitStatus.empty()
                )
            } catch (ex: Exception) {
                viewAction = MainScreenAction.ShowError(
                    ex.message ?: context.resources.getString(R.string.unknown_error)
                )
                viewState = viewState.copy(
                    pullLoading = false,
                )
            }
        }
    }
}