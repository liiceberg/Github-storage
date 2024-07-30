package ru.kpfu.itis.liiceberg.github_storage.presentation.screens.main

import ru.kpfu.itis.liiceberg.github_storage.presentation.base.UiEvent

sealed interface MainScreenEvent : UiEvent {
    data object OnPullClicked : MainScreenEvent
    data object OnPushClicked : MainScreenEvent
}