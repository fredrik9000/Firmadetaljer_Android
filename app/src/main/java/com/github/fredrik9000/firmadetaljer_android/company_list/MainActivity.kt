package com.github.fredrik9000.firmadetaljer_android.company_list

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fredrik9000.firmadetaljer_android.LogUtils
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.company_details.*
import com.github.fredrik9000.firmadetaljer_android.databinding.ActivityMainBinding
import com.github.fredrik9000.firmadetaljer_android.repository.rest.CompanyResponse
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CompanyListAdapter.OnItemClickListener, CompanyDetailsNavigation, SearchFilterDialogFragment.OnSearchFilterDialogFragmentInteractionListener {

    private lateinit var binding: ActivityMainBinding

    private val companyListViewModel: CompanyListViewModel by viewModels()
    private val companyDetailsViewModel: CompanyDetailsViewModel by viewModels()

    private lateinit var progressBarList: ProgressBar
    private var progressBarDetails: ProgressBar? = null // Not present on phones
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbarMenu: ActionMenuView

    private lateinit var adapterSearchList: CompanyListAdapter
    private lateinit var adapterSavedList: CompanyListAdapter

    private var isTwoPane: Boolean = false
    private var searchDebounceTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setTitle(R.string.main_activity_title)
        progressBarList = binding.progressCompanyList
        progressBarDetails = binding.progressCompanyDetails
        val toolbar = binding.includedToolbar.toolbarMain
        setSupportActionBar(toolbar)

        toolbarMenu = binding.includedToolbar.toolbarMenu
        toolbarMenu.setOnMenuItemClickListener { onOptionsItemSelected(it) }

        if (findViewById<View>(R.id.company_details_container) != null) {
            // The detail container view will be present only in the large-screen layouts (res/values-w900dp).
            // If this view is present, then the activity should be in two-pane mode.
            isTwoPane = true
        }

        setupRecyclerView(binding)

        adapterSavedList = CompanyListAdapter(this, this, ArrayList(), isTwoPane, true)
        adapterSearchList = CompanyListAdapter(this, this, ArrayList(), isTwoPane, false)

        savedInstanceState?.let {
            companyListViewModel.searchString = it.getString(SEARCH_KEY) ?: ""
            companyListViewModel.searchMode = it.getSerializable(SEARCH_MODE_KEY) as SearchMode
            companyListViewModel.selectedNumberOfEmployeesFilter = it.getSerializable(EMPLOYEES_FILTER_KEY) as NumberOfEmployeesFilter
        }

        // In case the activity gets recreated (for example by rotating the device)
        // the correct adapter should be set.
        if (companyListViewModel.isSearchingWithValidInput) {
            binding.includedCompanyList.companyListHeader.text = buildSearchResultHeaderText()
            recyclerView.adapter = adapterSearchList
        } else {
            binding.includedCompanyList.companyListHeader.setText(R.string.viewed_companies_header)
            recyclerView.adapter = adapterSavedList
        }

        setupSearchView(toolbar, binding)
        setupSearchResultObserver(binding)
        setupSavedCompaniesObserver(binding)

        // If the following is true that means both the Activity and ViewModel were destroyed,
        // and while having an active search. Therefore we need to do the search again.
        if (companyListViewModel.searchResultLiveData.value?.companies == null &&
                companyListViewModel.searchResultLiveData.value?.peekError() == null &&
                companyListViewModel.isSearchingWithValidInput) {
            progressBarList.visibility = View.VISIBLE
            companyListViewModel.searchOnSelectedSearchMode(companyListViewModel.searchString)
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

    // Saved companies is shown whenever the user isn't searching
    private fun setupSavedCompaniesObserver(binding: ActivityMainBinding) {
        companyListViewModel.savedCompaniesLiveData.observe(this, Observer { companyList ->
            adapterSavedList.update(companyList)
            showOrHideOnboardingView(binding)
        })
    }

    // Handles search results and updates the list
    private fun setupSearchResultObserver(binding: ActivityMainBinding) {
        companyListViewModel.searchResultLiveData.observe(this, Observer { companyListResponse ->
            progressBarList.visibility = View.GONE
            if (companyListResponse.companies != null) {
                adapterSearchList.update(companyListResponse.companies!!)
            } else {
                // If there was an error searching (or organization number not found), update with empty results.
                adapterSearchList.update(ArrayList())

                // Only show a toast message the first time the error is handled (otherwise a rotation would show it again).
                companyListResponse.getErrorIfNotHandled()?.let { error ->
                    // When an organization number is not found, the service can return either 400 or 404.
                    val toastMessage =
                            if (companyListViewModel.searchMode == SearchMode.ORGANIZATION_NUMBER && error is HttpException && ((error.code() == 400) || (error.code() == 404))) {
                                resources.getString(R.string.company_not_found)
                            } else {
                                resources.getString(R.string.search_request_error_message)
                            }
                    Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_SHORT).show()
                    LogUtils.debug(TAG, "onChanged() called with companyListResponse error = $error")
                }
            }
            binding.includedCompanyList.onboardingView.visibility = View.GONE
            binding.includedCompanyList.companyListWithHeader.visibility = View.VISIBLE
        })
    }

    private fun setupSearchView(toolbar: Toolbar, binding: ActivityMainBinding) {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = toolbar.findViewById(R.id.action_search)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        // Set default values for search view
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

        // Search view listerners
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (companyListViewModel.isSearchingWithValidInput) {
                    progressBarList.visibility = View.VISIBLE
                    companyListViewModel.searchOnSelectedSearchMode(query)
                    searchView.clearFocus() // clearFocus() closes the full screen search view for phones in landscape mode.
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                companyListViewModel.searchString = newText
                showOrHideOnboardingView(binding)

                // Check the current state and set the correct adapter
                if (companyListViewModel.isSearchingWithValidInput && recyclerView.adapter === adapterSavedList) {
                    binding.includedCompanyList.companyListHeader.text = buildSearchResultHeaderText()
                    recyclerView.adapter = adapterSearchList
                } else if (!companyListViewModel.isSearchingWithValidInput && recyclerView.adapter === adapterSearchList) {
                    binding.includedCompanyList.companyListHeader.setText(R.string.viewed_companies_header)
                    recyclerView.adapter = adapterSavedList
                }

                // Since organization number should be 9, it will be trimmed whenever the user tries to type more
                if (companyListViewModel.searchMode == SearchMode.ORGANIZATION_NUMBER && companyListViewModel.searchStringExceedsOrganizationNumberLength) {
                    companyListViewModel.trimSearchStringByOrganizationNumberLength()
                    searchView.setQuery(companyListViewModel.searchString, false)
                } else if (companyListViewModel.isSearchingWithValidInput) {
                    debounceSearch()
                    return true
                }

                return false
            }

            fun debounceSearch() {
                searchDebounceTimer?.cancel()
                val tempSearchMode = companyListViewModel.searchMode
                val tempSearchString = companyListViewModel.searchString

                searchDebounceTimer = Timer().apply {
                    schedule(timerTask {
                        // If the search mode or text was updated during the delay don´t perform search
                        if (tempSearchMode != companyListViewModel.searchMode || tempSearchString != companyListViewModel.searchString) {
                            return@timerTask
                        }

                        runOnUiThread {
                            progressBarList.visibility = View.VISIBLE
                            companyListViewModel.searchOnSelectedSearchMode(companyListViewModel.searchString)
                        }
                    }, DEBOUNCE_TIME_IN_MILLISECONDS)
                }
            }
        })

        // Search mode buttons listener
        binding.includedToolbar.searchModeToggle.setOnCheckedChangeListener { radioGroup, checkedId ->
            if (!radioGroup.findViewById<AppCompatRadioButton>(checkedId).isPressed) {
                // onCheckedChanged was triggered by the system, for example when rotating.
                return@setOnCheckedChangeListener
            }

            if (checkedId == binding.includedToolbar.searchFirmName.id) {
                companyListViewModel.searchMode = SearchMode.FIRM_NAME
                searchView.queryHint = getString(R.string.company_search_name_hint)
            } else {
                companyListViewModel.searchMode = SearchMode.ORGANIZATION_NUMBER
                searchView.queryHint = getString(R.string.company_search_org_number_hint)
            }

            searchView.setQuery("", false)
        }
    }

    private fun buildSearchResultHeaderText(): String {
        return when (companyListViewModel.selectedNumberOfEmployeesFilter) {
            NumberOfEmployeesFilter.ALL_EMPLOYEES -> resources.getString(R.string.search_results_header)
            NumberOfEmployeesFilter.LESS_THAN_6 -> resources.getString(R.string.search_results_header) + " (" + resources.getString(R.string.filter_employees_less_than_or_equal_to_5) + ")"
            NumberOfEmployeesFilter.BETWEEN_5_AND_201 -> resources.getString(R.string.search_results_header) + " (" + resources.getString(R.string.filter_employees_between_5_and_201) + ")"
            NumberOfEmployeesFilter.MORE_THAN_200 -> resources.getString(R.string.search_results_header) + " (" + resources.getString(R.string.filter_employees_more_than_200) + ")"
        }
    }

    override fun onItemClick(company: Company, isViewedCompaniesList: Boolean) {
        if (!isViewedCompaniesList) {
            companyListViewModel.upsert(company) // Save company so that it will be shown in the viewed companies list
        }

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
        outState.putSerializable(EMPLOYEES_FILTER_KEY, companyListViewModel.selectedNumberOfEmployeesFilter)
    }

    override fun handleCompanyNavigationResponse(response: CompanyResponse) {
        progressBarDetails!!.visibility = View.GONE
        response.company?.let {
            inflateCompanyDetailsFragment(it, true)
        } ?: run {
            Toast.makeText(applicationContext, R.string.company_detail_not_loaded, Toast.LENGTH_SHORT).show()
            LogUtils.debug(TAG, "handleCompanyNavigationResponse() called with response error = " + response.error!!)
        }
    }

    override fun navigateToCompany(organisasjonsnummer: Int) {
        progressBarDetails!!.visibility = View.VISIBLE
        companyDetailsViewModel.searchForCompanyWithOrgNumber(this, organisasjonsnummer)
    }

    override fun navigateToHomepage(url: String) {
        val arguments = Bundle()
        arguments.putString(HomepageFragment.ARG_URL, url)
        val fragment = HomepageFragment().apply { this.arguments = arguments }
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
        when (item.itemId) {
            R.id.delete_search_history -> {
                companyListViewModel.deleteAllCompanies()
                return true
            }
            R.id.modify_search_filters -> {
                val searchFilterDialogFragmentBundle = Bundle()
                searchFilterDialogFragmentBundle.putSerializable(SearchFilterDialogFragment.ARGUMENT_FILTER_SELECTED, companyListViewModel.selectedNumberOfEmployeesFilter)
                val searchFilterDialogFragment = SearchFilterDialogFragment()
                searchFilterDialogFragment.arguments = searchFilterDialogFragmentBundle
                searchFilterDialogFragment.show(supportFragmentManager, "SearchFilterDialogFragment")
                return true
            }
            R.id.toggle_night_mode -> {
                val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else { // Assume light theme
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        // Fix for memory leak in the Android framework that happens on Android 10:
        // https://issuetracker.google.com/issues/139738913
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && isTaskRoot) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        searchDebounceTimer?.cancel()
        super.onDestroy()
    }

    override fun onSearchFilterDialogFragmentInteraction(filter: NumberOfEmployeesFilter) {
        if (filter != companyListViewModel.selectedNumberOfEmployeesFilter) {
            companyListViewModel.selectedNumberOfEmployeesFilter = filter

            // Redo search when changing filter
            if (companyListViewModel.isSearchingWithValidInput) {
                binding.includedCompanyList.companyListHeader.text = buildSearchResultHeaderText()
                progressBarList.visibility = View.VISIBLE
                companyListViewModel.searchOnSelectedSearchMode(companyListViewModel.searchString)
            }
        }
    }

    private fun showOrHideOnboardingView(binding: ActivityMainBinding) {
        if (!companyListViewModel.isSearchingWithValidInput && companyListViewModel.savedCompaniesLiveData.value.isNullOrEmpty()) {
            binding.includedCompanyList.companyListWithHeader.visibility = View.GONE
            binding.includedCompanyList.onboardingView.visibility = View.VISIBLE
        } else {
            binding.includedCompanyList.onboardingView.visibility = View.GONE
            binding.includedCompanyList.companyListWithHeader.visibility = View.VISIBLE
        }
    }

    private fun inflateCompanyDetailsFragment(company: Company, addToBackStack: Boolean) {
        val arguments = Bundle()
        arguments.putParcelable(CompanyDetailsFragment.ARG_COMPANY, company)
        val fragment = CompanyDetailsFragment().apply { this.arguments = arguments }
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
        const val EMPLOYEES_FILTER_KEY = "EMPLOYEES_FILTER"
        const val DEBOUNCE_TIME_IN_MILLISECONDS = 500L
    }

}