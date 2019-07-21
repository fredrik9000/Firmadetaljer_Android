package com.github.fredrik9000.firmadetaljer_android;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse;

public class CompanyListViewModel extends AndroidViewModel {
    private MutableLiveData<CompanyListResponse> companyListLiveData;
    private CompanyRepository repository;

    public CompanyListViewModel(@NonNull Application application) {
        super(application);
        repository = new CompanyRepository(application);
        companyListLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<CompanyListResponse> getCompanyList() {
        return companyListLiveData;
    }

    public void searchForCompaniesThatStartsWith(String text) {
        repository.getAllCompaniesThatStartsWith(companyListLiveData, text);
    }

    // TODO: search for companies by organisasjonsnummer
}
