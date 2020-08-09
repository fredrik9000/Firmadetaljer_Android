package com.github.fredrik9000.firmadetaljer_android.company_list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import com.github.fredrik9000.firmadetaljer_android.R

class SearchFilterDialogFragment : DialogFragment() {

    private var listener: OnSearchFilterDialogFragmentInteractionListener? = null
    private lateinit var selectedFilter: NumberOfEmployeesFilter

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        selectedFilter = requireArguments().getSerializable(ARGUMENT_FILTER_SELECTED) as NumberOfEmployeesFilter

        val selectedFilterId = when (selectedFilter) {
            NumberOfEmployeesFilter.LESS_THAN_6 -> R.id.employees_less_than_6
            NumberOfEmployeesFilter.BETWEEN_5_AND_201 -> R.id.employees_between_5_and_201
            NumberOfEmployeesFilter.MORE_THAN_200 -> R.id.employees_more_than_200
            else -> R.id.employees_all
        }

        val view = inflater.inflate(R.layout.fragment_search_filter_dialog, container, false)

        val radioGroup: RadioGroup = view.findViewById(R.id.filter_employees_radio_group)
        radioGroup.check(selectedFilterId)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            this@SearchFilterDialogFragment.selectedFilter = when (checkedId) {
                R.id.employees_less_than_6 -> NumberOfEmployeesFilter.LESS_THAN_6
                R.id.employees_between_5_and_201 -> NumberOfEmployeesFilter.BETWEEN_5_AND_201
                R.id.employees_more_than_200 -> NumberOfEmployeesFilter.MORE_THAN_200
                else -> NumberOfEmployeesFilter.ALL_EMPLOYEES
            }
        }

        val selectButton: Button = view.findViewById(R.id.select_filter_button)
        selectButton.setOnClickListener {
            listener!!.onSearchFilterDialogFragmentInteraction(selectedFilter)
            dismiss()
        }

        return view
    }

    interface OnSearchFilterDialogFragmentInteractionListener {
        fun onSearchFilterDialogFragmentInteraction(filter: NumberOfEmployeesFilter)
    }

    override fun onAttach(@NonNull context: Context) {
        super.onAttach(context)
        listener = if (context is OnSearchFilterDialogFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException("$context must implement OnSearchFilterDialogFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        const val ARGUMENT_FILTER_SELECTED = "ARGUMENT_FILTER_SELECTED"
    }
}

