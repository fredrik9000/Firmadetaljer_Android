package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

class CompanyListResponse {
    var companies: List<Company>? = null
    var error: Throwable? = null

    constructor(companies: List<Company>) {
        this.companies = companies
        this.error = null
    }

    constructor(error: Throwable) {
        this.error = error
        this.companies = null
    }
}
