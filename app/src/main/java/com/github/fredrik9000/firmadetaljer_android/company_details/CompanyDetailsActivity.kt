package com.github.fredrik9000.firmadetaljer_android.company_details

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityCompanyDetailsBinding
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse

class CompanyDetailsActivity : AppCompatActivity(), CompanyDetailsNavigation {
    private lateinit var companyDetailsViewModel: CompanyDetailsViewModel
    private lateinit var progressBarDetails: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_details)
        val binding = DataBindingUtil.setContentView<ActivityCompanyDetailsBinding>(this, R.layout.activity_company_details)
        progressBarDetails = binding.progressCompanyDetails
        setSupportActionBar(binding.includedToolbar.toolbarCompanyDetails)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        companyDetailsViewModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel::class.java)

        // savedInstanceState is non-null when there is fragment state saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape). In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it. For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity using a fragment transaction.
            inflateCompanyDetailsFragment(intent.getParcelableExtra(CompanyDetailsFragment.ARG_COMPANY) as Company, false)
        }
    }

    override fun handleCompanyNavigationResponse(response: CompanyResponse) {
        progressBarDetails.visibility = View.GONE
        response.company?.let {
            inflateCompanyDetailsFragment(it, true)
        } ?: run {
            Toast.makeText(applicationContext, R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "handleCompanyNavigationResponse() called with response error = " + response.error!!)
        }
    }

    override fun navigateToCompany(organisasjonsnummer: Int) {
        progressBarDetails.visibility = View.VISIBLE
        companyDetailsViewModel.searchForCompanyWithOrgNumber(this, organisasjonsnummer)
    }

    override fun navigateToHomepage(url: String) {
        val arguments = Bundle()
        arguments.putString(HomepageFragment.ARG_URL, url)
        val fragment = HomepageFragment()
        fragment.arguments = arguments
        this.supportFragmentManager.beginTransaction()
                .replace(R.id.company_details_container, fragment)
                .addToBackStack(null)
                .commit()
    }

    private fun inflateCompanyDetailsFragment(company: Company, addToBackStack: Boolean) {
        val arguments = Bundle()
        arguments.putParcelable(CompanyDetailsFragment.ARG_COMPANY, company)
        val fragment = CompanyDetailsFragment()
        fragment.arguments = arguments
        val transaction = this.supportFragmentManager.beginTransaction()
                .replace(R.id.company_details_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
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

    companion object {
        private const val TAG = "CompanyDetailsActivity"
    }
}
