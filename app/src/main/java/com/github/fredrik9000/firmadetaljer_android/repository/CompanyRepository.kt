package com.github.fredrik9000.firmadetaljer_android.repository

import android.app.Application

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompaniesJson
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyJson
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
        call.enqueue(object : Callback<CompaniesJson> {
            override fun onResponse(call: Call<CompaniesJson>, response: Response<CompaniesJson>) {
                if (!response.isSuccessful) {
                    companyResponseMutableLiveData.value = CompanyListResponse(HttpException(response))
                    return
                }
                val companiesJson = response.body()
                val companyJsonList = companiesJson!!.data
                val companies = ArrayList<Company>()
                if (companyJsonList == null) {
                    companyResponseMutableLiveData.value = CompanyListResponse(companies)
                    return
                }

                for (companyJson in companyJsonList) {
                    val company = createCompanyFromJson(companyJson)
                    companies.add(company)
                }

                companyResponseMutableLiveData.value = CompanyListResponse(companies)
            }

            override fun onFailure(call: Call<CompaniesJson>, t: Throwable) {
                companyResponseMutableLiveData.value = CompanyListResponse(t)
            }
        })
    }

    fun getCompanyWithOrgNumber(companyResponseMutableLiveData: MutableLiveData<CompanyResponse>, orgNumber: Int?) {
        val call = service.getCompanyWithOrgNumber(orgNumber)
        call.enqueue(object : Callback<CompanyJson> {
            override fun onResponse(call: Call<CompanyJson>, response: Response<CompanyJson>) {
                if (!response.isSuccessful) {
                    // When a company doesn't exist a 400 status will return here.
                    // This is different from when searching for companies by name,
                    // because then the response will be successful with an empty list.
                    companyResponseMutableLiveData.value = CompanyResponse(HttpException(response))
                    return
                }
                val companyJson = response.body()
                val company = createCompanyFromJson(companyJson!!)
                companyResponseMutableLiveData.value = CompanyResponse(company)
            }

            override fun onFailure(call: Call<CompanyJson>, t: Throwable) {
                companyResponseMutableLiveData.value = CompanyResponse(t)
            }
        })
    }

    fun getCompaniesWithOrgNumber(companyResponseMutableLiveData: MutableLiveData<CompanyListResponse>, orgNumber: Int?) {
        val call = service.getCompanyWithOrgNumber(orgNumber)
        call.enqueue(object : Callback<CompanyJson> {
            override fun onResponse(call: Call<CompanyJson>, response: Response<CompanyJson>) {
                if (!response.isSuccessful) {
                    // When a company doesn't exist a 400 status will return here.
                    // This is different from when searching for companies by name,
                    // because then the response will be successful with an empty list.
                    companyResponseMutableLiveData.value = CompanyListResponse(HttpException(response))
                    return
                }
                val companyJson = response.body()
                val company = createCompanyFromJson(companyJson!!)
                val companyList = ArrayList<Company>()
                companyList.add(company)
                companyResponseMutableLiveData.value = CompanyListResponse(companyList)
            }

            override fun onFailure(call: Call<CompanyJson>, t: Throwable) {
                companyResponseMutableLiveData.value = CompanyListResponse(t)
            }
        })
    }

    private fun createCompanyFromJson(companyJson: CompanyJson): Company {
        return Company(0, companyJson.organisasjonsnummer, companyJson.navn, companyJson.stiftelsesdato,
                companyJson.registreringsdatoEnhetsregisteret, companyJson.oppstartsdato, companyJson.datoEierskifte,
                companyJson.organisasjonsform, companyJson.hjemmeside, companyJson.registertIFrivillighetsregisteret,
                companyJson.registrertIMvaregisteret, companyJson.registrertIForetaksregisteret, companyJson.registrertIStiftelsesregisteret,
                companyJson.antallAnsatte, companyJson.sisteInnsendteAarsregnskap, companyJson.konkurs,
                companyJson.underAvvikling, companyJson.underTvangsavviklingEllerTvangsopplosning, companyJson.overordnetEnhet,
                if (companyJson.institusjonellSektorkode != null) companyJson.institusjonellSektorkode.kode else null,
                if (companyJson.institusjonellSektorkode != null) companyJson.institusjonellSektorkode.beskrivelse else null,
                if (companyJson.naeringskode1 != null) companyJson.naeringskode1.kode else null,
                if (companyJson.naeringskode1 != null) companyJson.naeringskode1.beskrivelse else null,
                if (companyJson.naeringskode2 != null) companyJson.naeringskode2.kode else null,
                if (companyJson.naeringskode2 != null) companyJson.naeringskode2.beskrivelse else null,
                if (companyJson.naeringskode3 != null) companyJson.naeringskode3.kode else null,
                if (companyJson.naeringskode3 != null) companyJson.naeringskode3.beskrivelse else null,
                if (companyJson.postadresse != null) companyJson.postadresse.adresse else null,
                if (companyJson.postadresse != null) companyJson.postadresse.postnummer else null,
                if (companyJson.postadresse != null) companyJson.postadresse.poststed else null,
                if (companyJson.postadresse != null) companyJson.postadresse.kommunenummer else null,
                if (companyJson.postadresse != null) companyJson.postadresse.kommune else null,
                if (companyJson.postadresse != null) companyJson.postadresse.landkode else null,
                if (companyJson.postadresse != null) companyJson.postadresse.land else null,
                if (companyJson.forretningsadresse != null) companyJson.forretningsadresse.adresse else null,
                if (companyJson.forretningsadresse != null) companyJson.forretningsadresse.postnummer else null,
                if (companyJson.forretningsadresse != null) companyJson.forretningsadresse.poststed else null,
                if (companyJson.forretningsadresse != null) companyJson.forretningsadresse.kommunenummer else null,
                if (companyJson.forretningsadresse != null) companyJson.forretningsadresse.kommune else null,
                if (companyJson.forretningsadresse != null) companyJson.forretningsadresse.landkode else null,
                if (companyJson.forretningsadresse != null) companyJson.forretningsadresse.land else null,
                if (companyJson.beliggenhetsadresse != null) companyJson.beliggenhetsadresse.adresse else null,
                if (companyJson.beliggenhetsadresse != null) companyJson.beliggenhetsadresse.postnummer else null,
                if (companyJson.beliggenhetsadresse != null) companyJson.beliggenhetsadresse.poststed else null,
                if (companyJson.beliggenhetsadresse != null) companyJson.beliggenhetsadresse.kommunenummer else null,
                if (companyJson.beliggenhetsadresse != null) companyJson.beliggenhetsadresse.kommune else null,
                if (companyJson.beliggenhetsadresse != null) companyJson.beliggenhetsadresse.landkode else null,
                if (companyJson.beliggenhetsadresse != null) companyJson.beliggenhetsadresse.land else null)
    }
}
