package com.github.fredrik9000.firmadetaljer_android.company_list

import androidx.recyclerview.widget.DiffUtil
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

class CompanyDiffCallback(
        private val companyListOld: List<Company>,
        private val companyListNew: List<Company>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return companyListOld.get(oldItemPosition).organisasjonsnummer == companyListNew.get(newItemPosition).organisasjonsnummer
    }

    override fun getOldListSize(): Int {
        return companyListOld.size
    }

    override fun getNewListSize(): Int {
        return companyListNew.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = companyListOld.get(oldItemPosition);
        val newItem = companyListNew.get(newItemPosition)
        return oldItem.organisasjonsnummer == newItem.organisasjonsnummer
                && oldItem.navn == newItem.navn
                && oldItem.stiftelsesdato == newItem.stiftelsesdato
                && oldItem.registreringsdatoEnhetsregisteret == newItem.registreringsdatoEnhetsregisteret
                && oldItem.oppstartsdato == newItem.oppstartsdato
                && oldItem.datoEierskifte == newItem.datoEierskifte
                && oldItem.organisasjonsform == newItem.organisasjonsform
                && oldItem.hjemmeside == newItem.hjemmeside
                && oldItem.registertIFrivillighetsregisteret == newItem.registertIFrivillighetsregisteret
                && oldItem.registrertIMvaregisteret == newItem.registrertIMvaregisteret
                && oldItem.registrertIForetaksregisteret == newItem.registrertIForetaksregisteret
                && oldItem.registrertIStiftelsesregisteret == newItem.registrertIStiftelsesregisteret
                && oldItem.antallAnsatte == newItem.antallAnsatte
                && oldItem.sisteInnsendteAarsregnskap == newItem.sisteInnsendteAarsregnskap
                && oldItem.konkurs == newItem.konkurs
                && oldItem.underAvvikling == newItem.underAvvikling
                && oldItem.underTvangsavviklingEllerTvangsopplosning == newItem.underTvangsavviklingEllerTvangsopplosning
                && oldItem.overordnetEnhet == newItem.overordnetEnhet
                && oldItem.institusjonellSektorkodeKode == newItem.institusjonellSektorkodeKode
                && oldItem.institusjonellSektorkodeBeskrivelse == newItem.institusjonellSektorkodeBeskrivelse
                && oldItem.naeringskode1Kode == newItem.naeringskode1Kode
                && oldItem.naeringskode1Beskrivelse == newItem.naeringskode1Beskrivelse
                && oldItem.naeringskode2Kode == newItem.naeringskode2Kode
                && oldItem.naeringskode2Beskrivelse == newItem.naeringskode2Beskrivelse
                && oldItem.naeringskode3Kode == newItem.naeringskode3Kode
                && oldItem.naeringskode3Beskrivelse == newItem.naeringskode3Beskrivelse
                && oldItem.postadresseAdresse == newItem.postadresseAdresse
                && oldItem.postadressePostnummer == newItem.postadressePostnummer
                && oldItem.postadressePoststed == newItem.postadressePoststed
                && oldItem.postadresseKommunenummer == newItem.postadresseKommunenummer
                && oldItem.postadresseKommune == newItem.postadresseKommune
                && oldItem.postadresseLandkode == newItem.postadresseLandkode
                && oldItem.postadresseLand == newItem.postadresseLand
                && oldItem.forretningsadresseAdresse == newItem.forretningsadresseAdresse
                && oldItem.forretningsadressePostnummer == newItem.forretningsadressePostnummer
                && oldItem.forretningsadressePoststed == newItem.forretningsadressePoststed
                && oldItem.forretningsadresseKommunenummer == newItem.forretningsadresseKommunenummer
                && oldItem.forretningsadresseKommune == newItem.forretningsadresseKommune
                && oldItem.forretningsadresseLandkode == newItem.forretningsadresseLandkode
                && oldItem.forretningsadresseLand == newItem.forretningsadresseLand
                && oldItem.beliggenhetsadresseAdresse == newItem.beliggenhetsadresseAdresse
                && oldItem.beliggenhetsadressePostnummer == newItem.beliggenhetsadressePostnummer
                && oldItem.beliggenhetsadressePoststed == newItem.beliggenhetsadressePoststed
                && oldItem.beliggenhetsadresseKommunenummer == newItem.beliggenhetsadresseKommunenummer
                && oldItem.beliggenhetsadresseKommune == newItem.beliggenhetsadresseKommune
                && oldItem.beliggenhetsadresseLandkode == newItem.beliggenhetsadresseLandkode
                && oldItem.beliggenhetsadresseLand == newItem.beliggenhetsadresseLand
    }
}