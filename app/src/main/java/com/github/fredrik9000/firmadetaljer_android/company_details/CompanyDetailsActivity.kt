package com.github.fredrik9000.firmadetaljer_android.company_details

import android.os.Bundle
import android.view.MenuItem
import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityCompanyDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompanyDetailsActivity : CompanyDetailsNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCompanyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBarDetails = binding.progressCompanyDetails
        setSupportActionBar(binding.includedToolbar.toolbarCompanyDetails)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape). In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it. For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            inflateCompanyDetailsFragment(orgNumber = intent.getIntExtra(CompanyDetailsFragment.ARG_COMPANY_ORG_NUMBER, -1), addToBackStack = false)
        }
    }

    // When there's nesting of company detail fragments, navigate back to the previous company instead of the search results
    // This will only happen when one has navigated to a parent company
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val fm = supportFragmentManager
            if (fm.backStackEntryCount > 0) {
                fm.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}