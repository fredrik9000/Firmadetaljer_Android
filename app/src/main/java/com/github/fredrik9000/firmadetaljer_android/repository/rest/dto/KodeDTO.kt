package com.github.fredrik9000.firmadetaljer_android.repository.rest.dto

import kotlinx.serialization.Serializable

// Property names are in Norwegian as this is the language of the rest API
@Serializable
class KodeDTO {
    val kode: String? = null
    val beskrivelse: String? = null
}