package com.github.fredrik9000.firmadetaljer_android.repository.rest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanyService {
    @GET("enhet.json")
    fun getCompanies(@Query("\$filter") filter: String): Call<CompaniesJson>

    @GET("enhet/{orgNumber}.json")
    fun getCompanyWithOrgNumber(@Path("orgNumber") orgNumber: Int?): Call<CompanyJson>
}
