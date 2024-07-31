package ru.kpfu.itis.liiceberg.github_storage.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.formatDate() : String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM")
    return this.format(formatter)
}