package ru.kpfu.itis.liiceberg.github_storage.data.exception

class RepositoryNotFoundException(message: String) : Exception(message)
class AccessDeniedException(message: String) : Exception(message)
class ForbiddenException(message: String) : Exception(message)
class ConflictException(message: String) : Exception(message)
class NetworkException(message: String) : Exception(message)