package com.github.fredrik9000.firmadetaljer_android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse

class CompanyDetailsViewModel(application: Application) : AndroidViewModel(application) {
    val company: MutableLiveData<CompanyResponse> = MutableLiveData()
    private val repository: CompanyRepository = CompanyRepository(application)

    fun searchForCompanyWithOrgNumber(orgNumber: Int?) {
        repository.getCompanyWithOrgNumber(company, orgNumber)
    }
}
