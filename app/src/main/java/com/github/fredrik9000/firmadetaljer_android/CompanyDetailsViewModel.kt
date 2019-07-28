package com.github.fredrik9000.firmadetaljer_android

import android.app.Application
import androidx.lifecycle.AndroidViewModel

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository

class CompanyDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CompanyRepository = CompanyRepository(application)

    fun searchForCompanyWithOrgNumber(callback: ICompanyResponseHandler, orgNumber: Int) {
        repository.getCompanyWithOrgNumber(callback, orgNumber)
    }
}
