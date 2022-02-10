package com.github.fredrik9000.firmadetaljer_android.company_list

import android.text.TextUtils
import androidx.lifecycle.*
import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import companydb.CompanyEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListViewModel @Inject constructor(private val repository: CompanyRepository) : ViewModel() {

    private val _searchResultLiveData = MediatorLiveData<CompanyListResponse>()
    val searchResultLiveData: LiveData<CompanyListResponse>
        get() = _searchResultLiveData

    private val companyNameLiveData = MutableLiveData<String>()
    private val organizationNumberLiveData = MutableLiveData<Int>()

    private val searchByNameResultLiveData: LiveData<CompanyListResponse> = Transformations.switchMap(companyNameLiveData) { text ->
        liveData { emit(repository.searchForCompaniesByName(text, selectedNumberOfEmployeesFilter)) }
    }

    private val searchByOrganizationNumberResultLiveData: LiveData<CompanyListResponse> = Transformations.switchMap(organizationNumberLiveData) { orgNumber ->
        liveData { emit(repository.searchForCompaniesByOrgNumber(orgNumber)) }
    }

    val savedCompaniesFlow = repository.savedCompanies

    var searchMode = SearchMode.FIRM_NAME
    var searchString = ""
    var selectedNumberOfEmployeesFilter = NumberOfEmployeesFilter.ALL_EMPLOYEES

    val isSearchingWithValidOrganizationNumber: Boolean
        get() = searchMode == SearchMode.ORGANIZATION_NUMBER && searchString.length >= ORGANIZATION_NUMBER_LENGTH && TextUtils.isDigitsOnly(searchString)

    val isSearchingWithValidFirmName: Boolean
        get() = searchMode == SearchMode.FIRM_NAME && searchString.length >= MINIMUM_FIRM_NAME_SEARCH_LENGTH

    val isSearchingWithValidInput: Boolean
        get() = isSearchingWithValidFirmName || isSearchingWithValidOrganizationNumber

    val searchStringExceedsOrganizationNumberLength: Boolean
        get() = searchString.length > ORGANIZATION_NUMBER_LENGTH

    init {
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

    fun searchOnSelectedSearchMode(query: String) {
        if (searchMode == SearchMode.FIRM_NAME) {
            searchForCompaniesByName(query)
        } else {
            searchForCompaniesByOrgNumber(Integer.parseInt(query))
        }
    }

    private fun searchForCompaniesByName(text: String) {
        companyNameLiveData.value = text
    }

    private fun searchForCompaniesByOrgNumber(orgNumber: Int) {
        organizationNumberLiveData.value = orgNumber
    }

    fun upsert(companyEntity: CompanyEntity) {
        viewModelScope.launch {
            repository.upsert(companyEntity)
        }
    }

    fun deleteAllCompanies() {
        viewModelScope.launch {
            repository.deleteAllCompanies()
        }
    }

    companion object {
        private const val MINIMUM_FIRM_NAME_SEARCH_LENGTH = 2
        private const val ORGANIZATION_NUMBER_LENGTH = 9
    }
}