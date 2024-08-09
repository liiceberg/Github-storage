package ru.kpfu.itis.liiceberg.github_storage.data.remote.pojo

import java.time.LocalDate

data class AccessToken(
    val value: String,
    val activePeriod: LocalDate
)