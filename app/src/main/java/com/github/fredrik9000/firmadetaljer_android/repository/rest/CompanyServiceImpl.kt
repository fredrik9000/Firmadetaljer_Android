package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyDTO
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyWrapperEmbeddedDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class CompanyServiceImpl(private val client: HttpClient) : CompanyService {

    override suspend fun getCompanies(
        navn: String,
        fraAntallAnsatte: Int?,
        tilAntallAnsatte: Int?
    ): CompanyWrapperEmbeddedDTO {
        return client.get {
            url(BASE_URL)
            parameter("navn", navn)
            parameter("fraAntallAnsatte", fraAntallAnsatte)
            parameter("tilAntallAnsatte", tilAntallAnsatte)
        }.body()
    }

    override suspend fun getCompanyWithOrgNumber(orgNumber: Int): CompanyDTO? {
        return client.get {
            url("$BASE_URL/$orgNumber")
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://data.brreg.no/enhetsregisteret/api/enheter"
    }
}