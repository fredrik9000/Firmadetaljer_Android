package com.github.fredrik9000.firmadetaljer_android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityMainBinding;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyListResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

import java.util.ArrayList;
import java.util.List;

import retrofit2.HttpException;

public class MainActivity extends AppCompatActivity implements CompanyListAdapter.OnItemClickListener, ICompanyDetails {

    private ProgressBar progressBarList, progressBarDetails;
    private CompanyListViewModel companyListViewModel;
    private CompanyDetailsViewModel companyDetailsViewModel;
    private CompanyListAdapter adapterSearchList, adapterSavedList;
    private static final String TAG = "MainActivity";
    private boolean isTwoPane;
    private static final String SEARCH_KEY = "SEARCH";
    private static final String SEARCH_BY_ORG_NUMBER_KEY = "SEARCH_BY_ORG_NUMBER";
    private SearchView searchView;
    private String searchString = "";
    private boolean isSearchingByOrgNumber = false;
    private RecyclerView recyclerView;
    private static final int MINIMUM_SEARCH_LENGTH = 2;
    private static final int ORGANIZATION_NUMBER_LENGTH = 9;
    private List<Company> savedCompanies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle(R.string.main_activity_title);
        progressBarList = binding.progressCompanyList;
        progressBarDetails = binding.progressCompanyDetails;
        Toolbar toolbar = binding.includedToolbar.toolbarMain;
        setSupportActionBar(toolbar);

        companyListViewModel = ViewModelProviders.of(this).get(CompanyListViewModel.class);

        if (findViewById(R.id.company_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            isTwoPane = true;
            companyDetailsViewModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel.class);
        }

        setupRecyclerView(binding);
        adapterSavedList = new CompanyListAdapter(MainActivity.this, new ArrayList<Company>());
        adapterSearchList = new CompanyListAdapter(MainActivity.this, new ArrayList<Company>());

        if (savedInstanceState != null) {
            searchString = savedInstanceState.getString(SEARCH_KEY);
            isSearchingByOrgNumber = savedInstanceState.getBoolean(SEARCH_BY_ORG_NUMBER_KEY);
        }

        // In case the activity gets recreated (for example by rotating the device)
        // the correct adapter should be set.
        if (isValidFirmNameQuery() || isValidOrganizationNumberQuery()) {
            binding.includedCompanyList.companyListHeader.setText(R.string.search_results_header);
            recyclerView.setAdapter(adapterSearchList);
        } else {
            binding.includedCompanyList.companyListHeader.setText(R.string.last_viewed_companies_header);
            recyclerView.setAdapter(adapterSavedList);
        }

        setupSearchView(toolbar, binding);
        setupSearchResultObserver(binding);
        setupSavedCompaniesObserver(binding);

