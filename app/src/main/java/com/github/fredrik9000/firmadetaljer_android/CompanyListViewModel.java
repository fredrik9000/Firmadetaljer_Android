package com.github.fredrik9000.firmadetaljer_android;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.fredrik9000.firmadetaljer_android.repository.CompanyRepository;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

import java.util.List;

public class CompanyListViewModel extends AndroidViewModel {
    private MutableLiveData<CompanyListResponse> searchResultCompaniesLiveData;
    private LiveData<List<Company>> savedCompaniesLiveData;

    private CompanyRepository repository;

    public CompanyListViewModel(@NonNull Application application) {
        super(application);
        repository = new CompanyRepository(application);
        searchResultCompaniesLiveData = new MutableLiveData<>();
        savedCompaniesLiveData = repository.getAllSavedCompanies();
    }

    public MutableLiveData<CompanyListResponse> getSearchResultCompanyList() {
        return searchResultCompaniesLiveData;
    }

    public LiveData<List<Company>> getSavedCompanyList() {
        return savedCompaniesLiveData;
    }

    public void searchForCompaniesThatStartsWith(String text) {
        repository.getAllCompaniesThatStartsWith(searchResultCompaniesLiveData, text);
    }

    // Searching by org number can only return 1 company, so the implementation isn't optimal
    // The result will be displayed in the same way as for when searching text,
    // which is why the same live data is passed in here.
    // TODO: Improve implementation
    public void searchForCompaniesWithOrgNumber(Integer orgNumber) {
        repository.getCompaniesWithOrgNumber(searchResultCompaniesLiveData, orgNumber);
    }

    public void upsert(Company company) {
        repository.upsert(company);
    }
}
