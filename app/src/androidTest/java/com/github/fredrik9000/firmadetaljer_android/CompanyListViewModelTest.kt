package com.github.fredrik9000.firmadetaljer_android

import com.github.fredrik9000.firmadetaljer_android.company_list.CompanyListViewModel
import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.github.fredrik9000.firmadetaljer_android.company_list.SearchMode
import org.junit.Rule
import retrofit2.HttpException

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
                .baseUrl("https://data.brreg.no/enhetsregisteret/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CompanyService::class.java)

        repository = CompanyRepository(companyDao, companyService)
        viewModel = CompanyListViewModel(repository)
    }

    @Test
    fun searchOnSelectedSearchModeByName_ReturnsEmptyListOfCompanies() {
        viewModel.searchMode = SearchMode.FIRM_NAME
        viewModel.searchOnSelectedSearchMode("This company does not exist")
        Truth.assertThat(LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData).companies?.isEmpty()).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByOrganizationNumber_ReturnsHttpExceptionErrorWith400Or404() {
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        viewModel.searchOnSelectedSearchMode("123456789")
        val searchResultLiveDataValue = LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData)
        val error = searchResultLiveDataValue.peekError()
        Truth.assertThat(searchResultLiveDataValue.companies == null && error != null && error is HttpException && ((error.code() == 400) || (error.code() == 404))).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByName_ReturnsListOfCompanies() {
        viewModel.searchMode = SearchMode.FIRM_NAME
        viewModel.searchOnSelectedSearchMode("Microsoft")
        Truth.assertThat(LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData).companies?.isNotEmpty()).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByOrganizationNumber_ReturnsListOfOneCompany() {
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        viewModel.searchOnSelectedSearchMode("957485030") // Microsoft Norge AS
        Truth.assertThat(LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData).companies?.size == 1).isTrue()
    }

    @Test
    fun searchOnSelectedSearchModeByOrganizationNumber_ReturnsCorrectCompany() {
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        val organizationNumber = "957485030" // Microsoft Norge AS
        viewModel.searchOnSelectedSearchMode(organizationNumber)
        val searchResultLiveDataValue = LiveDataUtil.getOrAwaitValue(viewModel.searchResultLiveData)
        Truth.assertThat(searchResultLiveDataValue.companies?.size == 1 && searchResultLiveDataValue.companies?.get(0)?.organisasjonsnummer.toString() == organizationNumber).isTrue()
    }
}