package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

class CompanyResponse {
    var company: Company? = null
    var error: Throwable? = null

    constructor(company: Company) {
        this.company = company
        this.error = null
    }

    constructor(error: Throwable) {
        this.error = error
        this.company = null
    }
}