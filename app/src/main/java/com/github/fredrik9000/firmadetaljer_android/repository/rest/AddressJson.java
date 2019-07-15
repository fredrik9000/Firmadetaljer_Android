package com.github.fredrik9000.firmadetaljer_android.repository.rest;

public class AddressJson {
    private String adresse;
    private String postnummer;
    private String poststed;
    private String kommunenummer;
    private String kommune;
    private String landkode;
    private String land;

    public String getAdresse() { return adresse; }

    public String getPostnummer() {
        return postnummer;
    }

    public String getPoststed() {
        return poststed;
    }

    public String getKommunenummer() {
        return kommunenummer;
    }

    public String getKommune() {
        return kommune;
    }

    public String getLandkode() {
        return landkode;
    }

    public String getLand() {
        return land;
    }
}
