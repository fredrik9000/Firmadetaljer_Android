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
open class CompanyRepository @Inject constructor(
    private val companyDataSource: CompanyDataSource,
    private val service: CompanyService
) {

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
                NumberOfEmployeesFilter.ALL_EMPLOYEES -> service.getCompanies(navn = name, fraAntallAnsatte = null, tilAntallAnsatte = null)
                NumberOfEmployeesFilter.LESS_THAN_6 -> service.getCompanies(navn = name, fraAntallAnsatte = null, tilAntallAnsatte = 5)
                NumberOfEmployeesFilter.BETWEEN_5_AND_201 -> service.getCompanies(navn = name, fraAntallAnsatte = 6, tilAntallAnsatte = 200)
                NumberOfEmployeesFilter.MORE_THAN_200 -> service.getCompanies(navn = name, fraAntallAnsatte = 201, tilAntallAnsatte = null)
            }

            val embeddedCompaniesDTO = companyWrapperEmbeddedResponse.embedded ?: return CompanyListResponse.Success(listOf())

            return CompanyListResponse.Success(
                companyEntities = embeddedCompaniesDTO.enheter!!.filter {
                    it.organisasjonsnummer != null
                }.map {
                    createCompanyFromDTO(it)
                }
            )
        } catch (e: Exception) {
            CompanyListResponse.Error(e)
        }
    }

    suspend fun searchForCompaniesByOrgNumber(orgNumber: Int): CompanyListResponse {
        try {
            service.getCompanyWithOrgNumber(orgNumber)?.let {
                return CompanyListResponse.Success(companyEntities = listOf(createCompanyFromDTO(it)))
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
                return CompanyResponse.Success(companyEntity = createCompanyFromDTO(it))
            } ?: run {
                return CompanyResponse.Error(Exception(OK_STATUS_BUT_NO_BODY_ERROR))
            }
        } catch (e: Exception) {
            return CompanyResponse.Error(e)
        }
    }

    private fun createCompanyFromDTO(companyDTO: CompanyDTO): CompanyEntity {
        return CompanyEntity(
            id = 0,
            organisasjonsnummer = companyDTO.organisasjonsnummer!!.toInt(),
            navn = companyDTO.navn,
            stiftelsesdato = companyDTO.stiftelsesdato,
            registreringsdatoEnhetsregisteret = companyDTO.registreringsdatoEnhetsregisteret,
            oppstartsdato = companyDTO.oppstartsdato,
            datoEierskifte = companyDTO.datoEierskifte,
            organisasjonsform = companyDTO.organisasjonsform?.beskrivelse,
            hjemmeside = companyDTO.hjemmeside,
            registertIFrivillighetsregisteret = companyDTO.registertIFrivillighetsregisteret,
            registrertIMvaregisteret = companyDTO.registrertIMvaregisteret,
            registrertIForetaksregisteret = companyDTO.registrertIForetaksregisteret,
            registrertIStiftelsesregisteret = companyDTO.registrertIStiftelsesregisteret,
            antallAnsatte = companyDTO.antallAnsatte,
            sisteInnsendteAarsregnskap = companyDTO.sisteInnsendteAarsregnskap,
            konkurs = companyDTO.konkurs,
            underAvvikling = companyDTO.underAvvikling,
            underTvangsavviklingEllerTvangsopplosning = companyDTO.underTvangsavviklingEllerTvangsopplosning,
            overordnetEnhet = companyDTO.overordnetEnhet?.toInt(),
            institusjonellSektorkodeKode = companyDTO.institusjonellSektorkode?.kode,
            institusjonellSektorkodeBeskrivelse = companyDTO.institusjonellSektorkode?.beskrivelse,
            naeringskode1Kode = companyDTO.naeringskode1?.kode,
            naeringskode1Beskrivelse = companyDTO.naeringskode1?.beskrivelse,
            naeringskode2Kode = companyDTO.naeringskode2?.kode,
            naeringskode2Beskrivelse = companyDTO.naeringskode2?.beskrivelse,
            naeringskode3Kode = companyDTO.naeringskode3?.kode,
            naeringskode3Beskrivelse = companyDTO.naeringskode3?.beskrivelse,
            postadresseAdresse = buildAddressString(companyDTO.postadresse?.adresse),
            postadressePostnummer = companyDTO.postadresse?.postnummer,
            postadressePoststed = companyDTO.postadresse?.poststed,
            postadresseKommunenummer = companyDTO.postadresse?.kommunenummer,
            postadresseKommune = companyDTO.postadresse?.kommune,
            postadresseLandkode = companyDTO.postadresse?.landkode,
            postadresseLand = companyDTO.postadresse?.land,
            forretningsadresseAdresse = buildAddressString(companyDTO.forretningsadresse?.adresse),
            forretningsadressePostnummer = companyDTO.forretningsadresse?.postnummer,
            forretningsadressePoststed = companyDTO.forretningsadresse?.poststed,
            forretningsadresseKommunenummer = companyDTO.forretningsadresse?.kommunenummer,
            forretningsadresseKommune = companyDTO.forretningsadresse?.kommune,
            forretningsadresseLandkode = companyDTO.forretningsadresse?.landkode,
            forretningsadresseLand = companyDTO.forretningsadresse?.land,
            beliggenhetsadresseAdresse = buildAddressString(companyDTO.beliggenhetsadresse?.adresse),
            beliggenhetsadressePostnummer = companyDTO.beliggenhetsadresse?.postnummer,
            beliggenhetsadressePoststed = companyDTO.beliggenhetsadresse?.poststed,
            beliggenhetsadresseKommunenummer = companyDTO.beliggenhetsadresse?.kommunenummer,
            beliggenhetsadresseKommune = companyDTO.beliggenhetsadresse?.kommune,
            beliggenhetsadresseLandkode = companyDTO.beliggenhetsadresse?.landkode,
            beliggenhetsadresseLand = companyDTO.beliggenhetsadresse?.land
        )
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