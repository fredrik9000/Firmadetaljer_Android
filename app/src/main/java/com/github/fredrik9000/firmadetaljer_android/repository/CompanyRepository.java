package com.github.fredrik9000.firmadetaljer_android.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompaniesJson;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyJson;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao;
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CompanyRepository {
    private CompanyDao companyDao;
    private CompanyService service;

    public CompanyRepository(Application application) {
        CompanyDatabase database = CompanyDatabase.getInstance(application);
        companyDao = database.companyDao();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://data.brreg.no/enhetsregisteret/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(CompanyService.class);
    }

    public void insert(Company company) {
        new InsertCompanyAsyncTask(companyDao).execute(company);
    }

    public void update(Company todo) {
        new UpdateCompanyAsyncTask(companyDao).execute(todo);
    }

    public void delete(Company todo) {
        new DeleteCompanyAsyncTask(companyDao).execute(todo);
    }

    public void getAllCompaniesThatStartsWith(final MutableLiveData<CompanyResponse> companyResponseMutableLiveData, String text) {
        Call<CompaniesJson> call = service.getCompanies("startswith(navn,'" + text + "')");
        call.enqueue(new Callback<CompaniesJson>() {
            @Override
            public void onResponse(Call<CompaniesJson> call, Response<CompaniesJson> response) {
                if (!response.isSuccessful()) {
                    //Todo: Handle error
                    return;
                }
                    CompaniesJson companiesJson = response.body();
                    List<CompanyJson> companyJsonList = companiesJson.getData();
                    List<Company> companies = new ArrayList<>();
                    if (companyJsonList == null) {
                        companyResponseMutableLiveData.setValue(new CompanyResponse(companies));
                        return;
                    }

                    //Todo: map data to a list of Company objects

                    for(CompanyJson companyJson : companyJsonList) {
                        Company company = new Company();
                        company.setNavn(companyJson.getNavn());
                        companies.add(company);
                    }

                companyResponseMutableLiveData.setValue(new CompanyResponse(companies));
            }

            @Override
            public void onFailure(Call<CompaniesJson> call, Throwable t) {
                companyResponseMutableLiveData.setValue(new CompanyResponse(t));
            }
        });
    }

    public MutableLiveData<Company> getCompanyWithOrgNumber(Integer orgNumber) {
        final MutableLiveData<Company> data = new MutableLiveData<>();
        Call<CompaniesJson> call = service.getCompanyWithOrgNumber(orgNumber);
        call.enqueue(new Callback<CompaniesJson>() {
            @Override
            public void onResponse(Call<CompaniesJson> call, Response<CompaniesJson> response) {
                if (!response.isSuccessful()) {
                    //Todo: Handle error
                    return;
                }
                CompaniesJson companiesJson = response.body();
                Company company = new Company();
                //Todo: map data to Company object
                data.setValue(company);
            }

            @Override
            public void onFailure(Call<CompaniesJson> call, Throwable t) {
                //Todo: Handle error;
            }
        });
        return data;
    }

    private static class InsertCompanyAsyncTask extends AsyncTask<Company, Void, Void> {

        private CompanyDao companyDao;

        private InsertCompanyAsyncTask(CompanyDao companyDao) {
            this.companyDao = companyDao;
        }

        @Override
        protected Void doInBackground(Company... companies) {
            companyDao.insert(companies[0]);
            return null;
        }
    }

    private static class UpdateCompanyAsyncTask extends AsyncTask<Company, Void, Void> {

        private CompanyDao companyDao;

        private UpdateCompanyAsyncTask(CompanyDao companyDao) {
            this.companyDao = companyDao;
        }

        @Override
        protected Void doInBackground(Company... companies) {
            companyDao.update(companies[0]);
            return null;
        }
    }

    private static class DeleteCompanyAsyncTask extends AsyncTask<Company, Void, Void> {

        private CompanyDao companyDao;

        private DeleteCompanyAsyncTask(CompanyDao companyDao) {
            this.companyDao = companyDao;
        }

        @Override
        protected Void doInBackground(Company... companies) {
            companyDao.delete(companies[0]);
            return null;
        }
    }
}
