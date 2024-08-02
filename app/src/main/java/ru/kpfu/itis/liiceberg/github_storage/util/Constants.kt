package ru.kpfu.itis.liiceberg.github_storage.util

const val GITHUB_URL_PATTERN = "https?:\\/\\/(www\\.)?github\\.com\\/[a-zA-Z0-9._-]+\\/[a-zA-Z0-9._-]+\\/?(\\S*)?"
const val FOLDER_PATH_PATTERN = "^[a-zA-Zа-яА-Я0-9]+[a-zA-Zа-яА-Я0-9/_]*\$"
const val GITHUB_API_ACCESS_TOKEN_PATTERN = "^(ghp|gho|ghu|ghs|ghe)_[A-Za-z0-9]{36}\$"