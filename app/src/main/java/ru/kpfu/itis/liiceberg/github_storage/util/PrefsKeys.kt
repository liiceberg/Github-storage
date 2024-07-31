package ru.kpfu.itis.liiceberg.github_storage.util

import androidx.datastore.preferences.preferencesKey

object PrefsKeys {
    val REPOSITORY_KEY = preferencesKey<String>("REPOSITORY_KEY")
    val FOLDER_KEY = preferencesKey<String>("FOLDER_KEY")
    val ACCESS_TOKEN_KEY = preferencesKey<String>("ACCESS_TOKEN_KEY")
    val ACCESS_TOKEN_ACTIVE_PERIOD_KEY = preferencesKey<Long>("ACCESS_TOKEN_ACTIVE_PERIOD_KEY")
}