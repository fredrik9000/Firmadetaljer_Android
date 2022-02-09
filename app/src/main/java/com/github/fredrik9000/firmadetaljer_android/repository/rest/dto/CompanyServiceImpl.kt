package com.github.fredrik9000.firmadetaljer_android.repository.rest.dto

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService
import io.ktor.client.*
import io.ktor.client.request.*

class CompanyServiceImpl(private val client: HttpClient): CompanyService {

    override suspend fun getCompanies(navn: String, fraAntallAnsatte: Int?, tilAntallAnsatte: Int?): CompanyWrapperEmbeddedDTO {
        return client.get {
            url(BASE_URL)
            parameter("navn", navn)
            parameter("fraAntallAnsatte", fraAntallAnsatte)
            parameter("tilAntallAnsatte", tilAntallAnsatte)
        }
    }

    override suspend fun getCompanyWithOrgNumber(orgNumber: Int): CompanyDTO? {
        return client.get {
            url("$BASE_URL/$orgNumber")
        }
    }

    companion object {
        private const val BASE_URL = "https://data.brreg.no/enhetsregisteret/api/enheter"
    }
}