package com.github.fredrik9000.firmadetaljer_android.company_list

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fredrik9000.firmadetaljer_android.company_details.HomepageFragment
import com.github.fredrik9000.firmadetaljer_android.interfaces.ICompanyDetails
import com.github.fredrik9000.firmadetaljer_android.interfaces.ICompanyResponseHandler
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsActivity
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsFragment
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsViewModel

import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityMainBinding
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

import java.util.ArrayList

import retrofit2.HttpException

class MainActivity : AppCompatActivity(), CompanyListAdapter.OnItemClickListener, ICompanyDetails, ICompanyResponseHandler {
    private lateinit var progressBarList: ProgressBar
    private var progressBarDetails: ProgressBar? = null // Not present on phones
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapterSearchList: CompanyListAdapter
    private lateinit var adapterSavedList: CompanyListAdapter

    private lateinit var companyListViewModel: CompanyListViewModel
    private lateinit var companyDetailsViewModel: CompanyDetailsViewModel

    private var savedCompanies: List<Company> = ArrayList()
    private var isSearchingByOrgNumber = false
    private var isTwoPane: Boolean = false
    private var searchString: String = ""

    private val isValidOrganizationNumberQuery: Boolean
        get() = isSearchingByOrgNumber && searchString.length >= ORGANIZATION_NUMBER_LENGTH && TextUtils.isDigitsOnly(searchString)

    private val isValidFirmNameQuery: Boolean
        get() = !isSearchingByOrgNumber && searchString.length >= MINIMUM_FIRM_NAME_SEARCH_LENGTH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setTitle(R.string.main_activity_title)
        progressBarList = binding.progressCompanyList
        progressBarDetails = binding.progressCompanyDetails
        val toolbar = binding.includedToolbar.toolbarMain
        setSupportActionBar(toolbar)

        companyListViewModel = ViewModelProviders.of(this).get(CompanyListViewModel::class.java)

        if (findViewById<View>(R.id.company_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            isTwoPane = true
            companyDetailsViewModel = ViewModelProviders.of(this).get(CompanyDetailsViewModel::class.java)
        }

        setupRecyclerView(binding)

        adapterSavedList = CompanyListAdapter(this@MainActivity, ArrayList())
        adapterSearchList = CompanyListAdapter(this@MainActivity, ArrayList())

        if (savedInstanceState != null) {
            searchString = savedInstanceState.getString(SEARCH_KEY) ?: ""
            isSearchingByOrgNumber = savedInstanceState.getBoolean(SEARCH_BY_ORG_NUMBER_KEY)
        }

        // In case the activity gets recreated (for example by rotating the device)
        // the correct adapter should be set.
        if (isValidFirmNameQuery || isValidOrganizationNumberQuery) {
            binding.includedCompanyList.companyListHeader.setText(R.string.search_results_header)
            recyclerView.adapter = adapterSearchList
        } else {
            binding.includedCompanyList.companyListHeader.setText(R.string.last_viewed_companies_header)
            recyclerView.adapter = adapterSavedList
        }

        setupSearchView(toolbar, binding)

        setupSearchResultObserver(binding)
        setupSavedCompaniesObserver(binding)
    }

