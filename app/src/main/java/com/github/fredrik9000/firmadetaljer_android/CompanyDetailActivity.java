package com.github.fredrik9000.firmadetaljer_android;

import android.os.Bundle;

import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityCompanyDetailBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class CompanyDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);
        ActivityCompanyDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_company_detail);

        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape). In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it. For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(CompanyDetailFragment.ARG_COMPANY,
                    getIntent().getParcelableExtra(CompanyDetailFragment.ARG_COMPANY));
            CompanyDetailFragment fragment = new CompanyDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.company_detail_container, fragment)
                    .commit();
        }
    }

}