        if (isTwoPane) {
            setupCompanyDetailsObserver();
        }
    }

    private void setupRecyclerView(ActivityMainBinding binding) {
        recyclerView = binding.includedCompanyList.companyList;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation()));
    }

    // Used for when navigating to a parent company from company details view
    private void setupCompanyDetailsObserver() {
        companyDetailsViewModel.getCompany().observe(this, new Observer<CompanyResponse>() {
            @Override
            public void onChanged(@Nullable CompanyResponse companyResponse) {
                progressBarDetails.setVisibility(View.GONE);
                if (companyResponse.getCompany() != null) {
                    inflateCompanyDetailsFragment(companyResponse.getCompany(), true);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged() called with companyResponse error = " + companyResponse.getError());
                }
            }
        });
    }

    // Used for when updating saved companies (happens when viewing one)
    // Saved companies is shown whenever the user isn't searching
    private void setupSavedCompaniesObserver(final ActivityMainBinding binding) {
        companyListViewModel.getSavedCompanyList().observe(this, new Observer<List<Company>>() {
            @Override
            public void onChanged(@Nullable List<Company> companyList) {
                savedCompanies = companyList;
                adapterSavedList.update(companyList);
                checkForEmptyView(binding);
            }
        });
    }

    // Handles search results and updates the list
    private void setupSearchResultObserver(final ActivityMainBinding binding) {
        companyListViewModel.getSearchResultCompanyList().observe(this, new Observer<CompanyListResponse>() {
            @Override
            public void onChanged(@Nullable CompanyListResponse companyListResponse) {
                progressBarList.setVisibility(View.GONE);
                if (companyListResponse.getCompanies() != null) {
                    adapterSearchList.update(companyListResponse.getCompanies());
                } else {
                    // If there was an error searching (or organization number not found) set results to empty.
                    adapterSearchList.update(new ArrayList<Company>());
                    String toastMessage;
                    if (isSearchingByOrgNumber && companyListResponse.getError() instanceof HttpException
                            && (((HttpException) companyListResponse.getError()).code() == 400)
                            || ((HttpException) companyListResponse.getError()).code() == 404) {
                        // When an organization number is not found, the service can return either 400 or 404.
                        toastMessage = getResources().getString(R.string.company_not_found);
                    } else {
                        toastMessage = getResources().getString(R.string.search_request_error_message);
                    }
                    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged() called with companyListResponse error = " + companyListResponse.getError());
                }
                binding.includedCompanyList.emptyView.setVisibility(View.GONE);
                binding.includedCompanyList.companyListWithHeader.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupSearchView(Toolbar toolbar, final ActivityMainBinding binding) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = toolbar.findViewById(R.id.action_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if (isSearchingByOrgNumber) {
            searchView.setQueryHint(getString(R.string.company_search_org_number_hint));
        } else {
            searchView.setQueryHint(getString(R.string.company_search_name_hint));
        }

        if (!searchString.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(searchString, true);
            searchView.clearFocus();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isValidFirmNameQuery()) {
                    progressBarList.setVisibility(View.VISIBLE);
                    companyListViewModel.searchForCompaniesThatStartsWith(query);
                    // clearFocus() closes the full screen search view for phones in landscape mode.
                    searchView.clearFocus();
                    return true;
                } else if (isValidOrganizationNumberQuery()) {
                    progressBarList.setVisibility(View.VISIBLE);
                    companyListViewModel.searchForCompaniesWithOrgNumber(Integer.parseInt(query));
                    searchView.clearFocus();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchString = newText;
                checkForEmptyView(binding);

                // Check the current state and set the correct adapter
                if ((isValidFirmNameQuery() || isValidOrganizationNumberQuery()) && recyclerView.getAdapter() == adapterSavedList) {
                    binding.includedCompanyList.companyListHeader.setText(R.string.search_results_header);
                    recyclerView.setAdapter(adapterSearchList);
                } else if (!isValidFirmNameQuery() && !isValidOrganizationNumberQuery() && recyclerView.getAdapter() == adapterSearchList) {
                    binding.includedCompanyList.companyListHeader.setText(R.string.last_viewed_companies_header);
                    recyclerView.setAdapter(adapterSavedList);
                }

                if (isSearchingByOrgNumber) {
                    // Since organization number should be 9, it will be trimmed whenever the user tries to type more
                    if (newText.length() > ORGANIZATION_NUMBER_LENGTH) {
                        searchView.setQuery(newText.substring(0, ORGANIZATION_NUMBER_LENGTH), false);
                    } else if (isValidOrganizationNumberQuery()) {
                        progressBarList.setVisibility(View.VISIBLE);
                        companyListViewModel.searchForCompaniesWithOrgNumber(Integer.parseInt(newText));
                    }
                    return true;
                } else if (newText.length() >= MINIMUM_SEARCH_LENGTH) {
                    progressBarList.setVisibility(View.VISIBLE);
                    companyListViewModel.searchForCompaniesThatStartsWith(newText);
                    return true;
                }
                return false;
            }
        });

        binding.includedToolbar.toggle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == binding.includedToolbar.searchFirmName.getId()) {
                    isSearchingByOrgNumber = false;
                    searchView.setQueryHint(getString(R.string.company_search_name_hint));
                } else {
                    isSearchingByOrgNumber = true;
                    searchView.setQueryHint(getString(R.string.company_search_org_number_hint));
                }
                searchView.setQuery("",false);
            }
        });
    }

    @Override
    public void onItemClick(Company company) {

        companyListViewModel.upsert(company);

        if (isTwoPane) {
            inflateCompanyDetailsFragment(company, false);
        } else {
            Intent intent = new Intent(MainActivity.this, CompanyDetailActivity.class);
            intent.putExtra(CompanyDetailsFragment.ARG_COMPANY, company);

            this.startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        searchString = searchView.getQuery().toString();
        outState.putString(SEARCH_KEY, searchString);
        outState.putBoolean(SEARCH_BY_ORG_NUMBER_KEY, isSearchingByOrgNumber);
    }

    // This is only called when in two pane mode
    @Override
    public void navigateToParentCompany(Integer organisasjonsnummer) {
        if (organisasjonsnummer == null) {
            Toast.makeText(getApplicationContext(), R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show();
        }

        progressBarDetails.setVisibility(View.VISIBLE);
        companyDetailsViewModel.searchForCompanyWithOrgNumber(organisasjonsnummer);
    }

    @Override
    public void navigateToHomepage(String url) {
        Bundle arguments = new Bundle();
        arguments.putString(HomepageFragment.ARG_URL, url);
        HomepageFragment fragment = new HomepageFragment();
        fragment.setArguments(arguments);
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.company_details_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private boolean isValidOrganizationNumberQuery() {
        return isSearchingByOrgNumber && searchString.length() >= ORGANIZATION_NUMBER_LENGTH && TextUtils.isDigitsOnly(searchString);
    }

    private boolean isValidFirmNameQuery() {
        return !isSearchingByOrgNumber && searchString.length() >= MINIMUM_SEARCH_LENGTH;
    }

    private void checkForEmptyView(ActivityMainBinding binding) {
        if (!isValidFirmNameQuery() && !isValidOrganizationNumberQuery() && savedCompanies.isEmpty()) {
            binding.includedCompanyList.companyListWithHeader.setVisibility(View.GONE);
            binding.includedCompanyList.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.includedCompanyList.emptyView.setVisibility(View.GONE);
            binding.includedCompanyList.companyListWithHeader.setVisibility(View.VISIBLE);
        }
    }

    private void inflateCompanyDetailsFragment(Company company, boolean addToBackStack) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(CompanyDetailsFragment.ARG_COMPANY, company);
        CompanyDetailsFragment fragment = new CompanyDetailsFragment();
        fragment.setArguments(arguments);
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.company_details_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}