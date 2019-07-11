package com.github.fredrik9000.firmadetaljer_android.repository.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "copmany_table")
public class Company {
    @PrimaryKey
    private int organisasjonsnummer;

    private String navn;
    private String stiftelsesdato;
    private String registreringsdatoEnhetsregisteret;
    private String oppstartsdato;
    private String datoEierskifte;
    private String organisasjonsform;
    private String hjemmeside;
    private String registertIFrivillighetsregisteret;
    private String registrertIMvaregisteret;
    private String registrertIForetaksregisteret;
    private String registrertIStiftelsesregisteret;
    private int antallAnsatte;
    private int sisteInnsendteAarsregnskap;
    private String konkurs;
    private String underAvvikling;
    private String underTvangsavviklingEllerTvangsopplosning;
    private int overordnetEnhet;

    private String institusjonellSektorkodeKode;
    private String institusjonellSektorkodeBeskrivelse;
    private String naeringskode1Kode;
    private String naeringskode1Beskrivelse;
    private String naeringskode2Kode;
    private String naeringskode2Beskrivelse;
    private String naeringskode3Kode;
    private String naeringskode3Beskrivelse;

    private String postadresseAdresse;
    private String postadressePostnummer;
    private String postadressePoststed;
    private String postadresseKommunenummer;
    private String postadresseKommune;
    private String postadresseLandkode;
    private String postadresseLand;

    private String forretningsadresseAdresse;
    private String forretningsadressePostnummer;
    private String forretningsadressePoststed;
    private String forretningsadresseKommunenummer;
    private String forretningsadresseKommune;
    private String forretningsadresseLandkode;
    private String forretningsadresseLand;

    private String beliggenhetsadresseAdresse;
    private String beliggenhetsadressePostnummer;
    private String beliggenhetsadressePoststed;
    private String beliggenhetsadresseKommunenummer;
    private String beliggenhetsadresseKommune;
    private String beliggenhetsadresseLandkode;
    private String beliggenhetsadresseLand;

