package com.github.fredrik9000.firmadetaljer_android;

public class CompanyDetailDescription {
    private String label;
    private String description;

    public CompanyDetailDescription(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }
}
