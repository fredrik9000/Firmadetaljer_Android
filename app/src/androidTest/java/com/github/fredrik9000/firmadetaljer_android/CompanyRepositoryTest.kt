package com.github.fredrik9000.firmadetaljer_android

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.fredrik9000.firmadetaljer_android.company_list.CompanyListViewModel
import com.github.fredrik9000.firmadetaljer_android.company_list.SearchMode
import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CompanyRepositoryTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository : CompanyRepository
    private lateinit var companyService : CompanyService
    private lateinit var viewModel : CompanyListViewModel

    @Mock
    private lateinit var companyDao: CompanyDao

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        companyService = Retrofit.Builder()
                .baseUrl("https://data.brreg.no/enhetsregisteret/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CompanyService::class.java)

        repository = CompanyRepository(companyDao, companyService)
        viewModel = CompanyListViewModel(repository)
    }

    @Test
    fun searchOnSelectedSearchModeByName_ReturnsEmptyListOfCompanies() {
        viewModel.searchMode = SearchMode.FIRM_NAME
        viewModel.searchOnSelectedSearchMode("ThisCompanyDoesNotExist")
        Truth.assertThat((LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData) as CompanyListResponse.Success).companies.isEmpty()).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByOrganizationNumber_ReturnsHttpExceptionErrorWith400Or404() {
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        viewModel.searchOnSelectedSearchMode("123456789")
        val searchResultLiveDataValue = LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData)
        val error = (searchResultLiveDataValue as CompanyListResponse.Error).peekError()
        Truth.assertThat(error is HttpException && ((error.code() == 400) || (error.code() == 404))).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByName_ReturnsListOfCompanies() {
        viewModel.searchMode = SearchMode.FIRM_NAME
        viewModel.searchOnSelectedSearchMode("Microsoft")
        Truth.assertThat((LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData) as CompanyListResponse.Success).companies.isNotEmpty()).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByOrganizationNumber_ReturnsListOfOneCompany() {
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        viewModel.searchOnSelectedSearchMode("957485030") // Microsoft Norge AS
        Truth.assertThat((LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData) as CompanyListResponse.Success).companies.size == 1).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByOrganizationNumber_ReturnsCorrectCompany() {
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        val organizationNumber = "957485030" // Microsoft Norge AS
        viewModel.searchOnSelectedSearchMode(organizationNumber)
        val searchResultLiveDataValue = LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData) as CompanyListResponse.Success
        Truth.assertThat(searchResultLiveDataValue.companies.size == 1 && searchResultLiveDataValue.companies[0].organisasjonsnummer.toString() == organizationNumber).isTrue()
    }
}