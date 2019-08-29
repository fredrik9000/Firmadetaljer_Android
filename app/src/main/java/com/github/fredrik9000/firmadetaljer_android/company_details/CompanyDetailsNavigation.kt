package com.github.fredrik9000.firmadetaljer_android.company_details

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse

interface CompanyDetailsNavigation {
    fun navigateToCompany(organisasjonsnummer: Int)
    fun navigateToHomepage(url: String)
    fun handleCompanyNavigationResponse(response: CompanyResponse)
}
