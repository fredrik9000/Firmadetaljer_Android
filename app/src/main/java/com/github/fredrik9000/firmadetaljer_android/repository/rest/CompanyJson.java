package com.github.fredrik9000.firmadetaljer_android.repository.rest;

public class CompanyJson {

    private Integer organisasjonsnummer;

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
    private Integer antallAnsatte;
    private Integer sisteInnsendteAarsregnskap;
    private String konkurs;
    private String underAvvikling;
    private String underTvangsavviklingEllerTvangsopplosning;
    private Integer overordnetEnhet;

    private CodeJson institusjonellSektorkode;
    private CodeJson naeringskode1;
    private CodeJson naeringskode2;
    private CodeJson naeringskode3;

    private AddressJson postadresse;
    private AddressJson forretningsadresse;
    private AddressJson beliggenhetsadresse;

    public Integer getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public String getNavn() {
        return navn;
    }

    public String getStiftelsesdato() {
        return stiftelsesdato;
    }

    public String getRegistreringsdatoEnhetsregisteret() {
        return registreringsdatoEnhetsregisteret;
    }

    public String getOppstartsdato() {
        return oppstartsdato;
    }

    public String getDatoEierskifte() {
        return datoEierskifte;
    }

    public String getOrganisasjonsform() {
        return organisasjonsform;
    }

    public String getHjemmeside() {
        return hjemmeside;
    }

    public String getRegistertIFrivillighetsregisteret() {
        return registertIFrivillighetsregisteret;
    }

    public String getRegistrertIMvaregisteret() {
        return registrertIMvaregisteret;
    }

    public String getRegistrertIForetaksregisteret() {
        return registrertIForetaksregisteret;
    }

    public String getRegistrertIStiftelsesregisteret() {
        return registrertIStiftelsesregisteret;
    }

    public Integer getAntallAnsatte() {
        return antallAnsatte;
    }

    public Integer getSisteInnsendteAarsregnskap() {
        return sisteInnsendteAarsregnskap;
    }

    public String getKonkurs() {
        return konkurs;
    }

    public String getUnderAvvikling() {
        return underAvvikling;
    }

    public String getUnderTvangsavviklingEllerTvangsopplosning() {
        return underTvangsavviklingEllerTvangsopplosning;
    }

    public Integer getOverordnetEnhet() {
        return overordnetEnhet;
    }

    public CodeJson getInstitusjonellSektorkode() {
        return institusjonellSektorkode;
    }

    public CodeJson getNaeringskode1() {
        return naeringskode1;
    }

    public CodeJson getNaeringskode2() {
        return naeringskode2;
    }

    public CodeJson getNaeringskode3() {
        return naeringskode3;
    }

    public AddressJson getPostadresse() {
        return postadresse;
    }

    public AddressJson getForretningsadresse() {
        return forretningsadresse;
    }

    public AddressJson getBeliggenhetsadresse() {
        return beliggenhetsadresse;
    }
}