package com.github.fredrik9000.firmadetaljer_android.company_details

import androidx.lifecycle.ViewModel
import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompanyDetailsViewModel @Inject constructor(private val repository: CompanyRepository) : ViewModel() {

    fun searchForCompanyWithOrgNumber(callback: CompanyDetailsNavigation, orgNumber: Int) {
        repository.searchForCompanyWithOrgNumber(callback, orgNumber)
    }
}
