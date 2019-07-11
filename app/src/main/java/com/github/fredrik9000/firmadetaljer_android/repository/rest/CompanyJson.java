package com.github.fredrik9000.firmadetaljer_android.repository.rest;

public class CompanyJson {

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

    private Kode institusjonellSektorkode;
    private Kode naeringskode1;
    private Kode naeringskode2;
    private Kode naeringskode3;

    private Address postadresse;
    private Address forretningsadresse;
    private Address beliggenhetsadresse;

    private class Address {
        private String postnummer;
        private String poststed;
        private String kommunenummer;
        private String kommune;
        private String landkode;
        private String land;
    }

    private class Kode {
        private String kode;
        private String beskrivelse;
    }

    public String getNavn() {
        return navn;
    }
}