package com.github.fredrik9000.firmadetaljer_android.repository.rest;

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

public class CompanyResponse {
    private Company company;
    private Throwable error;
    public CompanyResponse(Company company) {
        this.company = company;
        this.error = null;
    }
    public CompanyResponse(Throwable error) {
        this.error = error;
        this.company = null;
    }
    public Company getCompany() {
        return company;
    }
    public void setCompany(Company company) {
        this.company = company;
    }
    public Throwable getError() {
        return error;
    }
    public void setError(Throwable error) {
        this.error = error;
    }
}