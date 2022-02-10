package com.github.fredrik9000.firmadetaljer_android.company_details

import androidx.lifecycle.ViewModel
import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import companydb.CompanyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompanyDetailsViewModel @Inject constructor(private val repository: CompanyRepository) : ViewModel() {

    suspend fun searchForCompanyWithOrgNumber(orgNumber: Int): CompanyResponse {
        return repository.searchForCompanyWithOrgNumber(orgNumber)
    }

    suspend fun getCompanyByOrgNumber(orgNumber: Int): CompanyEntity? {
        return repository.getCompanyByOrgNumber(orgNumber)
    }
}