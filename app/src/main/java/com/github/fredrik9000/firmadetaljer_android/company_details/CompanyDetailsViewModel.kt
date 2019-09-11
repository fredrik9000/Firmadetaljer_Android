package com.github.fredrik9000.firmadetaljer_android.company_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository

class CompanyDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CompanyRepository(application)

    fun searchForCompanyWithOrgNumber(callback: CompanyDetailsNavigation, orgNumber: Int) {
        repository.searchForCompanyWithOrgNumber(callback, orgNumber)
    }
}
