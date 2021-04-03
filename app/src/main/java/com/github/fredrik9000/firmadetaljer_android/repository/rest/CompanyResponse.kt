package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

sealed class CompanyResponse {
    class Success(val company: Company) : CompanyResponse()
    class Error(val error: Throwable) : CompanyResponse()
}