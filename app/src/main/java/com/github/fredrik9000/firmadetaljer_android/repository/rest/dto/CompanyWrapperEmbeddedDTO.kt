package com.github.fredrik9000.firmadetaljer_android.repository.rest.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CompanyWrapperEmbeddedDTO {
    @SerialName("_embedded")
    val embedded: CompanyWrapperEnheterDTO? = null
}