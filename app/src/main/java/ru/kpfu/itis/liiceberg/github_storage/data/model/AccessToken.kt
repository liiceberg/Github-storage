package ru.kpfu.itis.liiceberg.github_storage.data.model

import java.time.LocalDate

data class AccessToken(
    val value: String,
    val activePeriod: LocalDate
)