package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.kpfu.itis.liiceberg.github_storage.data.model.AccessToken
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.CheckFolderPathUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetFolderUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetRepositoryUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.GetTokenUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.SaveFolderUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.SaveRepositoryUseCase
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.SaveTokenUseCase
import ru.kpfu.itis.liiceberg.github_storage.presentation.base.BaseViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val pathValidator: CheckFolderPathUseCase,
    private val getRepositoryUseCase: GetRepositoryUseCase,
    private val getFolderUseCase: GetFolderUseCase,
    private val saveRepositoryUseCase: SaveRepositoryUseCase,
    private val saveFolderUseCase: SaveFolderUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
) : BaseViewModel<SettingsScreenState, SettingsScreenEvent, SettingsScreenAction>(
    SettingsScreenState()
) {

    init {
        initAll()
    }

    override fun obtainEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.OnRepositoryFilled -> {
                val value = event.repository
                val isValid = true
                viewState = viewState.copy(repository = value, repositoryNotValid = isValid.not())
            }

            is SettingsScreenEvent.OnFolderFilled -> {
                val value = event.path
                val isValid = pathValidator.invoke(value)
                viewState = viewState.copy(folderPath = value, folderPathNotValid = isValid.not())
            }

            is SettingsScreenEvent.OnAccessFilled -> {
                val value = event.access
                val isValid = true
                viewState = viewState.copy(access = value, accessNotValid = isValid.not())
            }

            is SettingsScreenEvent.OnDateSelected -> {
                viewState =
                    viewState.copy(accessActiveDate = event.date, showDatePickerDialog = false)
            }

            SettingsScreenEvent.OnDatePickerCalled -> {
                viewState = viewState.copy(showDatePickerDialog = true)
            }

            SettingsScreenEvent.OnDatePickerDismissed -> {
                viewState = viewState.copy(showDatePickerDialog = false)
            }

            SettingsScreenEvent.OnSave -> {
                with(viewState) {
                    saveData(repository, folderPath, access, accessActiveDate)
                }
            }
        }
    }

    private fun initAll() {
        viewModelScope.launch {
            val token = getTokenUseCase.invoke()
            viewState =
                viewState.copy(
                    repository = getRepositoryUseCase.invoke(),
                    folderPath = getFolderUseCase.invoke(),
                    access = token?.value ?: "",
                    accessActiveDate = token?.activePeriod
                )
        }
    }

    private fun saveData(
        repository: String,
        folder: String,
        token: String,
        activePeriod: LocalDate?
    ) {
        viewModelScope.launch {
            saveRepositoryUseCase.invoke(repository)
            saveFolderUseCase.invoke(folder)
            activePeriod?.let {
                saveTokenUseCase.invoke(AccessToken(token, activePeriod))
            }
        }
    }

}