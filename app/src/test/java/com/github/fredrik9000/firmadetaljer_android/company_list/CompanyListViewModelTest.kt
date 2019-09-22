package com.github.fredrik9000.firmadetaljer_android.company_list

import android.app.Application
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class CompanyListViewModelTest {

    private lateinit var viewModel : CompanyListViewModel
    private lateinit var applicationContext: Application

    @Before
    fun init() {
        applicationContext = ApplicationProvider.getApplicationContext()
        viewModel = CompanyListViewModel(applicationContext)
    }

    @Test
    fun isSearchingWithValidOrganizationNumber_ReturnsTrue() {
        viewModel.searchString = "123456789"
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        Truth.assertThat(viewModel.isSearchingWithValidOrganizationNumber).isTrue()
    }

    @Test
    fun isSearchingWithValidOrganizationNumber_WrongSearchModeAndReturnsFalse() {
        viewModel.searchString = "123456789"
        viewModel.searchMode = SearchMode.FIRM_NAME
        Truth.assertThat(viewModel.isSearchingWithValidOrganizationNumber).isFalse()
    }

    @Test
    fun isSearchingWithValidOrganizationNumber_TooShortSearchStringAndReturnsFalse() {
        viewModel.searchString = "12345678"
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        Truth.assertThat(viewModel.isSearchingWithValidOrganizationNumber).isFalse()
    }

    @Test
    fun isSearchingWithValidOrganizationNumber_NotNumericAndReturnsFalse() {
        viewModel.searchString = "12345678a"
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        Truth.assertThat(viewModel.isSearchingWithValidOrganizationNumber).isFalse()
    }

    @Test
    fun isSearchingWithValidFirmName_ReturnsTrue() {
        viewModel.searchString = "firm name"
        viewModel.searchMode = SearchMode.FIRM_NAME
        Truth.assertThat(viewModel.isSearchingWithValidFirmName).isTrue()
    }

    @Test
    fun isSearchingWithValidFirmName_WrongSearchModeAndReturnsFalse() {
        viewModel.searchString = "firm name"
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        Truth.assertThat(viewModel.isSearchingWithValidFirmName).isFalse()
    }

    @Test
    fun isSearchingWithValidFirmName_TooShortSearchStringAndReturnsFalse() {
        viewModel.searchString = "f"
        viewModel.searchMode = SearchMode.FIRM_NAME
        Truth.assertThat(viewModel.isSearchingWithValidFirmName).isFalse()
    }

    @Test
    fun isSearchingWithValidInput_ReturnsTrue1() {
        viewModel.searchString = "123456789"
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        Truth.assertThat(viewModel.isSearchingWithValidInput).isTrue()
    }

    @Test
    fun isSearchingWithValidInput_ReturnsTrue2() {
        viewModel.searchString = "firm name"
        viewModel.searchMode = SearchMode.FIRM_NAME
        Truth.assertThat(viewModel.isSearchingWithValidInput).isTrue()
    }

    @Test
    fun isSearchingWithValidInput_ReturnsFalse1() {
        viewModel.searchString = "firm name"
        viewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
        Truth.assertThat(viewModel.isSearchingWithValidInput).isFalse()
    }

    @Test
    fun isSearchingWithValidInput_ReturnsFalse2() {
        viewModel.searchString = "f"
        viewModel.searchMode = SearchMode.FIRM_NAME
        Truth.assertThat(viewModel.isSearchingWithValidInput).isFalse()
    }

    @Test
    fun searchStringExceedsOrganizationNumberLength_ReturnsTrue() {
        viewModel.searchString = "1234567891"
        Truth.assertThat(viewModel.searchStringExceedsOrganizationNumberLength).isTrue()
    }

    @Test
    fun searchStringExceedsOrganizationNumberLength_ReturnsFalse() {
        viewModel.searchString = "123456789"
        Truth.assertThat(viewModel.searchStringExceedsOrganizationNumberLength).isFalse()
    }

    @Test
    fun trimSearchStringByOrganizationNumberLength_ReturnsTrue() {
        viewModel.searchString = "1234567891"
        viewModel.trimSearchStringByOrganizationNumberLength()
        Truth.assertThat(viewModel.searchString.length == 9).isTrue()
    }

}