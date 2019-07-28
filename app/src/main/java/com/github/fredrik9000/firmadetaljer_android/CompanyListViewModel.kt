package com.github.fredrik9000.firmadetaljer_android

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import kotlinx.coroutines.launch

class CompanyListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CompanyRepository = CompanyRepository(application)
    val searchResultCompanyList: MutableLiveData<CompanyListResponse> = MutableLiveData()
    val savedCompanyList: LiveData<List<Company>>

    init {
        savedCompanyList = repository.allSavedCompanies
    }

    fun searchForCompaniesThatStartsWith(text: String) {
        repository.getAllCompaniesThatStartsWith(searchResultCompanyList, text)
    }

    // Searching by org number can only return 1 company, so the implementation isn't optimal
    // The result will be displayed in the same way as for when searching text,
    // which is why the same live data is passed in here.
    // TODO: Improve implementation
    fun searchForCompaniesWithOrgNumber(orgNumber: Int) {
        repository.getCompaniesWithOrgNumber(searchResultCompanyList, orgNumber)
    }

    fun upsert(company: Company) {
        viewModelScope.launch {
            repository.upsert(company)
        }
    }
}
