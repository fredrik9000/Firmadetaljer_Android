package com.github.fredrik9000.firmadetaljer_android.company_details

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.github.fredrik9000.firmadetaljer_android.LogUtils
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import kotlinx.coroutines.launch

/**
 * Since the app supports two different layouts, one for phones and one for tablets,
 * this base class is needed to avoid code duplication for company details behavior.
 */
abstract class CompanyDetailsNavigationActivity : AppCompatActivity() {

    protected lateinit var tag: String
    protected lateinit var progressBarDetails: ProgressBar
    private val companyDetailsViewModel: CompanyDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tag = this.javaClass.simpleName // Get the tag for the subclass
    }

    fun navigateToCompany(organisasjonsnummer: Int) {
        progressBarDetails.visibility = View.VISIBLE
        lifecycleScope.launch {
            val response = companyDetailsViewModel.searchForCompanyWithOrgNumber(organisasjonsnummer)
            progressBarDetails.visibility = View.GONE
            when (response) {
                is CompanyResponse.Success -> inflateCompanyDetailsFragment(
                    response.companyEntity.organisasjonsnummer,
                    true
                )
                is CompanyResponse.Error -> {
                    Toast.makeText(applicationContext, R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show()
                    LogUtils.debug(
                        tag = tag,
                        message = "handleCompanyNavigationResponse() called with response error = " + response.error
                    )
                }
            }
        }
    }

    fun navigateToHomepage(url: String) {
        this.supportFragmentManager.commit {
            replace(R.id.company_details_container, HomepageFragment().apply {
                this.arguments = Bundle().apply {
                    putString(HomepageFragment.ARG_URL, url)
                }
            })

            addToBackStack(null)
        }
    }

    protected fun inflateCompanyDetailsFragment(orgNumber: Int, addToBackStack: Boolean) {
        this.supportFragmentManager.commit {
            replace(R.id.company_details_container, CompanyDetailsFragment().apply {
                this.arguments = Bundle().also {
                    it.putInt(CompanyDetailsFragment.ARG_COMPANY_ORG_NUMBER, orgNumber)
                }
            })

            if (addToBackStack) {
                addToBackStack(null)
            }
        }
    }
}