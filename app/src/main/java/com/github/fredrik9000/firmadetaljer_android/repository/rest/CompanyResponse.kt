package com.github.fredrik9000.firmadetaljer_android.repository.rest

import companydb.CompanyEntity

sealed class CompanyResponse {
    class Success(val companyEntity: CompanyEntity) : CompanyResponse()
    class Error(val error: Throwable) : CompanyResponse()
}