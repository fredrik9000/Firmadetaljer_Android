package com.github.fredrik9000.firmadetaljer_android.repository

import androidx.lifecycle.MutableLiveData
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsNavigation
import com.github.fredrik9000.firmadetaljer_android.company_list.NumberOfEmployeesFilter
import com.github.fredrik9000.firmadetaljer_android.repository.rest.*
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyDTO
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyWrapperEmbeddedDTO
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.*
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

    fun searchForCompaniesByName(name: String, selectedNumberOfEmployeesFilter: NumberOfEmployeesFilter): MutableLiveData<CompanyListResponse> {
        val companyListResponseLiveData = MutableLiveData<CompanyListResponse>()
        when (selectedNumberOfEmployeesFilter) {
            NumberOfEmployeesFilter.ALL_EMPLOYEES -> service.getCompanies(name, null, null)
            NumberOfEmployeesFilter.LESS_THAN_6 -> service.getCompanies(name, null, 5)
            NumberOfEmployeesFilter.BETWEEN_5_AND_201 -> service.getCompanies(name, 6, 200)
            NumberOfEmployeesFilter.MORE_THAN_200 -> service.getCompanies(name, 201, null)
        }.enqueue(object : Callback<CompanyWrapperEmbeddedDTO> {
            override fun onResponse(call: Call<CompanyWrapperEmbeddedDTO>, response: Response<CompanyWrapperEmbeddedDTO>) {
                if (!response.isSuccessful) {
                    companyListResponseLiveData.value = CompanyListResponse.Error(HttpException(response))
                    return
                }
                val embeddedCompaniesDTO = response.body()!!.embedded
                val companies = ArrayList<Company>()

                // If data is null this means no companies were found, so return an empty list
                if (embeddedCompaniesDTO == null) {
                    companyListResponseLiveData.value = CompanyListResponse.Success(companies)
                    return
                }

                for (companyDTO in embeddedCompaniesDTO.enheter!!) {
                    if (companyDTO.organisasjonsnummer != null) { //Should never be null
                        companies.add(createCompanyFromDTO(companyDTO))
                    }
                }

                companyListResponseLiveData.value = CompanyListResponse.Success(companies)
            }

            override fun onFailure(call: Call<CompanyWrapperEmbeddedDTO>, t: Throwable) {
                companyListResponseLiveData.value = CompanyListResponse.Error(t)
            }
        })
        return companyListResponseLiveData
    }

    fun searchForCompaniesByOrgNumber(orgNumber: Int): MutableLiveData<CompanyListResponse> {
        val companyListResponseLiveData = MutableLiveData<CompanyListResponse>()
        service.getCompanyWithOrgNumber(orgNumber).enqueue(object : Callback<CompanyDTO> {
            override fun onResponse(call: Call<CompanyDTO>, response: Response<CompanyDTO>) {
                if (!response.isSuccessful) {
                    // When a company doesn't exist a 400 or 404 status will return here.
                    // This is different from when searching for companies by name,
                    // because then the response will be successful with an empty list.
                    companyListResponseLiveData.value = CompanyListResponse.Error(HttpException(response))
                    return
                }
                val companyList = ArrayList<Company>()
                companyList.add(createCompanyFromDTO(response.body()!!))
                companyListResponseLiveData.value = CompanyListResponse.Success(companyList)
            }

            override fun onFailure(call: Call<CompanyDTO>, t: Throwable) {
                companyListResponseLiveData.value = CompanyListResponse.Error(t)
            }
        })
        return companyListResponseLiveData
    }

    fun searchForCompanyWithOrgNumber(callback: CompanyDetailsNavigation, orgNumber: Int) {
        service.getCompanyWithOrgNumber(orgNumber).enqueue(object : Callback<CompanyDTO> {
            override fun onResponse(call: Call<CompanyDTO>, response: Response<CompanyDTO>) {
                if (!response.isSuccessful) {
                    callback.handleCompanyNavigationResponse(CompanyResponse.Error(HttpException(response)))
                    return
                }
                callback.handleCompanyNavigationResponse(CompanyResponse.Success(createCompanyFromDTO(response.body()!!)))
            }

            override fun onFailure(call: Call<CompanyDTO>, t: Throwable) {
                callback.handleCompanyNavigationResponse(CompanyResponse.Error(t))
            }
        })
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
}
