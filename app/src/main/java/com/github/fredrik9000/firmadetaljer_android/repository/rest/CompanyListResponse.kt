package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

sealed class CompanyListResponse {
    class Success(val companies: List<Company>) : CompanyListResponse()
    class Error(private val error: Throwable) : CompanyListResponse() {
        private var isErrorHandled = false

        // Prevents observer from handling the same error multiple times
        fun getErrorIfNotHandled(): Throwable? {
            return if (!isErrorHandled) {
                isErrorHandled = true
                error
            } else {
                null
            }
        }

        fun peekError() = error
    }
}
