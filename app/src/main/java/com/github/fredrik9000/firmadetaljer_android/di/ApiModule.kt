package com.github.fredrik9000.firmadetaljer_android.di

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApiModule {
    @Singleton
    @Provides
    fun provideRetrofitInstance() : Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://data.brreg.no/enhetsregisteret/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Singleton
    @Provides
    fun provideCompanyService(retrofit: Retrofit) : CompanyService {
        return retrofit.create(CompanyService::class.java)
    }
}