    public int getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public void setOrganisasjonsnummer(int organisasjonsnummer) {
        this.organisasjonsnummer = organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public void setNavn(String navn) {
        this.navn = navn;
    }

    public String getStiftelsesdato() {
        return stiftelsesdato;
    }

    public void setStiftelsesdato(String stiftelsesdato) {
        this.stiftelsesdato = stiftelsesdato;
    }

    public String getRegistreringsdatoEnhetsregisteret() {
        return registreringsdatoEnhetsregisteret;
    }

    public void setRegistreringsdatoEnhetsregisteret(String registreringsdatoEnhetsregisteret) {
        this.registreringsdatoEnhetsregisteret = registreringsdatoEnhetsregisteret;
    }

    public String getOppstartsdato() {
        return oppstartsdato;
    }

    public void setOppstartsdato(String oppstartsdato) {
        this.oppstartsdato = oppstartsdato;
    }

    public String getDatoEierskifte() {
        return datoEierskifte;
    }

    public void setDatoEierskifte(String datoEierskifte) {
        this.datoEierskifte = datoEierskifte;
    }

    public String getOrganisasjonsform() {
        return organisasjonsform;
    }

    public void setOrganisasjonsform(String organisasjonsform) {
        this.organisasjonsform = organisasjonsform;
    }

    public String getHjemmeside() {
        return hjemmeside;
    }

    public void setHjemmeside(String hjemmeside) {
        this.hjemmeside = hjemmeside;
    }

    public String getRegistertIFrivillighetsregisteret() {
        return registertIFrivillighetsregisteret;
    }

    public void setRegistertIFrivillighetsregisteret(String registertIFrivillighetsregisteret) {
        this.registertIFrivillighetsregisteret = registertIFrivillighetsregisteret;
    }

    public String getRegistrertIMvaregisteret() {
        return registrertIMvaregisteret;
    }

    public void setRegistrertIMvaregisteret(String registrertIMvaregisteret) {
        this.registrertIMvaregisteret = registrertIMvaregisteret;
    }

    public String getRegistrertIForetaksregisteret() {
        return registrertIForetaksregisteret;
    }

    public void setRegistrertIForetaksregisteret(String registrertIForetaksregisteret) {
        this.registrertIForetaksregisteret = registrertIForetaksregisteret;
    }

    public String getRegistrertIStiftelsesregisteret() {
        return registrertIStiftelsesregisteret;
    }

    public void setRegistrertIStiftelsesregisteret(String registrertIStiftelsesregisteret) {
        this.registrertIStiftelsesregisteret = registrertIStiftelsesregisteret;
    }

    public int getAntallAnsatte() {
        return antallAnsatte;
    }

    public void setAntallAnsatte(int antallAnsatte) {
        this.antallAnsatte = antallAnsatte;
    }

    public int getSisteInnsendteAarsregnskap() {
        return sisteInnsendteAarsregnskap;
    }

    public void setSisteInnsendteAarsregnskap(int sisteInnsendteAarsregnskap) {
        this.sisteInnsendteAarsregnskap = sisteInnsendteAarsregnskap;
    }

    public String getKonkurs() {
        return konkurs;
    }

    public void setKonkurs(String konkurs) {
        this.konkurs = konkurs;
    }

    public String getUnderAvvikling() {
        return underAvvikling;
    }

    public void setUnderAvvikling(String underAvvikling) {
        this.underAvvikling = underAvvikling;
    }

    public String getUnderTvangsavviklingEllerTvangsopplosning() {
        return underTvangsavviklingEllerTvangsopplosning;
    }

    public void setUnderTvangsavviklingEllerTvangsopplosning(String underTvangsavviklingEllerTvangsopplosning) {
        this.underTvangsavviklingEllerTvangsopplosning = underTvangsavviklingEllerTvangsopplosning;
    }

    public int getOverordnetEnhet() {
        return overordnetEnhet;
    }

    public void setOverordnetEnhet(int overordnetEnhet) {
        this.overordnetEnhet = overordnetEnhet;
    }

    public String getInstitusjonellSektorkodeKode() {
        return institusjonellSektorkodeKode;
    }

    public void setInstitusjonellSektorkodeKode(String institusjonellSektorkodeKode) {
        this.institusjonellSektorkodeKode = institusjonellSektorkodeKode;
    }

    public String getInstitusjonellSektorkodeBeskrivelse() {
        return institusjonellSektorkodeBeskrivelse;
    }

    public void setInstitusjonellSektorkodeBeskrivelse(String institusjonellSektorkodeBeskrivelse) {
        this.institusjonellSektorkodeBeskrivelse = institusjonellSektorkodeBeskrivelse;
    }

    public String getNaeringskode1Kode() {
        return naeringskode1Kode;
    }

    public void setNaeringskode1Kode(String naeringskode1Kode) {
        this.naeringskode1Kode = naeringskode1Kode;
    }

    public String getNaeringskode1Beskrivelse() {
        return naeringskode1Beskrivelse;
    }

    public void setNaeringskode1Beskrivelse(String naeringskode1Beskrivelse) {
        this.naeringskode1Beskrivelse = naeringskode1Beskrivelse;
    }

    public String getNaeringskode2Kode() {
        return naeringskode2Kode;
    }

    public void setNaeringskode2Kode(String naeringskode2Kode) {
        this.naeringskode2Kode = naeringskode2Kode;
    }

    public String getNaeringskode2Beskrivelse() {
        return naeringskode2Beskrivelse;
    }

    public void setNaeringskode2Beskrivelse(String naeringskode2Beskrivelse) {
        this.naeringskode2Beskrivelse = naeringskode2Beskrivelse;
    }

    public String getNaeringskode3Kode() {
        return naeringskode3Kode;
    }

    public void setNaeringskode3Kode(String naeringskode3Kode) {
        this.naeringskode3Kode = naeringskode3Kode;
    }

    public String getNaeringskode3Beskrivelse() {
        return naeringskode3Beskrivelse;
    }

    public void setNaeringskode3Beskrivelse(String naeringskode3Beskrivelse) {
        this.naeringskode3Beskrivelse = naeringskode3Beskrivelse;
    }

    public String getPostadresseAdresse() {
        return postadresseAdresse;
    }

    public void setPostadresseAdresse(String postadresseAdresse) {
        this.postadresseAdresse = postadresseAdresse;
    }

    public String getPostadressePostnummer() {
        return postadressePostnummer;
    }

    public void setPostadressePostnummer(String postadressePostnummer) {
        this.postadressePostnummer = postadressePostnummer;
    }

    public String getPostadressePoststed() {
        return postadressePoststed;
    }

    public void setPostadressePoststed(String postadressePoststed) {
        this.postadressePoststed = postadressePoststed;
    }

    public String getPostadresseKommunenummer() {
        return postadresseKommunenummer;
    }

    public void setPostadresseKommunenummer(String postadresseKommunenummer) {
        this.postadresseKommunenummer = postadresseKommunenummer;
    }

    public String getPostadresseKommune() {
        return postadresseKommune;
    }

    public void setPostadresseKommune(String postadresseKommune) {
        this.postadresseKommune = postadresseKommune;
    }

    public String getPostadresseLandkode() {
        return postadresseLandkode;
    }

    public void setPostadresseLandkode(String postadresseLandkode) {
        this.postadresseLandkode = postadresseLandkode;
    }

    public String getPostadresseLand() {
        return postadresseLand;
    }

    public void setPostadresseLand(String postadresseLand) {
        this.postadresseLand = postadresseLand;
    }

    public String getForretningsadresseAdresse() {
        return forretningsadresseAdresse;
    }

    public void setForretningsadresseAdresse(String forretningsadresseAdresse) {
        this.forretningsadresseAdresse = forretningsadresseAdresse;
    }

    public String getForretningsadressePostnummer() {
        return forretningsadressePostnummer;
    }

    public void setForretningsadressePostnummer(String forretningsadressePostnummer) {
        this.forretningsadressePostnummer = forretningsadressePostnummer;
    }

    public String getForretningsadressePoststed() {
        return forretningsadressePoststed;
    }

    public void setForretningsadressePoststed(String forretningsadressePoststed) {
        this.forretningsadressePoststed = forretningsadressePoststed;
    }

    public String getForretningsadresseKommunenummer() {
        return forretningsadresseKommunenummer;
    }

    public void setForretningsadresseKommunenummer(String forretningsadresseKommunenummer) {
        this.forretningsadresseKommunenummer = forretningsadresseKommunenummer;
    }

    public String getForretningsadresseKommune() {
        return forretningsadresseKommune;
    }

    public void setForretningsadresseKommune(String forretningsadresseKommune) {
        this.forretningsadresseKommune = forretningsadresseKommune;
    }

    public String getForretningsadresseLandkode() {
        return forretningsadresseLandkode;
    }

    public void setForretningsadresseLandkode(String forretningsadresseLandkode) {
        this.forretningsadresseLandkode = forretningsadresseLandkode;
    }

    public String getForretningsadresseLand() {
        return forretningsadresseLand;
    }

    public void setForretningsadresseLand(String forretningsadresseLand) {
        this.forretningsadresseLand = forretningsadresseLand;
    }

    public String getBeliggenhetsadresseAdresse() {
        return beliggenhetsadresseAdresse;
    }

    public void setBeliggenhetsadresseAdresse(String beliggenhetsadresseAdresse) {
        this.beliggenhetsadresseAdresse = beliggenhetsadresseAdresse;
    }

    public String getBeliggenhetsadressePostnummer() {
        return beliggenhetsadressePostnummer;
    }

    public void setBeliggenhetsadressePostnummer(String beliggenhetsadressePostnummer) {
        this.beliggenhetsadressePostnummer = beliggenhetsadressePostnummer;
    }

    public String getBeliggenhetsadressePoststed() {
        return beliggenhetsadressePoststed;
    }

    public void setBeliggenhetsadressePoststed(String beliggenhetsadressePoststed) {
        this.beliggenhetsadressePoststed = beliggenhetsadressePoststed;
    }

    public String getBeliggenhetsadresseKommunenummer() {
        return beliggenhetsadresseKommunenummer;
    }

    public void setBeliggenhetsadresseKommunenummer(String beliggenhetsadresseKommunenummer) {
        this.beliggenhetsadresseKommunenummer = beliggenhetsadresseKommunenummer;
    }

    public String getBeliggenhetsadresseKommune() {
        return beliggenhetsadresseKommune;
    }

    public void setBeliggenhetsadresseKommune(String beliggenhetsadresseKommune) {
        this.beliggenhetsadresseKommune = beliggenhetsadresseKommune;
    }

    public String getBeliggenhetsadresseLandkode() {
        return beliggenhetsadresseLandkode;
    }

    public void setBeliggenhetsadresseLandkode(String beliggenhetsadresseLandkode) {
        this.beliggenhetsadresseLandkode = beliggenhetsadresseLandkode;
    }

    public String getBeliggenhetsadresseLand() {
        return beliggenhetsadresseLand;
    }

    public void setBeliggenhetsadresseLand(String beliggenhetsadresseLand) {
        this.beliggenhetsadresseLand = beliggenhetsadresseLand;
    }
}