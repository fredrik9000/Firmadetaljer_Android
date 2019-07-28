package com.github.fredrik9000.firmadetaljer_android

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse

interface ICompanyResponseHandler {
    fun handleResponse(response: CompanyResponse)
}
