package com.github.fredrik9000.firmadetaljer_android.repository.data

import companydb.CompanyEntity
import kotlinx.coroutines.flow.Flow

interface CompanyDataSource {
    fun getCompanies(): Flow<List<CompanyEntity>>

    suspend fun getCompanyByOrgNumber(orgNumber: Int): CompanyEntity?

    suspend fun deleteCompanyByOrgNumber(orgNumber: Int)

    suspend fun deleteAllCompanies()

    suspend fun insertCompany(companyEntity: CompanyEntity)
}