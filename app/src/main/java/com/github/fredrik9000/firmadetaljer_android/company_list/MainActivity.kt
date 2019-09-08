package com.github.fredrik9000.firmadetaljer_android.company_list

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fredrik9000.firmadetaljer_android.company_details.HomepageFragment
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsNavigation
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsActivity
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsFragment
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsViewModel

import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityMainBinding
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

import retrofit2.HttpException
import java.util.Timer
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity(), CompanyListAdapter.OnItemClickListener, CompanyDetailsNavigation {
    private lateinit var progressBarList: ProgressBar
    private var progressBarDetails: ProgressBar? = null // Not present on phones
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapterSearchList: CompanyListAdapter
    private lateinit var adapterSavedList: CompanyListAdapter

    private lateinit var companyListViewModel: CompanyListViewModel
    private lateinit var companyDetailsViewModel: CompanyDetailsViewModel

    private var isTwoPane: Boolean = false

    private lateinit var toolbarMenu : ActionMenuView

    var searchDebounceTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setTitle(R.string.main_activity_title)
        progressBarList = binding.progressCompanyList
        progressBarDetails = binding.progressCompanyDetails
        val toolbar = binding.includedToolbar.toolbarMain
        setSupportActionBar(toolbar)

        toolbarMenu = binding.includedToolbar.toolbarMenu
        toolbarMenu.setOnMenuItemClickListener { onOptionsItemSelected(it) }

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
            companyListViewModel.searchString = savedInstanceState.getString(SEARCH_KEY) ?: ""
            companyListViewModel.searchMode = savedInstanceState.getSerializable(SEARCH_MODE_KEY) as SearchMode
        }

        // In case the activity gets recreated (for example by rotating the device)
        // the correct adapter should be set.
        if (companyListViewModel.isSearchingWithValidInput) {
            binding.includedCompanyList.companyListHeader.setText(R.string.search_results_header)
            recyclerView.adapter = adapterSearchList
        } else {
            binding.includedCompanyList.companyListHeader.setText(R.string.last_viewed_companies_header)
            recyclerView.adapter = adapterSavedList
        }

        setupSearchView(toolbar, binding)
        setupSearchResultObserver(binding)
        setupSavedCompaniesObserver(binding)

        // If the following is true that means both the Activity and ViewModel were destroyed,
        // and while having an active search. Therefore we need to do the search again.
        if (companyListViewModel.searchResultCompanyList.value?.companies == null && companyListViewModel.isSearchingWithValidInput) {
            progressBarList.visibility = View.VISIBLE

            if (companyListViewModel.searchMode == SearchMode.ORGANIZATION_NUMBER) {
                companyListViewModel.searchForCompaniesWithOrgNumber(Integer.parseInt(companyListViewModel.searchString))
            } else {
                companyListViewModel.searchForCompaniesThatStartsWith(companyListViewModel.searchString)
            }
        }
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
            adapterSavedList.updateWithAnimation(companyList)
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
                        if (companyListViewModel.searchMode == SearchMode.ORGANIZATION_NUMBER && companyListResponse.error is HttpException
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

        if (companyListViewModel.searchMode == SearchMode.ORGANIZATION_NUMBER) {
            searchView.queryHint = getString(R.string.company_search_org_number_hint)
        } else {
            searchView.queryHint = getString(R.string.company_search_name_hint)
        }

        if (companyListViewModel.searchString.isNotEmpty()) {
            searchView.isIconified = false
            searchView.setQuery(companyListViewModel.searchString, true)
            searchView.clearFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (companyListViewModel.isSearchingWithValidFirmName) {
                    progressBarList.visibility = View.VISIBLE
                    companyListViewModel.searchForCompaniesThatStartsWith(query)
                    // clearFocus() closes the full screen search view for phones in landscape mode.
                    searchView.clearFocus()
                    return true
                } else if (companyListViewModel.isSearchingWithValidOrganizationNumber) {
                    progressBarList.visibility = View.VISIBLE
                    companyListViewModel.searchForCompaniesWithOrgNumber(Integer.parseInt(query))
                    searchView.clearFocus()
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                companyListViewModel.searchString = newText
                checkForEmptyView(binding)

                // Check the current state and set the correct adapter
                if (companyListViewModel.isSearchingWithValidInput && recyclerView.adapter === adapterSavedList) {
                    binding.includedCompanyList.companyListHeader.setText(R.string.search_results_header)
                    recyclerView.adapter = adapterSearchList
                } else if (!companyListViewModel.isSearchingWithValidInput && recyclerView.adapter === adapterSearchList) {
                    binding.includedCompanyList.companyListHeader.setText(R.string.last_viewed_companies_header)
                    recyclerView.adapter = adapterSavedList
                }

                // Since organization number should be 9, it will be trimmed whenever the user tries to type more
                if (companyListViewModel.searchMode == SearchMode.ORGANIZATION_NUMBER && companyListViewModel.organizationNumberSearchHasTooManyCharacters) {
                    companyListViewModel.searchString = companyListViewModel.trimmedOrganizationNumber
                    searchView.setQuery(companyListViewModel.searchString, false)
                } else if (companyListViewModel.isSearchingWithValidFirmName || companyListViewModel.isSearchingWithValidOrganizationNumber) {
                    debounceSearch()
                    return true
                }

                return false
            }

            fun debounceSearch() {
                searchDebounceTimer?.cancel()
                searchDebounceTimer = Timer().apply {
                    schedule(timerTask {
                        runOnUiThread {
                            progressBarList.visibility = View.VISIBLE
                            if (companyListViewModel.searchMode == SearchMode.FIRM_NAME) {
                                companyListViewModel.searchForCompaniesThatStartsWith(companyListViewModel.searchString)
                            } else {
                                companyListViewModel.searchForCompaniesWithOrgNumber(Integer.parseInt(companyListViewModel.searchString))
                            }}
                    }, DEBOUNCE_TIME_IN_MILLISECONDS)
                }
            }
        })

        binding.includedToolbar.searchModeToggle.setOnCheckedChangeListener { _, i ->
            if (i == binding.includedToolbar.searchFirmName.id) {
                companyListViewModel.searchMode = SearchMode.FIRM_NAME
                searchView.queryHint = getString(R.string.company_search_name_hint)
            } else {
                companyListViewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
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
        outState.putString(SEARCH_KEY, companyListViewModel.searchString)
        outState.putSerializable(SEARCH_MODE_KEY, companyListViewModel.searchMode)
    }

    override fun handleCompanyNavigationResponse(response: CompanyResponse) {
        progressBarDetails!!.visibility = View.GONE
        if (response.company != null) {
            inflateCompanyDetailsFragment(response.company!!, true)
        } else {
            Toast.makeText(applicationContext, R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "handleResponse() called with companyResponse error = " + response.error!!)
        }
    }

    // This is only called when in two pane mode
    override fun navigateToCompany(organisasjonsnummer: Int) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, toolbarMenu.menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_search_history) {
            companyListViewModel.deleteAllCompanies()
            return true
        } else if (item.itemId == R.id.toggle_night_mode) {
            val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Configuration.UI_MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> // Assumes light theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        searchDebounceTimer?.cancel()
        super.onDestroy()
    }

    private fun checkForEmptyView(binding: ActivityMainBinding) {
        if (!companyListViewModel.isSearchingWithValidInput && companyListViewModel.savedCompanyList.value.isNullOrEmpty()) {
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

    private companion object {
        const val TAG = "MainActivity"
        const val SEARCH_KEY = "SEARCH"
        const val SEARCH_MODE_KEY = "SEARCH_MODE"
        const val DEBOUNCE_TIME_IN_MILLISECONDS = 500L
    }
}