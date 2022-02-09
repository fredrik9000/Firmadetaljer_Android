package com.github.fredrik9000.firmadetaljer_android.repository.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// Property names are in Norwegian as this is the language of the rest API which gets the data.
// This app is for Norwegian firms only, and some of these values are specific to Norway.
// Therefore, in order to be consistent the properties are not translated to English.

@Parcelize
@Entity(tableName = "company_table")
data class Company(@PrimaryKey(autoGenerate = true) var id: Int = 0,
              var organisasjonsnummer: Int,
              var navn: String? = null,
              var stiftelsesdato: String? = null,
              var registreringsdatoEnhetsregisteret: String? = null,
              var oppstartsdato: String? = null,
              var datoEierskifte: String? = null,
              var organisasjonsform: String? = null,
              var hjemmeside: String? = null,
              var registertIFrivillighetsregisteret: String? = null,
              var registrertIMvaregisteret: String? = null,
              var registrertIForetaksregisteret: String? = null,
              var registrertIStiftelsesregisteret: String? = null,
              var antallAnsatte: Int? = null,
              var sisteInnsendteAarsregnskap: Int? = null,
              var konkurs: String? = null,
              var underAvvikling: String? = null,
              var underTvangsavviklingEllerTvangsopplosning: String? = null,
              var overordnetEnhet: Int? = null,
              var institusjonellSektorkodeKode: String? = null,
              var institusjonellSektorkodeBeskrivelse: String? = null,
              var naeringskode1Kode: String? = null,
              var naeringskode1Beskrivelse: String? = null,
              var naeringskode2Kode: String? = null,
              var naeringskode2Beskrivelse: String? = null,
              var naeringskode3Kode: String? = null,
              var naeringskode3Beskrivelse: String? = null,
              var postadresseAdresse: String? = null,
              var postadressePostnummer: String? = null,
              var postadressePoststed: String? = null,
              var postadresseKommunenummer: String? = null,
              var postadresseKommune: String? = null,
              var postadresseLandkode: String? = null,
              var postadresseLand: String? = null,
              var forretningsadresseAdresse: String? = null,
              var forretningsadressePostnummer: String? = null,
              var forretningsadressePoststed: String? = null,
              var forretningsadresseKommunenummer: String? = null,
              var forretningsadresseKommune: String? = null,
              var forretningsadresseLandkode: String? = null,
              var forretningsadresseLand: String? = null,
              var beliggenhetsadresseAdresse: String? = null,
              var beliggenhetsadressePostnummer: String? = null,
              var beliggenhetsadressePoststed: String? = null,
              var beliggenhetsadresseKommunenummer: String? = null,
              var beliggenhetsadresseKommune: String? = null,
              var beliggenhetsadresseLandkode: String? = null,
              var beliggenhetsadresseLand: String? = null) : Parcelable