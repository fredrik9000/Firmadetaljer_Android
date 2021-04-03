package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyDTO
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyWrapperEmbeddedDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanyService {
    @GET("enheter")
    fun getCompanies(@Query("navn") navn: String, @Query("fraAntallAnsatte") fraAntallAnsatte: Int?, @Query("tilAntallAnsatte") tilAntallAnsatte: Int?): Call<CompanyWrapperEmbeddedDTO>

    @GET("enheter/{orgNumber}")
    fun getCompanyWithOrgNumber(@Path("orgNumber") orgNumber: Int): Call<CompanyDTO>
}
