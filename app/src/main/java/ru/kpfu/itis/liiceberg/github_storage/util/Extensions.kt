package ru.kpfu.itis.liiceberg.github_storage.util

import android.util.Base64
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.formatDate(): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM")
    return this.format(formatter)
}

fun String.decodeFromBase64(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(charset("UTF-8"))
}

fun String.encodeToBase64(): String {
    return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.DEFAULT)
}

fun List<String>.convertToString(separator: String): String {
    val builder = StringBuilder()
    this.forEachIndexed { index, item ->
        builder.append(item)
        if (index != this.lastIndex) {
            builder.append(separator)
        }
    }
    return builder.toString()
}