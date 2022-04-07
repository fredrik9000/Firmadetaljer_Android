package com.github.fredrik9000.firmadetaljer_android.repository.rest

import companydb.CompanyEntity

sealed class CompanyListResponse {
    class Success(val companyEntities: List<CompanyEntity>) : CompanyListResponse()
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