package com.github.fredrik9000.firmadetaljer_android.repository.rest

class CompanyJson {

    val organisasjonsnummer: Int? = null

    val navn: String? = null
    val stiftelsesdato: String? = null
    val registreringsdatoEnhetsregisteret: String? = null
    val oppstartsdato: String? = null
    val datoEierskifte: String? = null
    val organisasjonsform: String? = null
    val hjemmeside: String? = null
    val registertIFrivillighetsregisteret: String? = null
    val registrertIMvaregisteret: String? = null
    val registrertIForetaksregisteret: String? = null
    val registrertIStiftelsesregisteret: String? = null
    val antallAnsatte: Int? = null
    val sisteInnsendteAarsregnskap: Int? = null
    val konkurs: String? = null
    val underAvvikling: String? = null
    val underTvangsavviklingEllerTvangsopplosning: String? = null
    val overordnetEnhet: Int? = null

    val institusjonellSektorkode: CodeJson? = null
    val naeringskode1: CodeJson? = null
    val naeringskode2: CodeJson? = null
    val naeringskode3: CodeJson? = null

    val postadresse: AddressJson? = null
    val forretningsadresse: AddressJson? = null
    val beliggenhetsadresse: AddressJson? = null
}