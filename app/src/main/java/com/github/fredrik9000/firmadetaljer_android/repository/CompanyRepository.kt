package com.github.fredrik9000.firmadetaljer_android.repository

import android.app.Application

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.fredrik9000.firmadetaljer_android.ICompanyResponseHandler

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
    val allSavedCompanies: LiveData<List<Company>>

    init {
        val database = CompanyDatabase.getInstance(application)
        companyDao = database.companyDao

        val retrofit = Retrofit.Builder()
                .baseUrl("https://data.brreg.no/enhetsregisteret/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        service = retrofit.create(CompanyService::class.java)

        allSavedCompanies = companyDao.allCompaniesOrderedByLastInserted
    }

    // When a company is updated it should appear on the top of last viewed companies list.
    // In order to achieve this the company (if exists) is deleted before being inserted.
    // It would be better with a modification date but this does the job in a simple way.
    suspend fun upsert(company: Company) {
        companyDao.delete(company)
        company.id = 0
        companyDao.insert(company)
    }

    fun getAllCompaniesThatStartsWith(companyResponseMutableLiveData: MutableLiveData<CompanyListResponse>, text: String) {
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
                    val company = createCompanyFromDTO(companyDTO)
                    companies.add(company)
                }

                companyResponseMutableLiveData.value = CompanyListResponse(companies)
            }

            override fun onFailure(call: Call<CompaniesDTO>, t: Throwable) {
                companyResponseMutableLiveData.value = CompanyListResponse(t)
            }
        })
    }

    fun getCompaniesWithOrgNumber(companyResponseMutableLiveData: MutableLiveData<CompanyListResponse>, orgNumber: Int) {
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

    fun getCompanyWithOrgNumber(callback: ICompanyResponseHandler, orgNumber: Int) {
        val call = service.getCompanyWithOrgNumber(orgNumber)
        call.enqueue(object : Callback<CompanyDTO> {
            override fun onResponse(call: Call<CompanyDTO>, response: Response<CompanyDTO>) {
                if (!response.isSuccessful) {
                    callback.handleResponse(CompanyResponse(HttpException(response)))
                    return
                }
                val companyDTO = response.body()
                val company = createCompanyFromDTO(companyDTO!!)
                callback.handleResponse(CompanyResponse(company))
            }

            override fun onFailure(call: Call<CompanyDTO>, t: Throwable) {
                callback.handleResponse(CompanyResponse(t))
            }
        })
    }

    private fun createCompanyFromDTO(companyDTO: CompanyDTO): Company {
        return Company(0, companyDTO.organisasjonsnummer, companyDTO.navn, companyDTO.stiftelsesdato,
                companyDTO.registreringsdatoEnhetsregisteret, companyDTO.oppstartsdato, companyDTO.datoEierskifte,
                companyDTO.organisasjonsform, companyDTO.hjemmeside, companyDTO.registertIFrivillighetsregisteret,
                companyDTO.registrertIMvaregisteret, companyDTO.registrertIForetaksregisteret, companyDTO.registrertIStiftelsesregisteret,
                companyDTO.antallAnsatte, companyDTO.sisteInnsendteAarsregnskap, companyDTO.konkurs,
                companyDTO.underAvvikling, companyDTO.underTvangsavviklingEllerTvangsopplosning, companyDTO.overordnetEnhet,
                if (companyDTO.institusjonellSektorkode != null) companyDTO.institusjonellSektorkode.kode else null,
                if (companyDTO.institusjonellSektorkode != null) companyDTO.institusjonellSektorkode.beskrivelse else null,
                if (companyDTO.naeringskode1 != null) companyDTO.naeringskode1.kode else null,
                if (companyDTO.naeringskode1 != null) companyDTO.naeringskode1.beskrivelse else null,
                if (companyDTO.naeringskode2 != null) companyDTO.naeringskode2.kode else null,
                if (companyDTO.naeringskode2 != null) companyDTO.naeringskode2.beskrivelse else null,
                if (companyDTO.naeringskode3 != null) companyDTO.naeringskode3.kode else null,
                if (companyDTO.naeringskode3 != null) companyDTO.naeringskode3.beskrivelse else null,
                if (companyDTO.postadresse != null) companyDTO.postadresse.adresse else null,
                if (companyDTO.postadresse != null) companyDTO.postadresse.postnummer else null,
                if (companyDTO.postadresse != null) companyDTO.postadresse.poststed else null,
                if (companyDTO.postadresse != null) companyDTO.postadresse.kommunenummer else null,
                if (companyDTO.postadresse != null) companyDTO.postadresse.kommune else null,
                if (companyDTO.postadresse != null) companyDTO.postadresse.landkode else null,
                if (companyDTO.postadresse != null) companyDTO.postadresse.land else null,
                if (companyDTO.forretningsadresse != null) companyDTO.forretningsadresse.adresse else null,
                if (companyDTO.forretningsadresse != null) companyDTO.forretningsadresse.postnummer else null,
                if (companyDTO.forretningsadresse != null) companyDTO.forretningsadresse.poststed else null,
                if (companyDTO.forretningsadresse != null) companyDTO.forretningsadresse.kommunenummer else null,
                if (companyDTO.forretningsadresse != null) companyDTO.forretningsadresse.kommune else null,
                if (companyDTO.forretningsadresse != null) companyDTO.forretningsadresse.landkode else null,
                if (companyDTO.forretningsadresse != null) companyDTO.forretningsadresse.land else null,
                if (companyDTO.beliggenhetsadresse != null) companyDTO.beliggenhetsadresse.adresse else null,
                if (companyDTO.beliggenhetsadresse != null) companyDTO.beliggenhetsadresse.postnummer else null,
                if (companyDTO.beliggenhetsadresse != null) companyDTO.beliggenhetsadresse.poststed else null,
                if (companyDTO.beliggenhetsadresse != null) companyDTO.beliggenhetsadresse.kommunenummer else null,
                if (companyDTO.beliggenhetsadresse != null) companyDTO.beliggenhetsadresse.kommune else null,
                if (companyDTO.beliggenhetsadresse != null) companyDTO.beliggenhetsadresse.landkode else null,
                if (companyDTO.beliggenhetsadresse != null) companyDTO.beliggenhetsadresse.land else null)
    }
}
