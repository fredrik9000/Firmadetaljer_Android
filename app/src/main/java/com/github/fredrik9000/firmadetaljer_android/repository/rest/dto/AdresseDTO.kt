package com.github.fredrik9000.firmadetaljer_android.repository.rest.dto

import kotlinx.serialization.Serializable

// Property names are in Norwegian as this is the language of the rest API
@Serializable
class AdresseDTO {
    val adresse: List<String>? = null
    val postnummer: String? = null
    val poststed: String? = null
    val kommunenummer: String? = null
    val kommune: String? = null
    val landkode: String? = null
    val land: String? = null
}
