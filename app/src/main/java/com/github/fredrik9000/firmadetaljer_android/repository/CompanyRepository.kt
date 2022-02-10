package com.github.fredrik9000.firmadetaljer_android.repository

import com.github.fredrik9000.firmadetaljer_android.company_list.NumberOfEmployeesFilter
import com.github.fredrik9000.firmadetaljer_android.repository.data.CompanyDataSource
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyDTO
import companydb.CompanyEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class CompanyRepository @Inject constructor(private val companyDataSource: CompanyDataSource,
                                                 private val service: CompanyService) {

    val savedCompanies = companyDataSource.getCompanies()

    // When a company is updated it should appear at the top of the viewed companies list.
    // In order to achieve this the company (if exists) is deleted before being inserted again.
    suspend fun upsert(companyEntity: CompanyEntity) {
        companyDataSource.deleteCompanyByOrgNumber(companyEntity.organisasjonsnummer)
        companyDataSource.insertCompany(companyEntity)
    }

    suspend fun getCompanyByOrgNumber(orgNumber: Int): CompanyEntity? {
        return companyDataSource.getCompanyByOrgNumber(orgNumber)
    }

    suspend fun deleteAllCompanies() {
        companyDataSource.deleteAllCompanies()
    }

    suspend fun searchForCompaniesByName(name: String, selectedNumberOfEmployeesFilter: NumberOfEmployeesFilter): CompanyListResponse {
        return try {
            val companyWrapperEmbeddedResponse = when (selectedNumberOfEmployeesFilter) {
                NumberOfEmployeesFilter.ALL_EMPLOYEES -> service.getCompanies(name, null, null)
                NumberOfEmployeesFilter.LESS_THAN_6 -> service.getCompanies(name, null, 5)
                NumberOfEmployeesFilter.BETWEEN_5_AND_201 -> service.getCompanies(name, 6, 200)
                NumberOfEmployeesFilter.MORE_THAN_200 -> service.getCompanies(name, 201, null)
            }

            val embeddedCompaniesDTO = companyWrapperEmbeddedResponse.embedded ?: return CompanyListResponse.Success(listOf())

            return CompanyListResponse.Success(embeddedCompaniesDTO.enheter!!.filter { it.organisasjonsnummer != null }.map { createCompanyFromDTO(it) })
        } catch (e: Exception) {
            CompanyListResponse.Error(e)
        }
    }

    suspend fun searchForCompaniesByOrgNumber(orgNumber: Int): CompanyListResponse {
        try {
            service.getCompanyWithOrgNumber(orgNumber)?.let {
                return CompanyListResponse.Success(listOf(createCompanyFromDTO(it)))
            } ?: run {
                return CompanyListResponse.Error(Exception(OK_STATUS_BUT_NO_BODY_ERROR))
            }
        } catch (e: Exception) {
            return CompanyListResponse.Error(e)
        }
    }

    suspend fun searchForCompanyWithOrgNumber(orgNumber: Int): CompanyResponse {
        try {
            service.getCompanyWithOrgNumber(orgNumber)?.let {
                return CompanyResponse.Success(createCompanyFromDTO(it))
            } ?: run {
                return CompanyResponse.Error(Exception(OK_STATUS_BUT_NO_BODY_ERROR))
            }
        } catch (e: Exception) {
            return CompanyResponse.Error(e)
        }
    }

    private fun createCompanyFromDTO(companyDTO: CompanyDTO): CompanyEntity {
        return CompanyEntity(0, companyDTO.organisasjonsnummer!!.toInt(), companyDTO.navn, companyDTO.stiftelsesdato,
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