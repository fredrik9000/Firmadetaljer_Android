package com.github.fredrik9000.firmadetaljer_android.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsNavigation
import com.github.fredrik9000.firmadetaljer_android.company_list.NumberOfEmployeesFilter
import com.github.fredrik9000.firmadetaljer_android.repository.rest.*
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

    val savedCompanies: LiveData<List<Company>> = companyDao.companiesOrderedByName

    // When a company is updated it should appear at the top of the viewed companies list.
    // In order to achieve this the company (if exists) is deleted before being inserted again.
    suspend fun upsert(company: Company) {
        companyDao.deleteByOrganizationNumber(company.organisasjonsnummer)
        companyDao.insert(company)
    }

    suspend fun deleteAllCompanies() {
        companyDao.deleteAll()
    }

    fun searchForCompaniesByName(name: String, selectedNumberOfEmployeesFilter: NumberOfEmployeesFilter) : MutableLiveData<CompanyListResponse> {
        val companyListResponseLiveData = MutableLiveData<CompanyListResponse>()
        val call = when(selectedNumberOfEmployeesFilter) {
            NumberOfEmployeesFilter.ALL_EMPLOYEES -> service.getCompanies(name, null, null)
            NumberOfEmployeesFilter.LESS_THAN_6 -> service.getCompanies(name, null, 5)
            NumberOfEmployeesFilter.BETWEEN_5_AND_201 -> service.getCompanies(name, 6, 200)
            NumberOfEmployeesFilter.MORE_THAN_200 -> service.getCompanies(name, 201, null)
        }
        call.enqueue(object : Callback<CompanyWrapperEmbeddedDTO> {
            override fun onResponse(call: Call<CompanyWrapperEmbeddedDTO>, response: Response<CompanyWrapperEmbeddedDTO>) {
                if (!response.isSuccessful) {
                    companyListResponseLiveData.value = CompanyListResponse(HttpException(response))
                    return
                }
                val companiesDTO = response.body()
                val embeddedCompaniesDTO = companiesDTO!!._embedded
                val companies = ArrayList<Company>()

                // If data is null this means no companies were found, so return an empty list
                if (embeddedCompaniesDTO == null) {
                    companyListResponseLiveData.value = CompanyListResponse(companies)
                    return
                }

                val companyDTOList = embeddedCompaniesDTO.enheter!!

                for (companyDTO in companyDTOList) {
                    if (companyDTO.organisasjonsnummer != null) { //Should never be null
                        val company = createCompanyFromDTO(companyDTO)
                        companies.add(company)
                    }
                }

                companyListResponseLiveData.value = CompanyListResponse(companies)
            }

            override fun onFailure(call: Call<CompanyWrapperEmbeddedDTO>, t: Throwable) {
                companyListResponseLiveData.value = CompanyListResponse(t)
            }
        })
        return companyListResponseLiveData
    }

    fun searchForCompaniesByOrgNumber(orgNumber: Int) : MutableLiveData<CompanyListResponse> {
        val companyListResponseLiveData = MutableLiveData<CompanyListResponse>()
        val call = service.getCompanyWithOrgNumber(orgNumber)
        call.enqueue(object : Callback<CompanyDTO> {
            override fun onResponse(call: Call<CompanyDTO>, response: Response<CompanyDTO>) {
                if (!response.isSuccessful) {
                    // When a company doesn't exist a 400 or 404 status will return here.
                    // This is different from when searching for companies by name,
                    // because then the response will be successful with an empty list.
                    companyListResponseLiveData.value = CompanyListResponse(HttpException(response))
                    return
                }
                val companyDTO = response.body()
                val company = createCompanyFromDTO(companyDTO!!)
                val companyList = ArrayList<Company>()
                companyList.add(company)
                companyListResponseLiveData.value = CompanyListResponse(companyList)
            }

            override fun onFailure(call: Call<CompanyDTO>, t: Throwable) {
                companyListResponseLiveData.value = CompanyListResponse(t)
            }
        })
        return companyListResponseLiveData
    }

    fun searchForCompanyWithOrgNumber(callback: CompanyDetailsNavigation, orgNumber: Int) {
        val call = service.getCompanyWithOrgNumber(orgNumber)
        call.enqueue(object : Callback<CompanyDTO> {
            override fun onResponse(call: Call<CompanyDTO>, response: Response<CompanyDTO>) {
                if (!response.isSuccessful) {
                    callback.handleCompanyNavigationResponse(CompanyResponse(HttpException(response)))
                    return
                }
                val companyDTO = response.body()
                val company = createCompanyFromDTO(companyDTO!!)
                callback.handleCompanyNavigationResponse(CompanyResponse(company))
            }

            override fun onFailure(call: Call<CompanyDTO>, t: Throwable) {
                callback.handleCompanyNavigationResponse(CompanyResponse(t))
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
        return if (addressList == null || addressList.isEmpty()) {
            null
        } else {
            addressList.joinToString()
        }
    }
}