    private fun setupRecyclerView(binding: ActivityMainBinding) {
        recyclerView = binding.includedCompanyList.companyList
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context,
                layoutManager.orientation))
    }

    // Used for when updating saved companies (happens when viewing one)
    // Saved companies is shown whenever the user isn't searching
    private fun setupSavedCompaniesObserver(binding: ActivityMainBinding) {
        companyListViewModel.savedCompanyList.observe(this, Observer { companyList ->
            savedCompanies = companyList
            adapterSavedList.update(companyList)
            checkForEmptyView(binding)
        })
    }

    // Handles search results and updates the list
    private fun setupSearchResultObserver(binding: ActivityMainBinding) {
        companyListViewModel.searchResultCompanyList.observe(this, Observer { companyListResponse ->
            progressBarList.visibility = View.GONE
            if (companyListResponse.companies != null) {
                adapterSearchList.update(companyListResponse.companies!!)
            } else {
                // If there was an error searching (or organization number not found), update with empty results.
                adapterSearchList.update(ArrayList())

                // When an organization number is not found, the service can return either 400 or 404.
                val toastMessage =
                        if (isSearchingByOrgNumber && companyListResponse.error is HttpException
                                && (((companyListResponse.error as HttpException).code() == 400)
                                        || ((companyListResponse.error as HttpException).code() == 404))) {
                    resources.getString(R.string.company_not_found)
                } else {
                    resources.getString(R.string.search_request_error_message)
                }
                Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "onChanged() called with companyListResponse error = " + companyListResponse.error!!)
            }
            binding.includedCompanyList.emptyView.visibility = View.GONE
            binding.includedCompanyList.companyListWithHeader.visibility = View.VISIBLE
        })
    }

    private fun setupSearchView(toolbar: Toolbar, binding: ActivityMainBinding) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = toolbar.findViewById(R.id.action_search)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        if (isSearchingByOrgNumber) {
            searchView.queryHint = getString(R.string.company_search_org_number_hint)
        } else {
            searchView.queryHint = getString(R.string.company_search_name_hint)
        }

        if (searchString.isNotEmpty()) {
            searchView.isIconified = false
            searchView.setQuery(searchString, true)
            searchView.clearFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (isValidFirmNameQuery) {
                    progressBarList.visibility = View.VISIBLE
                    companyListViewModel.searchForCompaniesThatStartsWith(query)
                    // clearFocus() closes the full screen search view for phones in landscape mode.
                    searchView.clearFocus()
                    return true
                } else if (isValidOrganizationNumberQuery) {
                    progressBarList.visibility = View.VISIBLE
                    companyListViewModel.searchForCompaniesWithOrgNumber(Integer.parseInt(query))
                    searchView.clearFocus()
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchString = newText
                checkForEmptyView(binding)

                // Check the current state and set the correct adapter
                if ((isValidFirmNameQuery || isValidOrganizationNumberQuery) && recyclerView.adapter === adapterSavedList) {
                    binding.includedCompanyList.companyListHeader.setText(R.string.search_results_header)
                    recyclerView.adapter = adapterSearchList
                } else if (!isValidFirmNameQuery && !isValidOrganizationNumberQuery && recyclerView.adapter === adapterSearchList) {
                    binding.includedCompanyList.companyListHeader.setText(R.string.last_viewed_companies_header)
                    recyclerView.adapter = adapterSavedList
                }

                if (isSearchingByOrgNumber) {
                    // Since organization number should be 9, it will be trimmed whenever the user tries to type more
                    if (newText.length > ORGANIZATION_NUMBER_LENGTH) {
                        searchView.setQuery(newText.substring(0, ORGANIZATION_NUMBER_LENGTH), false)
                    } else if (isValidOrganizationNumberQuery) {
                        progressBarList.visibility = View.VISIBLE
                        companyListViewModel.searchForCompaniesWithOrgNumber(Integer.parseInt(newText))
                    }
                    return true
                } else if (newText.length >= MINIMUM_FIRM_NAME_SEARCH_LENGTH) {
                    progressBarList.visibility = View.VISIBLE
                    companyListViewModel.searchForCompaniesThatStartsWith(newText)
                    return true
                }
                return false
            }
        })

        binding.includedToolbar.toggle.setOnCheckedChangeListener { _, i ->
            if (i == binding.includedToolbar.searchFirmName.id) {
                isSearchingByOrgNumber = false
                searchView.queryHint = getString(R.string.company_search_name_hint)
            } else {
                isSearchingByOrgNumber = true
                searchView.queryHint = getString(R.string.company_search_org_number_hint)
            }
            searchView.setQuery("", false)
        }
    }

    override fun onItemClick(company: Company) {

        // Save company so that it will be shown in the last viewed companies list
        companyListViewModel.upsert(company)

        if (isTwoPane) {
            inflateCompanyDetailsFragment(company, false)
        } else {
            val intent = Intent(this@MainActivity, CompanyDetailsActivity::class.java)
            intent.putExtra(CompanyDetailsFragment.ARG_COMPANY, company)

            this.startActivity(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchString = searchView.query.toString()
        outState.putString(SEARCH_KEY, searchString)
        outState.putBoolean(SEARCH_BY_ORG_NUMBER_KEY, isSearchingByOrgNumber)
    }

    override fun handleResponse(response: CompanyResponse) {
        progressBarDetails!!.visibility = View.GONE
        if (response.company != null) {
            inflateCompanyDetailsFragment(response.company!!, true)
        } else {
            Toast.makeText(applicationContext, R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "handleResponse() called with companyResponse error = " + response.error!!)
        }
    }

    // This is only called when in two pane mode
    override fun navigateToParentCompany(organisasjonsnummer: Int) {
        progressBarDetails!!.visibility = View.VISIBLE
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

    private fun checkForEmptyView(binding: ActivityMainBinding) {
        if (!isValidFirmNameQuery && !isValidOrganizationNumberQuery && savedCompanies.isEmpty()) {
            binding.includedCompanyList.companyListWithHeader.visibility = View.GONE
            binding.includedCompanyList.emptyView.visibility = View.VISIBLE
        } else {
            binding.includedCompanyList.emptyView.visibility = View.GONE
            binding.includedCompanyList.companyListWithHeader.visibility = View.VISIBLE
        }
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

    companion object {
        private const val TAG = "MainActivity"
        private const val SEARCH_KEY = "SEARCH"
        private const val SEARCH_BY_ORG_NUMBER_KEY = "SEARCH_BY_ORG_NUMBER"
        private const val MINIMUM_FIRM_NAME_SEARCH_LENGTH = 2
        private const val ORGANIZATION_NUMBER_LENGTH = 9
    }
}