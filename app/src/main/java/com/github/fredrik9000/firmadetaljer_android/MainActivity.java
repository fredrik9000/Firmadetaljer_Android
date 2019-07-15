package com.github.fredrik9000.firmadetaljer_android;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityMainBinding;
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CompanyAdapter.OnItemClickListener {

    private ProgressBar progressBar;
    private CompanyListViewModel companyListViewModel;
    private CompanyAdapter adapter;
    private static final String TAG = "MainActivity";
    private boolean isTwoPane;
    private static final String SEARCH_KEY = "search";
    private SearchView searchView;
    private String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle(R.string.main_activity_title);
        progressBar = binding.progress;
        companyListViewModel = ViewModelProviders.of(this).get(CompanyListViewModel.class);

        if (findViewById(R.id.company_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            isTwoPane = true;
        }

        RecyclerView recyclerView = binding.companylist;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CompanyAdapter(MainActivity.this, new ArrayList<Company>());
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        companyListViewModel.getCompanyList().observe(this, new Observer<CompanyResponse>() {
            @Override
            public void onChanged(@Nullable CompanyResponse companyResponse) {
                progressBar.setVisibility(View.GONE);
                if (companyResponse.getCompanies() != null) {
                    adapter.update(companyResponse.getCompanies());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.search_request_error_message, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onChanged() called with companyResponse error = " + companyResponse.getError());
                }
            }
        });

        if (savedInstanceState != null) {
            searchString = savedInstanceState.getString(SEARCH_KEY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.company_search_hint));

        if (searchString != null && !searchString.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(searchString, true);
            searchView.clearFocus();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    progressBar.setVisibility(View.VISIBLE);
                    companyListViewModel.searchForCompaniesThatStartsWith(query);
                    searchView.clearFocus(); // This closes the full screen search view for phones in landscape mode.
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    progressBar.setVisibility(View.VISIBLE);
                    companyListViewModel.searchForCompaniesThatStartsWith(newText);
                    return true;
                }
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false, false);

        return true;
    }

    @Override
    public void onItemClick(Company company) {
        if (isTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CompanyDetailFragment.ARG_COMPANY, company);
            CompanyDetailFragment fragment = new CompanyDetailFragment();
            fragment.setArguments(arguments);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.company_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(MainActivity.this, CompanyDetailActivity.class);
            intent.putExtra(CompanyDetailFragment.ARG_COMPANY, company);

            this.startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        searchString = searchView.getQuery().toString();
        outState.putString(SEARCH_KEY, searchString);
    }
}
