package com.github.fredrik9000.firmadetaljer_android.company_list

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse
import com.google.common.truth.Truth
import org.junit.Test

class CompanyListResponseTest {

    @Test
    fun getErrorIfNotHandled_ReturnsError() {
        val companyListResponse = CompanyListResponse.Error(Exception())
        val error = companyListResponse.getErrorIfNotHandled()
        Truth.assertThat(error != null && error == companyListResponse.peekError()).isTrue()
    }

    @Test
    fun getErrorIfNotHandled_ReturnsErrorWithMessage() {
        val companyListResponse = CompanyListResponse.Error(Exception("an error happened!"))
        val error = companyListResponse.getErrorIfNotHandled()
        Truth.assertThat(error!!.message == "an error happened!").isTrue()
    }

    @Test
    fun getErrorIfNotHandled_ReturnsNullTheSecondTime() {
        val companyListResponse = CompanyListResponse.Error(Exception())
        companyListResponse.getErrorIfNotHandled()
        val error = companyListResponse.getErrorIfNotHandled()
        Truth.assertThat(error == null).isTrue()
    }
}