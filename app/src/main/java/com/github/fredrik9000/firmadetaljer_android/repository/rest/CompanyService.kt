package com.github.fredrik9000.firmadetaljer_android.repository.rest

import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyDTO
import com.github.fredrik9000.firmadetaljer_android.repository.rest.dto.CompanyWrapperEmbeddedDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanyService {
    @GET("enheter")
    suspend fun getCompanies(@Query("navn") navn: String, @Query("fraAntallAnsatte") fraAntallAnsatte: Int?, @Query("tilAntallAnsatte") tilAntallAnsatte: Int?): Response<CompanyWrapperEmbeddedDTO>

    @GET("enheter/{orgNumber}")
    suspend fun getCompanyWithOrgNumber(@Path("orgNumber") orgNumber: Int): Response<CompanyDTO>
}
