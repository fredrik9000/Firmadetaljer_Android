package com.github.fredrik9000.firmadetaljer_android.repository.rest

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanyService {
    @GET("enheter")
    fun getCompanies(@Query("navn") navn: String): Call<CompanyWrapperEmbeddedDTO>

    @GET("enheter/{orgNumber}")
    fun getCompanyWithOrgNumber(@Path("orgNumber") orgNumber: Int): Call<CompanyDTO>
}
