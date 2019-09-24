package com.github.fredrik9000.firmadetaljer_android.company_list

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.*

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import kotlinx.coroutines.launch
import androidx.lifecycle.Transformations
import androidx.lifecycle.LiveData

class CompanyListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CompanyRepository(application)

    private val _searchResultLiveData = MediatorLiveData<CompanyListResponse>()
    val searchResultLiveData: LiveData<CompanyListResponse>
        get() = _searchResultLiveData

    private val companyNameLiveData = MutableLiveData<String>()
    private val organizationNumberLiveData = MutableLiveData<Int>()

    private val searchByNameResultLiveData: LiveData<CompanyListResponse> = Transformations.switchMap(companyNameLiveData) {
        text -> repository.searchForCompaniesByName(text)
    }

    private val searchByOrganizationNumberResultLiveData: LiveData<CompanyListResponse> = Transformations.switchMap(organizationNumberLiveData) {
        orgNumber -> repository.searchForCompaniesByOrgNumber(orgNumber)
    }

    val savedCompaniesLiveData: LiveData<List<Company>>

    var searchMode = SearchMode.FIRM_NAME
    var searchString = ""

    val isSearchingWithValidOrganizationNumber: Boolean
        get() = searchMode == SearchMode.ORGANIZATION_NUMBER && searchString.length >= ORGANIZATION_NUMBER_LENGTH && TextUtils.isDigitsOnly(searchString)

    val isSearchingWithValidFirmName: Boolean
        get() = searchMode == SearchMode.FIRM_NAME && searchString.length >= MINIMUM_FIRM_NAME_SEARCH_LENGTH

    val isSearchingWithValidInput: Boolean
        get() = isSearchingWithValidFirmName || isSearchingWithValidOrganizationNumber

    val searchStringExceedsOrganizationNumberLength : Boolean
        get () = searchString.length > ORGANIZATION_NUMBER_LENGTH

    init {
        savedCompaniesLiveData = repository.savedCompanies

        _searchResultLiveData.addSource(searchByNameResultLiveData) {
            _searchResultLiveData.value = it
        }

        _searchResultLiveData.addSource(searchByOrganizationNumberResultLiveData) {
            _searchResultLiveData.value = it
        }
    }

    fun trimSearchStringByOrganizationNumberLength() {
        searchString = searchString.substring(0, ORGANIZATION_NUMBER_LENGTH)
    }

    fun searchForCompaniesByName(text: String) {
        companyNameLiveData.value = text
    }

    fun searchForCompaniesByOrgNumber(orgNumber: Int) {
        organizationNumberLiveData.value = orgNumber
    }

    fun searchOnSelectedSearchMode(query: String) {
        if (searchMode == SearchMode.FIRM_NAME) {
            searchForCompaniesByName(query)
        } else {
            searchForCompaniesByOrgNumber(Integer.parseInt(query))
        }
    }

    fun upsert(company: Company) {
        viewModelScope.launch {
            repository.upsert(company)
        }
    }

    fun deleteAllCompanies() {
        viewModelScope.launch {
            repository.deleteAllCompanies()
        }
    }

    private companion object {
        const val MINIMUM_FIRM_NAME_SEARCH_LENGTH = 2
        const val ORGANIZATION_NUMBER_LENGTH = 9
    }
}
