package com.github.fredrik9000.firmadetaljer_android.repository

import com.github.fredrik9000.firmadetaljer_android.company_list.NumberOfEmployeesFilter
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyDTO
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CompanyRepository @Inject constructor(private val companyDao: CompanyDao,
                                                 private val service: CompanyService) {

    val savedCompanies = companyDao.companiesOrderedByName

    // When a company is updated it should appear at the top of the viewed companies list.
    // In order to achieve this the company (if exists) is deleted before being inserted again.
    suspend fun upsert(company: Company) {
        companyDao.deleteByOrganizationNumber(company.organisasjonsnummer)
        companyDao.insert(company)
    }

    suspend fun deleteAllCompanies() {
        companyDao.deleteAll()
    }

    suspend fun searchForCompaniesByName(name: String, selectedNumberOfEmployeesFilter: NumberOfEmployeesFilter): CompanyListResponse {
        val response = when (selectedNumberOfEmployeesFilter) {
            NumberOfEmployeesFilter.ALL_EMPLOYEES -> service.getCompanies(name, null, null)
            NumberOfEmployeesFilter.LESS_THAN_6 -> service.getCompanies(name, null, 5)
            NumberOfEmployeesFilter.BETWEEN_5_AND_201 -> service.getCompanies(name, 6, 200)
            NumberOfEmployeesFilter.MORE_THAN_200 -> service.getCompanies(name, 201, null)
        }

        if (response.code() != 200) {
            return CompanyListResponse.Error(HttpException(response))
        } else {
            response.body()?.let { responseBody ->
                // If data is null that means no companies were found, so return an empty list
                val embeddedCompaniesDTO = responseBody.embedded
                        ?: return CompanyListResponse.Success(listOf())

                return CompanyListResponse.Success(embeddedCompaniesDTO.enheter!!.filter { it.organisasjonsnummer != null }.map { createCompanyFromDTO(it) })
            } ?: run {
                return CompanyListResponse.Error(Exception(OK_STATUS_BUT_NO_BODY_ERROR))
            }
        }
    }

    suspend fun searchForCompaniesByOrgNumber(orgNumber: Int): CompanyListResponse {
        val response = service.getCompanyWithOrgNumber(orgNumber)
        if (response.code() == 200) {
            response.body()?.let { responseBody ->
                return CompanyListResponse.Success(listOf(createCompanyFromDTO(responseBody)))
            } ?: run {
                return CompanyListResponse.Error(Exception(OK_STATUS_BUT_NO_BODY_ERROR))
            }
        } else {
            return CompanyListResponse.Error(HttpException(response))
        }
    }

    suspend fun searchForCompanyWithOrgNumber(orgNumber: Int): CompanyResponse {
        val response = service.getCompanyWithOrgNumber(orgNumber)
        if (response.code() == 200) {
            response.body()?.let { responseBody ->
                return CompanyResponse.Success(createCompanyFromDTO(responseBody))
            } ?: run {
                return CompanyResponse.Error(Exception(OK_STATUS_BUT_NO_BODY_ERROR))
            }
        } else {
            return CompanyResponse.Error(HttpException(response))
        }
    }

    private fun createCompanyFromDTO(companyDTO: CompanyDTO): Company {
        return Company(0, companyDTO.organisasjonsnummer!!.toInt(), companyDTO.navn, companyDTO.stiftelsesdato,
                companyDTO.registreringsdatoEnhetsregisteret, companyDTO.oppstartsdato, companyDTO.datoEierskifte,
                companyDTO.organisasjonsform?.beskrivelse, companyDTO.hjemmeside, companyDTO.registertIFrivillighetsregisteret,
                companyDTO.registrertIMvaregisteret, companyDTO.registrertIForetaksregisteret, companyDTO.registrertIStiftelsesregisteret,
                companyDTO.antallAnsatte, companyDTO.sisteInnsendteAarsregnskap, companyDTO.konkurs,
                companyDTO.underAvvikling, companyDTO.underTvangsavviklingEllerTvangsopplosning, companyDTO.overordnetEnhet?.toInt(),
                companyDTO.institusjonellSektorkode?.kode, companyDTO.institusjonellSektorkode?.beskrivelse,
                companyDTO.naeringskode1?.kode, companyDTO.naeringskode1?.beskrivelse,
                companyDTO.naeringskode2?.kode, companyDTO.naeringskode2?.beskrivelse,
                companyDTO.naeringskode3?.kode, companyDTO.naeringskode3?.beskrivelse,
                buildAddressString(companyDTO.postadresse?.adresse), companyDTO.postadresse?.postnummer,
                companyDTO.postadresse?.poststed, companyDTO.postadresse?.kommunenummer,
                companyDTO.postadresse?.kommune, companyDTO.postadresse?.landkode,
                companyDTO.postadresse?.land, buildAddressString(companyDTO.forretningsadresse?.adresse),
                companyDTO.forretningsadresse?.postnummer, companyDTO.forretningsadresse?.poststed,
                companyDTO.forretningsadresse?.kommunenummer, companyDTO.forretningsadresse?.kommune,
                companyDTO.forretningsadresse?.landkode, companyDTO.forretningsadresse?.land,
                buildAddressString(companyDTO.beliggenhetsadresse?.adresse), companyDTO.beliggenhetsadresse?.postnummer,
                companyDTO.beliggenhetsadresse?.poststed, companyDTO.beliggenhetsadresse?.kommunenummer,
                companyDTO.beliggenhetsadresse?.kommune, companyDTO.beliggenhetsadresse?.landkode,
                companyDTO.beliggenhetsadresse?.land)
    }

    private fun buildAddressString(addressList: List<String>?): String? {
        return if (addressList.isNullOrEmpty()) {
            null
        } else {
            addressList.joinToString()
        }
    }

    companion object {
        private const val OK_STATUS_BUT_NO_BODY_ERROR = "Response code is 200 but there's no response body!"
    }
}