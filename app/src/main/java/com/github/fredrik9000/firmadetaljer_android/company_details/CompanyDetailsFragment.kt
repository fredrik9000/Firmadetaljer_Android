package com.github.fredrik9000.firmadetaljer_android.company_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.databinding.FragmentCompanyDetailsBinding
import companydb.CompanyEntity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompanyDetailsFragment : Fragment() {

    private lateinit var companyEntity: CompanyEntity
    private lateinit var companyDetailsNavigation: CompanyDetailsNavigationActivity
    private val companyDetailsViewModel: CompanyDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentCompanyDetailsBinding.inflate(inflater, container, false)

        val expandableListView = binding.companyDetailsList

        val companyOrgNumber = requireArguments().getInt(ARG_COMPANY_ORG_NUMBER)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            companyEntity = companyDetailsViewModel.getCompanyByOrgNumber(companyOrgNumber)!!

            val (companyDetailGroups, companyDetailItems) = fillData()

            val adapter = CompanyDetailsAdapter(this@CompanyDetailsFragment.requireContext(), companyDetailGroups, companyDetailItems)
            expandableListView.setAdapter(adapter)

            expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                val companyDetailsDescription = companyDetailItems[companyDetailGroups[groupPosition]]!![childPosition]
                // TODO: It's not good to use the labels as identifiers here. Should instead use something that is sure to be unique if the text changes.
                when (companyDetailsDescription.label) {
                    resources.getString(R.string.company_detail_details_overordnet_enhet) -> {
                        companyDetailsNavigation.navigateToCompany(companyEntity.overordnetEnhet!!.toInt())
                        return@setOnChildClickListener true
                    }
                    resources.getString(R.string.company_detail_details_hjemmeside) -> {
                        companyDetailsNavigation.navigateToHomepage(companyEntity.hjemmeside!!)
                        return@setOnChildClickListener true
                    }
                    resources.getString(R.string.company_detail_adresse_postadresse) -> {
                        navigateToMap(companyEntity.postadresseAdresse)
                        return@setOnChildClickListener true
                    }
                    resources.getString(R.string.company_detail_adresse_forretningsadresse) -> {
                        navigateToMap(companyEntity.forretningsadresseAdresse)
                        return@setOnChildClickListener true
                    }
                    resources.getString(R.string.company_detail_adresse_beliggenhetsadresse) -> {
                        navigateToMap(companyEntity.beliggenhetsadresseAdresse)
                        return@setOnChildClickListener true
                    }
                    else -> false
                }
            }
            expandGroups(expandableListView, adapter)
        }

        return binding.root
    }

    private fun navigateToMap(address: String?) {
        startActivity(Intent(activity, CompanyLocationMapActivity::class.java).apply {
            putExtra(CompanyLocationMapActivity.ADDRESS, address)
        })
    }

    private fun expandGroups(expandableListView: ExpandableListView, adapter: CompanyDetailsAdapter) {
        for (position in 1..adapter.groupCount)
            expandableListView.expandGroup(position - 1)
    }

    private fun fillData(): Pair<MutableList<String>, MutableMap<String, List<CompanyDetailsDescription>>> {
        val companyDetailGroups = ArrayList<String>()
        val companyDetailItems = HashMap<String, List<CompanyDetailsDescription>>()

        val detailsList = ArrayList<CompanyDetailsDescription>()

        detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_organisasjonsnummer), (companyEntity.organisasjonsnummer).toString()))

        companyEntity.hjemmeside?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_hjemmeside), it))
        }

        companyEntity.overordnetEnhet?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_overordnet_enhet), it.toString()))
        }

        companyEntity.stiftelsesdato?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_stiftelsesdato), it))
        }

        companyEntity.organisasjonsform?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_organisasjonsform), it))
        }

        companyEntity.antallAnsatte?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_antall_ansatte), it.toString()))
        }

        companyEntity.registreringsdatoEnhetsregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_enhetsregisteret), it))
        }

        companyEntity.oppstartsdato?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_oppstartsdato), it))
        }

        companyEntity.datoEierskifte?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_eierskife), it))
        }

        companyEntity.registertIFrivillighetsregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_frivillighetsregisteret), convertToYesNoString(it)))
        }

        companyEntity.registrertIMvaregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_mva_registeret), convertToYesNoString(it)))
        }

        companyEntity.registrertIForetaksregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_foretaksregisteret), convertToYesNoString(it)))
        }

        companyEntity.registrertIStiftelsesregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_stiftelsesregisteret), convertToYesNoString(it)))
        }

        companyEntity.sisteInnsendteAarsregnskap?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_sist_innsendte_årsregnskap), it.toString()))
        }

        companyEntity.konkurs?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_konkurs), convertToYesNoString(it)))
        }

        companyEntity.underAvvikling?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_under_avvikling), convertToYesNoString(it)))
        }

        companyEntity.underTvangsavviklingEllerTvangsopplosning?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_under_tvangsavvikling_eller_tvangsoppløsning), convertToYesNoString(it)))
        }

        val detailsHeading = companyEntity.navn
                ?: resources.getString(R.string.company_detail_heading_details)

        companyDetailItems[detailsHeading] = detailsList
        companyDetailGroups.add(detailsHeading)

        val forretningsadresseList = ArrayList<CompanyDetailsDescription>()

        companyEntity.forretningsadresseAdresse?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_forretningsadresse), it))
        }

        companyEntity.forretningsadressePostnummer?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), it))
        }

        companyEntity.forretningsadressePoststed?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), it))
        }

        companyEntity.forretningsadresseKommunenummer?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), it))
        }

        companyEntity.forretningsadresseKommune?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), it))
        }

        companyEntity.forretningsadresseLandkode?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), it))
        }

        companyEntity.forretningsadresseLand?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), it))
        }

        if (forretningsadresseList.size > 0) {
            val forretningsadresseHeading = resources.getString(R.string.company_detail_heading_forettningsadresse)
            companyDetailItems[forretningsadresseHeading] = forretningsadresseList
            companyDetailGroups.add(forretningsadresseHeading)
        }

        val beliggenhetsadresseList = ArrayList<CompanyDetailsDescription>()

        companyEntity.beliggenhetsadresseAdresse?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_beliggenhetsadresse), it))
        }

        companyEntity.beliggenhetsadressePostnummer?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), it))
        }

        companyEntity.beliggenhetsadressePoststed?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), it))
        }

        companyEntity.beliggenhetsadresseKommunenummer?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), it))
        }

        companyEntity.beliggenhetsadresseKommune?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), it))
        }

        companyEntity.beliggenhetsadresseLandkode?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), it))
        }

        companyEntity.beliggenhetsadresseLand?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), it))
        }

        if (beliggenhetsadresseList.size > 0) {
            val beliggenhetsadresseHeading = resources.getString(R.string.company_detail_heading_beliggenhetsadresse)
            companyDetailItems[beliggenhetsadresseHeading] = beliggenhetsadresseList
            companyDetailGroups.add(beliggenhetsadresseHeading)
        }

        val postadresseList = ArrayList<CompanyDetailsDescription>()

        companyEntity.postadresseAdresse?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postadresse), it))
        }

        companyEntity.postadressePostnummer?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), it))
        }

        companyEntity.postadressePoststed?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), it))
        }

        companyEntity.postadresseKommunenummer?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), it))
        }

        companyEntity.postadresseKommune?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), it))
        }

        companyEntity.postadresseLandkode?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), it))
        }

        companyEntity.postadresseLand?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), it))
        }

        if (postadresseList.size > 0) {
            val postadresseHeading = resources.getString(R.string.company_detail_heading_postadresse)
            companyDetailItems[postadresseHeading] = postadresseList
            companyDetailGroups.add(postadresseHeading)
        }

        val institusjonellSektorKodeList = ArrayList<CompanyDetailsDescription>()

        companyEntity.institusjonellSektorkodeKode?.let {
            institusjonellSektorKodeList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        companyEntity.institusjonellSektorkodeBeskrivelse?.let {
            institusjonellSektorKodeList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (institusjonellSektorKodeList.size > 0) {
            val institusjonellSektorKodeHeading = resources.getString(R.string.company_detail_heading_institusjonell_sektor_kode)
            companyDetailItems[institusjonellSektorKodeHeading] = institusjonellSektorKodeList
            companyDetailGroups.add(institusjonellSektorKodeHeading)
        }

        val naeringskode1List = ArrayList<CompanyDetailsDescription>()

        companyEntity.naeringskode1Kode?.let {
            naeringskode1List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        companyEntity.naeringskode1Beskrivelse?.let {
            naeringskode1List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (naeringskode1List.size > 0) {
            val naeringsKode1Heading = resources.getString(R.string.company_detail_heading_naeringskode1)
            companyDetailItems[naeringsKode1Heading] = naeringskode1List
            companyDetailGroups.add(naeringsKode1Heading)
        }

        val naeringskode2List = ArrayList<CompanyDetailsDescription>()

        companyEntity.naeringskode2Kode?.let {
            naeringskode2List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        companyEntity.naeringskode2Beskrivelse?.let {
            naeringskode2List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (naeringskode2List.size > 0) {
            val naeringsKode2Heading = resources.getString(R.string.company_detail_heading_naeringskode2)
            companyDetailItems[naeringsKode2Heading] = naeringskode2List
            companyDetailGroups.add(naeringsKode2Heading)
        }

        val naeringskode3List = ArrayList<CompanyDetailsDescription>()

        companyEntity.naeringskode3Kode?.let {
            naeringskode3List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        companyEntity.naeringskode3Beskrivelse?.let {
            naeringskode3List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (naeringskode3List.size > 0) {
            val naeringsKode3Heading = resources.getString(R.string.company_detail_heading_naeringskode3)
            companyDetailItems[naeringsKode3Heading] = naeringskode3List
            companyDetailGroups.add(naeringsKode3Heading)
        }

        return Pair(companyDetailGroups, companyDetailItems)
    }

    private fun convertToYesNoString(valueIsTrue: Boolean): String {
        return when (valueIsTrue) {
            true -> resources.getString(R.string.yes)
            false -> resources.getString(R.string.no)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        companyDetailsNavigation = activity as CompanyDetailsNavigationActivity
    }

    companion object {
        const val ARG_COMPANY_ORG_NUMBER = "COMPANY_ORG_NUMBER"
    }
}