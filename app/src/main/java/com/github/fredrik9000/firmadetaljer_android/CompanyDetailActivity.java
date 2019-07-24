package com.github.fredrik9000.firmadetaljer_android;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityCompanyDetailsBinding;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class CompanyDetailActivity extends AppCompatActivity implements ICompanyDetails {

    private CompanyDetailsViewModel companyDetailsViewModel;
    private static final String TAG = "CompanyDetailActivity";
    private ProgressBar progressBarDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
        ActivityCompanyDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_company_details);
        progressBarDetails = binding.progressCompanyDetails;
        Toolbar toolbar = binding.includedToolbar.toolbarCompanyDetails;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        companyDetailsViewModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel.class);

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

        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape). In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it. For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            inflateCompanyDetailsFragment((Company) getIntent().getParcelableExtra(CompanyDetailsFragment.ARG_COMPANY), false);
        }
    }

    @Override
    public void navigateToParentCompany(Integer organisasjonsnummer) {
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

    // When there's nesting of company detail fragments, navigate back to the previous instead of the search results
    // This will only happen when one has navigated to a parent company
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
