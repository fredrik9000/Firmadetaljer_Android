package com.github.fredrik9000.firmadetaljer_android;

public class CompanyDetailsDescription {
    private String label;
    private String description;

    public CompanyDetailsDescription(String label, String description) {
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
