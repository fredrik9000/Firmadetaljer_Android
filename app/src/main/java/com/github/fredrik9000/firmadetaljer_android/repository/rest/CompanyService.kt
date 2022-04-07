package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyDTO
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyWrapperEmbeddedDTO

interface CompanyService {
    suspend fun getCompanies(navn: String, fraAntallAnsatte: Int?, tilAntallAnsatte: Int?): CompanyWrapperEmbeddedDTO

    suspend fun getCompanyWithOrgNumber(orgNumber: Int): CompanyDTO?
}