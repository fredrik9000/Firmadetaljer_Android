package com.github.fredrik9000.firmadetaljer_android.company_details

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository

class CompanyDetailsViewModel @ViewModelInject constructor(private val repository: CompanyRepository) : ViewModel() {

    fun searchForCompanyWithOrgNumber(callback: CompanyDetailsNavigation, orgNumber: Int) {
        repository.searchForCompanyWithOrgNumber(callback, orgNumber)
    }
}
