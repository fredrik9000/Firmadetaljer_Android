package com.github.fredrik9000.firmadetaljer_android.repository.rest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CompanyService {
    @GET("enhet.json")
    Call<CompaniesJson> getCompanies(@Query("$filter") String filter);

    @GET("enhet/{orgNumber}.json")
    Call<CompanyJson> getCompanyWithOrgNumber(@Path("orgNumber") Integer orgNumber);
}
