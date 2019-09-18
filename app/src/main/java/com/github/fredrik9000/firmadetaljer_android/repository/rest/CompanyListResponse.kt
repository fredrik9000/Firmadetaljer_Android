package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

class CompanyListResponse {
    var companies: List<Company>? = null
    private var error: Throwable? = null
    private var isErrorHandled = false

    constructor(companies: List<Company>) {
        this.companies = companies
        this.error = null
    }

    constructor(error: Throwable) {
        this.error = error
        this.companies = null
    }

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
