package com.example.payn.core.domain

sealed interface DataError : Error {
    sealed interface Remote : DataError {
        data class BAD_REQUEST(val message: String, val formErrors: FormErrors?) : Remote
        data object REQUEST_TIMEOUT : Remote
        data object TOO_MANY_REQUESTS : Remote
        data object NO_INTERNET : Remote
        data object SERVER : Remote
        data object SERIALIZATION : Remote
        data object UNKNOWN : Remote
    }

    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN
    }
}