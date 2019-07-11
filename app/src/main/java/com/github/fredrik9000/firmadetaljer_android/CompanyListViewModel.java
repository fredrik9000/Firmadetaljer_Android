package com.github.fredrik9000.firmadetaljer_android;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

public class CompanyListViewModel extends AndroidViewModel {
    private MutableLiveData<CompanyResponse> companyListLiveData;
    private CompanyRepository repository;

    public CompanyListViewModel(@NonNull Application application) {
        super(application);
        repository = new CompanyRepository(application);
        companyListLiveData = new MutableLiveData<>();
    }

    public void insert(Company company) {
        repository.insert(company);
    }

    public void delete(Company company) {
        repository.delete(company);
    }

    public void update(Company company) {
        repository.update(company);
    }

    public MutableLiveData<CompanyResponse> getCompanyList() {
        return companyListLiveData;
    }

    public void searchForCompaniesThatStartsWith(String text) {
        repository.getAllCompaniesThatStartsWith(companyListLiveData, text);
    }

    //Handle search for organization number.
}
