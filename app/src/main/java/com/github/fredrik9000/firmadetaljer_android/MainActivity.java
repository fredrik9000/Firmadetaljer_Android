package com.github.fredrik9000.firmadetaljer_android;

import android.app.SearchManager;
import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle(R.string.main_activity_title);
        progressBar = binding.progress;
        companyListViewModel = ViewModelProviders.of(this).get(CompanyListViewModel.class);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.company_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    progressBar.setVisibility(View.VISIBLE);
                    companyListViewModel.searchForCompaniesThatStartsWith(query);
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

    }
}
