package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.kpfu.itis.liiceberg.github_storage.domain.usecase.CheckFolderPathUseCase
import ru.kpfu.itis.liiceberg.github_storage.presentation.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val pathValidator: CheckFolderPathUseCase
) : BaseViewModel<SettingsScreenState, SettingsScreenEvent, SettingsScreenAction>(
    SettingsScreenState()
) {
    override fun obtainEvent(event: SettingsScreenEvent) {
        when(event) {
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
            SettingsScreenEvent.OnSave -> {}
        }
    }


}