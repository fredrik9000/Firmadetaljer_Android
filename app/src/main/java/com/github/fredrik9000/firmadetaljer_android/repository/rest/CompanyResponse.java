package com.github.fredrik9000.firmadetaljer_android.repository.rest;

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

import java.util.List;

public class CompanyResponse {
    private List<Company> companies;
    private Throwable error;
    public CompanyResponse(List<Company> companies) {
        this.companies = companies;
        this.error = null;
    }
    public CompanyResponse(Throwable error) {
        this.error = error;
        this.companies = null;
    }
    public List<Company> getCompanies() {
        return companies;
    }
    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }
    public Throwable getError() {
        return error;
    }
    public void setError(Throwable error) {
        this.error = error;
    }
}