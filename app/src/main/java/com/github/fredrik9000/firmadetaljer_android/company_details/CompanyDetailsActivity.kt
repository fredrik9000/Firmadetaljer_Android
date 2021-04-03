package com.github.fredrik9000.firmadetaljer_android.company_details

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.github.fredrik9000.firmadetaljer_android.BuildConfig
import com.github.fredrik9000.firmadetaljer_android.LogUtils
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityCompanyDetailsBinding
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompanyDetailsActivity : AppCompatActivity(), CompanyDetailsNavigation {

    private val companyDetailsViewModel: CompanyDetailsViewModel by viewModels()
    private lateinit var progressBarDetails: ProgressBar

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
            inflateCompanyDetailsFragment(intent.getParcelableExtra<Company>(CompanyDetailsFragment.ARG_COMPANY) as Company, false)
        }
    }

    override fun handleCompanyNavigationResponse(response: CompanyResponse) {
        progressBarDetails.visibility = View.GONE
        when (response) {
            is CompanyResponse.Success -> inflateCompanyDetailsFragment(response.company, true)
            is CompanyResponse.Error -> {
                Toast.makeText(applicationContext, R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show()
                if (BuildConfig.DEBUG) {
                    LogUtils.debug(TAG, "handleCompanyNavigationResponse() called with response error = " + response.error)
                }
            }
        }
    }

    override fun navigateToCompany(organisasjonsnummer: Int) {
        progressBarDetails.visibility = View.VISIBLE
        companyDetailsViewModel.searchForCompanyWithOrgNumber(this, organisasjonsnummer)
    }

    override fun navigateToHomepage(url: String) {
        this.supportFragmentManager.commit {
            replace(R.id.company_details_container, HomepageFragment().apply {
                this.arguments = Bundle().apply {
                    putString(HomepageFragment.ARG_URL, url)
                }
            })

            addToBackStack(null)
        }
    }

    private fun inflateCompanyDetailsFragment(company: Company, addToBackStack: Boolean) {
        this.supportFragmentManager.commit {
            replace(R.id.company_details_container, CompanyDetailsFragment().apply {
                this.arguments = Bundle().also {
                    it.putParcelable(CompanyDetailsFragment.ARG_COMPANY, company)
                }
            })

            if (addToBackStack) {
                addToBackStack(null)
            }
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

    companion object {
        private const val TAG = "CompanyDetailsActivity"
    }
}
