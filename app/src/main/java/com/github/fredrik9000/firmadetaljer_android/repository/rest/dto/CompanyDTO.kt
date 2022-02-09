package com.github.fredrik9000.firmadetaljer_android.repository.rest.dto

import kotlinx.serialization.Serializable

// Property names are in Norwegian as this is the language of the rest API
@Serializable
class CompanyDTO {
    val organisasjonsnummer: String? = null

    val navn: String? = null
    val stiftelsesdato: String? = null
    val registreringsdatoEnhetsregisteret: String? = null
    val oppstartsdato: String? = null
    val datoEierskifte: String? = null
    val organisasjonsform: OrganisasjonsformDTO? = null
    val hjemmeside: String? = null
    val registertIFrivillighetsregisteret: Boolean? = null
    val registrertIMvaregisteret: Boolean? = null
    val registrertIForetaksregisteret: Boolean? = null
    val registrertIStiftelsesregisteret: Boolean? = null
    val antallAnsatte: Int? = null
    val sisteInnsendteAarsregnskap: Int? = null
    val konkurs: Boolean? = null
    val underAvvikling: Boolean? = null
    val underTvangsavviklingEllerTvangsopplosning: Boolean? = null
    val overordnetEnhet: String? = null

    val institusjonellSektorkode: KodeDTO? = null
    val naeringskode1: KodeDTO? = null
    val naeringskode2: KodeDTO? = null
    val naeringskode3: KodeDTO? = null

    val postadresse: AdresseDTO? = null
    val forretningsadresse: AdresseDTO? = null
    val beliggenhetsadresse: AdresseDTO? = null
}