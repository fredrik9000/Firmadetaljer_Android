package com.github.fredrik9000.firmadetaljer_android.company_list

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import kotlinx.coroutines.launch

class CompanyListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CompanyRepository(application)

    var searchMode = SearchMode.FIRM_NAME
    var searchString = ""

    val searchResultCompanyList: MutableLiveData<CompanyListResponse> = MutableLiveData()
    val savedCompanyList: LiveData<List<Company>>

    val isSearchingWithValidOrganizationNumber: Boolean
        get() = searchMode == SearchMode.ORGANIZATION_NUMBER && searchString.length >= ORGANIZATION_NUMBER_LENGTH && TextUtils.isDigitsOnly(searchString)

    val isSearchingWithValidFirmName: Boolean
        get() = searchMode == SearchMode.FIRM_NAME && searchString.length >= MINIMUM_FIRM_NAME_SEARCH_LENGTH

    val isSearchingWithValidInput: Boolean
        get() = isSearchingWithValidFirmName || isSearchingWithValidOrganizationNumber

    val organizationNumberSearchHasTooManyCharacters : Boolean
        get () = searchString.length > ORGANIZATION_NUMBER_LENGTH

    init {
        savedCompanyList = repository.savedCompanies
    }

    fun trimSearchStringByOrganizationNumberLength() {
        searchString = searchString.substring(0, ORGANIZATION_NUMBER_LENGTH)
    }

    fun searchForCompaniesWithNamesBeginningWithText(text: String) {
        repository.searchForCompaniesWithNamesBeginningWithText(searchResultCompanyList, text)
    }

    // Searching by org number can only return 1 company, so the implementation isn't optimal
    // The result will be displayed in the same way as for when searching text,
    // which is why the same live data is passed in here.
    // TODO: Improve implementation
    fun searchForCompaniesWithOrgNumber(orgNumber: Int) {
        repository.searchForCompaniesWithOrgNumber(searchResultCompanyList, orgNumber)
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
