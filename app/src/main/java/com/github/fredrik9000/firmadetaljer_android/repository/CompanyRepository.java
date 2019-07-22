package com.github.fredrik9000.firmadetaljer_android.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompaniesJson;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyJson;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyService;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao;
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
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

    public void getAllCompaniesThatStartsWith(final MutableLiveData<CompanyListResponse> companyResponseMutableLiveData, String text) {
        Call<CompaniesJson> call = service.getCompanies("startswith(navn,'" + text + "')");
        call.enqueue(new Callback<CompaniesJson>() {
            @Override
            public void onResponse(Call<CompaniesJson> call, Response<CompaniesJson> response) {
                if (!response.isSuccessful()) {
                    companyResponseMutableLiveData.setValue(new CompanyListResponse(new HttpException(response)));
                    return;
                }
                CompaniesJson companiesJson = response.body();
                List<CompanyJson> companyJsonList = companiesJson.getData();
                List<Company> companies = new ArrayList<>();
                if (companyJsonList == null) {
                    companyResponseMutableLiveData.setValue(new CompanyListResponse(companies));
                    return;
                }

                for (CompanyJson companyJson : companyJsonList) {
                    Company company = createCompanyFromJson(companyJson);
                    companies.add(company);
                }

                companyResponseMutableLiveData.setValue(new CompanyListResponse(companies));
            }

            @Override
            public void onFailure(Call<CompaniesJson> call, Throwable t) {
                companyResponseMutableLiveData.setValue(new CompanyListResponse(t));
            }
        });
    }

    public void getCompanyWithOrgNumber(final MutableLiveData<CompanyResponse> companyResponseMutableLiveData, Integer orgNumber) {
        Call<CompanyJson> call = service.getCompanyWithOrgNumber(orgNumber);
        call.enqueue(new Callback<CompanyJson>() {
            @Override
            public void onResponse(Call<CompanyJson> call, Response<CompanyJson> response) {
                if (!response.isSuccessful()) {
                    // When a company doesn't exist a 400 status will return here.
                    // This is different from when searching for companies by name,
                    // because then the response will be successful with an empty list.
                    companyResponseMutableLiveData.setValue(new CompanyResponse(new HttpException(response)));
                    return;
                }
                CompanyJson companyJson = response.body();
                Company company = createCompanyFromJson(companyJson);
                companyResponseMutableLiveData.setValue(new CompanyResponse(company));
            }

            @Override
            public void onFailure(Call<CompanyJson> call, Throwable t) {
                companyResponseMutableLiveData.setValue(new CompanyResponse(t));
            }
        });
    }

    public void getCompaniesWithOrgNumber(final MutableLiveData<CompanyListResponse> companyResponseMutableLiveData, Integer orgNumber) {
        Call<CompanyJson> call = service.getCompanyWithOrgNumber(orgNumber);
        call.enqueue(new Callback<CompanyJson>() {
            @Override
            public void onResponse(Call<CompanyJson> call, Response<CompanyJson> response) {
                if (!response.isSuccessful()) {
                    // When a company doesn't exist a 400 status will return here.
                    // This is different from when searching for companies by name,
                    // because then the response will be successful with an empty list.
                    companyResponseMutableLiveData.setValue(new CompanyListResponse(new HttpException(response)));
                    return;
                }
                CompanyJson companyJson = response.body();
                Company company = createCompanyFromJson(companyJson);
                List<Company> companyList = new ArrayList<>();
                companyList.add(company);
                companyResponseMutableLiveData.setValue(new CompanyListResponse(companyList));
            }

            @Override
            public void onFailure(Call<CompanyJson> call, Throwable t) {
                companyResponseMutableLiveData.setValue(new CompanyListResponse(t));
            }
        });
    }

    private Company createCompanyFromJson(CompanyJson companyJson) {
        return new Company(companyJson.getOrganisasjonsnummer(), companyJson.getNavn(), companyJson.getStiftelsesdato(),
                companyJson.getRegistreringsdatoEnhetsregisteret(), companyJson.getOppstartsdato(), companyJson.getDatoEierskifte(),
                companyJson.getOrganisasjonsform(), companyJson.getHjemmeside(), companyJson.getRegistertIFrivillighetsregisteret(),
                companyJson.getRegistrertIMvaregisteret(), companyJson.getRegistrertIForetaksregisteret(), companyJson.getRegistrertIStiftelsesregisteret(),
                companyJson.getAntallAnsatte(), companyJson.getSisteInnsendteAarsregnskap(), companyJson.getKonkurs(),
                companyJson.getUnderAvvikling(), companyJson.getUnderTvangsavviklingEllerTvangsopplosning(), companyJson.getOverordnetEnhet(),
                companyJson.getInstitusjonellSektorkode() != null ? companyJson.getInstitusjonellSektorkode().getKode() : null,
                companyJson.getInstitusjonellSektorkode() != null ? companyJson.getInstitusjonellSektorkode().getBeskrivelse() : null,
                companyJson.getNaeringskode1() != null ? companyJson.getNaeringskode1().getKode() : null,
                companyJson.getNaeringskode1() != null ? companyJson.getNaeringskode1().getBeskrivelse() : null,
                companyJson.getNaeringskode2() != null ? companyJson.getNaeringskode2().getKode() : null,
                companyJson.getNaeringskode2() != null ? companyJson.getNaeringskode2().getBeskrivelse() : null,
                companyJson.getNaeringskode3() != null ? companyJson.getNaeringskode3().getKode() : null,
                companyJson.getNaeringskode3() != null ? companyJson.getNaeringskode3().getBeskrivelse() : null,
                companyJson.getPostadresse() != null ? companyJson.getPostadresse().getAdresse() : null,
                companyJson.getPostadresse() != null ? companyJson.getPostadresse().getPostnummer() : null,
                companyJson.getPostadresse() != null ? companyJson.getPostadresse().getPoststed() : null,
                companyJson.getPostadresse() != null ? companyJson.getPostadresse().getKommunenummer() : null,
                companyJson.getPostadresse() != null ? companyJson.getPostadresse().getKommune() : null,
                companyJson.getPostadresse() != null ? companyJson.getPostadresse().getLandkode() : null,
                companyJson.getPostadresse() != null ? companyJson.getPostadresse().getLand() : null,
                companyJson.getForretningsadresse() != null ? companyJson.getForretningsadresse().getAdresse() : null,
                companyJson.getForretningsadresse() != null ? companyJson.getForretningsadresse().getPostnummer() : null,
                companyJson.getForretningsadresse() != null ? companyJson.getForretningsadresse().getPoststed() : null,
                companyJson.getForretningsadresse() != null ? companyJson.getForretningsadresse().getKommunenummer() : null,
                companyJson.getForretningsadresse() != null ? companyJson.getForretningsadresse().getKommune() : null,
                companyJson.getForretningsadresse() != null ? companyJson.getForretningsadresse().getLandkode() : null,
                companyJson.getForretningsadresse() != null ? companyJson.getForretningsadresse().getLand() : null,
                companyJson.getBeliggenhetsadresse() != null ? companyJson.getBeliggenhetsadresse().getAdresse() : null,
                companyJson.getBeliggenhetsadresse() != null ? companyJson.getBeliggenhetsadresse().getPostnummer() : null,
                companyJson.getBeliggenhetsadresse() != null ? companyJson.getBeliggenhetsadresse().getPoststed() : null,
                companyJson.getBeliggenhetsadresse() != null ? companyJson.getBeliggenhetsadresse().getKommunenummer() : null,
                companyJson.getBeliggenhetsadresse() != null ? companyJson.getBeliggenhetsadresse().getKommune() : null,
                companyJson.getBeliggenhetsadresse() != null ? companyJson.getBeliggenhetsadresse().getLandkode() : null,
                companyJson.getBeliggenhetsadresse() != null ? companyJson.getBeliggenhetsadresse().getLand() : null);
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
