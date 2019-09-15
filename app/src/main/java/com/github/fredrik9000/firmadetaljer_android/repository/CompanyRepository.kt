package com.github.fredrik9000.firmadetaljer_android.repository

import android.app.Application

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsNavigation

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompaniesDTO
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyDTO
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDatabase

import java.util.ArrayList

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompanyRepository(application: Application) {
    private val companyDao: CompanyDao
    private val service: CompanyService
    val savedCompanies: LiveData<List<Company>>

    init {
        val database = CompanyDatabase.getInstance(application)
        companyDao = database.companyDao

        val retrofit = Retrofit.Builder()
                .baseUrl("https://data.brreg.no/enhetsregisteret/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(CompanyService::class.java)

        savedCompanies = companyDao.companiesOrderedByName
    }

    // When a company is updated it should appear in the viewed companies list.
    // In order to achieve this the company (if exists) is deleted before being inserted again.
    suspend fun upsert(company: Company) {
        companyDao.deleteByOrganizationNumber(company.organisasjonsnummer)
        companyDao.insert(company)
    }

    suspend fun deleteAllCompanies() {
        companyDao.deleteAll()
    }

    fun searchForCompaniesWithNamesBeginningWithText(companyResponseMutableLiveData: MutableLiveData<CompanyListResponse>, text: String) {
        val call = service.getCompanies("startswith(navn,'$text')")
        call.enqueue(object : Callback<CompaniesDTO> {
            override fun onResponse(call: Call<CompaniesDTO>, response: Response<CompaniesDTO>) {
                if (!response.isSuccessful) {
                    companyResponseMutableLiveData.value = CompanyListResponse(HttpException(response))
                    return
                }
                val companiesDTO = response.body()
                val companyDTOList = companiesDTO!!.data
                val companies = ArrayList<Company>()

                // If data is null this means no companies were found, so return an empty list
                if (companyDTOList == null) {
                    companyResponseMutableLiveData.value = CompanyListResponse(companies)
                    return
                }

                for (companyDTO in companyDTOList) {
                    if (companyDTO.organisasjonsnummer != null) { //Should never be null
                        val company = createCompanyFromDTO(companyDTO)
                        companies.add(company)
                    }
                }

                companyResponseMutableLiveData.value = CompanyListResponse(companies)
            }

            override fun onFailure(call: Call<CompaniesDTO>, t: Throwable) {
                companyResponseMutableLiveData.value = CompanyListResponse(t)
            }
        })
    }

    fun searchForCompaniesWithOrgNumber(companyResponseMutableLiveData: MutableLiveData<CompanyListResponse>, orgNumber: Int) {
        val call = service.getCompanyWithOrgNumber(orgNumber)
        call.enqueue(object : Callback<CompanyDTO> {
            override fun onResponse(call: Call<CompanyDTO>, response: Response<CompanyDTO>) {
                if (!response.isSuccessful) {
                    // When a company doesn't exist a 400 status will return here.
                    // This is different from when searching for companies by name,
                    // because then the response will be successful with an empty list.
                    companyResponseMutableLiveData.value = CompanyListResponse(HttpException(response))
                    return
                }
                val companyDTO = response.body()
                val company = createCompanyFromDTO(companyDTO!!)
                val companyList = ArrayList<Company>()
                companyList.add(company)
                companyResponseMutableLiveData.value = CompanyListResponse(companyList)
            }

            override fun onFailure(call: Call<CompanyDTO>, t: Throwable) {
                companyResponseMutableLiveData.value = CompanyListResponse(t)
            }
        })
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
        return Company(0, companyDTO.organisasjonsnummer!!, companyDTO.navn, companyDTO.stiftelsesdato,
                companyDTO.registreringsdatoEnhetsregisteret, companyDTO.oppstartsdato, companyDTO.datoEierskifte,
                companyDTO.organisasjonsform, companyDTO.hjemmeside, companyDTO.registertIFrivillighetsregisteret,
                companyDTO.registrertIMvaregisteret, companyDTO.registrertIForetaksregisteret, companyDTO.registrertIStiftelsesregisteret,
                companyDTO.antallAnsatte, companyDTO.sisteInnsendteAarsregnskap, companyDTO.konkurs,
                companyDTO.underAvvikling, companyDTO.underTvangsavviklingEllerTvangsopplosning, companyDTO.overordnetEnhet,
                companyDTO.institusjonellSektorkode?.kode, companyDTO.institusjonellSektorkode?.beskrivelse,
                companyDTO.naeringskode1?.kode, companyDTO.naeringskode1?.beskrivelse,
                companyDTO.naeringskode2?.kode, companyDTO.naeringskode2?.beskrivelse,
                companyDTO.naeringskode3?.kode, companyDTO.naeringskode3?.beskrivelse,
                companyDTO.postadresse?.adresse, companyDTO.postadresse?.postnummer,
                companyDTO.postadresse?.poststed, companyDTO.postadresse?.kommunenummer,
                companyDTO.postadresse?.kommune, companyDTO.postadresse?.landkode,
                companyDTO.postadresse?.land, companyDTO.forretningsadresse?.adresse,
                companyDTO.forretningsadresse?.postnummer, companyDTO.forretningsadresse?.poststed,
                companyDTO.forretningsadresse?.kommunenummer, companyDTO.forretningsadresse?.kommune,
                companyDTO.forretningsadresse?.landkode, companyDTO.forretningsadresse?.land,
                companyDTO.beliggenhetsadresse?.adresse, companyDTO.beliggenhetsadresse?.postnummer,
                companyDTO.beliggenhetsadresse?.poststed, companyDTO.beliggenhetsadresse?.kommunenummer,
                companyDTO.beliggenhetsadresse?.kommune, companyDTO.beliggenhetsadresse?.landkode,
                companyDTO.beliggenhetsadresse?.land)
    }
}
