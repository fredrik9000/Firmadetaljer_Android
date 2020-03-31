package com.github.fredrik9000.firmadetaljer_android.company_details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.databinding.FragmentCompanyDetailsBinding
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import java.util.*

class CompanyDetailsFragment : Fragment() {

    private lateinit var company: Company
    private lateinit var companyDetailsNavigation: CompanyDetailsNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        company = arguments!!.getParcelable(ARG_COMPANY)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCompanyDetailsBinding>(
                inflater, R.layout.fragment_company_details, container, false)

        val expandableListView = binding.companyDetailsList

        val (companyDetailGroups, companyDetailItems) = fillData()

        val adapter = CompanyDetailsAdapter(this.context!!, companyDetailGroups, companyDetailItems)
        expandableListView.setAdapter(adapter)

        expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val companyDetailsDescription = companyDetailItems[companyDetailGroups[groupPosition]]!![childPosition]
            // TODO: It's not good to use the labels as identifiers here. Should instead use something that is sure to be unique if the text changes.
            when {
                companyDetailsDescription.label == resources.getString(R.string.company_detail_details_overordnet_enhet) -> {
                    companyDetailsNavigation.navigateToCompany(company.overordnetEnhet!!)
                    return@setOnChildClickListener true
                }
                companyDetailsDescription.label == resources.getString(R.string.company_detail_details_hjemmeside) -> {
                    companyDetailsNavigation.navigateToHomepage(company.hjemmeside!!)
                    return@setOnChildClickListener true
                }
                companyDetailsDescription.label == resources.getString(R.string.company_detail_adresse_postadresse) -> {
                    navigateToMap(company.postadresseAdresse)
                    return@setOnChildClickListener true
                }
                companyDetailsDescription.label == resources.getString(R.string.company_detail_adresse_forretningsadresse) -> {
                    navigateToMap(company.forretningsadresseAdresse)
                    return@setOnChildClickListener true
                }
                companyDetailsDescription.label == resources.getString(R.string.company_detail_adresse_beliggenhetsadresse) -> {
                    navigateToMap(company.beliggenhetsadresseAdresse)
                    return@setOnChildClickListener true
                }
                else -> false
            }
        }
        expandGroups(expandableListView, adapter)

        return binding.root
    }

    private fun navigateToMap(address: String?) {
        val intent = Intent(activity, CompanyLocationMapActivity::class.java)
        intent.putExtra(CompanyLocationMapActivity.ADDRESS, address)
        startActivity(intent)
    }

    private fun expandGroups(expandableListView: ExpandableListView, adapter: CompanyDetailsAdapter) {
        for (position in 1..adapter.groupCount)
            expandableListView.expandGroup(position - 1)
    }

    private fun fillData() : Pair<MutableList<String>, MutableMap<String, List<CompanyDetailsDescription>>> {
        val companyDetailGroups = ArrayList<String>()
        val companyDetailItems = HashMap<String, List<CompanyDetailsDescription>>()

        val detailsList = ArrayList<CompanyDetailsDescription>()

        company.navn?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_firmanavn), it))
        }

        detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_organisasjonsnummer), (company.organisasjonsnummer).toString()))

        company.stiftelsesdato?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_stiftelsesdato), it))
        }

        company.registreringsdatoEnhetsregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_enhetsregisteret), it))
        }

        company.oppstartsdato?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_oppstartsdato), it))
        }

        company.datoEierskifte?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_eierskife), it))
        }

        company.organisasjonsform?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_organisasjonsform), it))
        }

        company.hjemmeside?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_hjemmeside), it))
        }

        company.registertIFrivillighetsregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_frivillighetsregisteret), convertYesNoValue(it)))
        }

        company.registrertIMvaregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_mva_registeret), convertYesNoValue(it)))
        }

        company.registrertIForetaksregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_foretaksregisteret), convertYesNoValue(it)))
        }

        company.registrertIStiftelsesregisteret?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_stiftelsesregisteret), convertYesNoValue(it)))
        }

        company.antallAnsatte?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_antall_ansatte), it.toString()))
        }

        company.sisteInnsendteAarsregnskap?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_sist_innsendte_årsregnskap), it.toString()))
        }

        company.konkurs?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_konkurs), convertYesNoValue(it)))
        }

        company.underAvvikling?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_under_avvikling), convertYesNoValue(it)))
        }

        company.underTvangsavviklingEllerTvangsopplosning?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_under_tvangsavvikling_eller_tvangsoppløsning), convertYesNoValue(it)))
        }

        company.overordnetEnhet?.let {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_overordnet_enhet), it.toString()))
        }

        val detailsHeading = resources.getString(R.string.company_detail_heading_details)
        companyDetailItems[detailsHeading] = detailsList
        companyDetailGroups.add(detailsHeading)

        val institusjonellSektorKodeList = ArrayList<CompanyDetailsDescription>()

        company.institusjonellSektorkodeKode?.let {
            institusjonellSektorKodeList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        company.institusjonellSektorkodeBeskrivelse?.let {
            institusjonellSektorKodeList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (institusjonellSektorKodeList.size > 0) {
            val institusjonellSektorKodeHeading = resources.getString(R.string.company_detail_heading_institusjonell_sektor_kode)
            companyDetailItems[institusjonellSektorKodeHeading] = institusjonellSektorKodeList
            companyDetailGroups.add(institusjonellSektorKodeHeading)
        }

        val naeringskode1List = ArrayList<CompanyDetailsDescription>()

        company.naeringskode1Kode?.let {
            naeringskode1List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        company.naeringskode1Beskrivelse?.let {
            naeringskode1List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (naeringskode1List.size > 0) {
            val naeringsKode1Heading = resources.getString(R.string.company_detail_heading_naeringskode1)
            companyDetailItems[naeringsKode1Heading] = naeringskode1List
            companyDetailGroups.add(naeringsKode1Heading)
        }

        val naeringskode2List = ArrayList<CompanyDetailsDescription>()

        company.naeringskode2Kode?.let {
            naeringskode2List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        company.naeringskode2Beskrivelse?.let {
            naeringskode2List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (naeringskode2List.size > 0) {
            val naeringsKode2Heading = resources.getString(R.string.company_detail_heading_naeringskode2)
            companyDetailItems[naeringsKode2Heading] = naeringskode2List
            companyDetailGroups.add(naeringsKode2Heading)
        }

        val naeringskode3List = ArrayList<CompanyDetailsDescription>()

        company.naeringskode3Kode?.let {
            naeringskode3List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), it))
        }

        company.naeringskode3Beskrivelse?.let {
            naeringskode3List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), it))
        }

        if (naeringskode3List.size > 0) {
            val naeringsKode3Heading = resources.getString(R.string.company_detail_heading_naeringskode3)
            companyDetailItems[naeringsKode3Heading] = naeringskode3List
            companyDetailGroups.add(naeringsKode3Heading)
        }

        val postadresseList = ArrayList<CompanyDetailsDescription>()

        company.postadresseAdresse?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postadresse), it))
        }

        company.postadressePostnummer?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), it))
        }

        company.postadressePoststed?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), it))
        }

        company.postadresseKommunenummer?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), it))
        }

        company.postadresseKommune?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), it))
        }

        company.postadresseLandkode?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), it))
        }

        company.postadresseLand?.let {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), it))
        }

        if (postadresseList.size > 0) {
            val postadresseHeading = resources.getString(R.string.company_detail_heading_postadresse)
            companyDetailItems[postadresseHeading] = postadresseList
            companyDetailGroups.add(postadresseHeading)
        }

        val forretningsadresseList = ArrayList<CompanyDetailsDescription>()

        company.forretningsadresseAdresse?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_forretningsadresse), it))
        }

        company.forretningsadressePostnummer?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), it))
        }

        company.forretningsadressePoststed?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), it))
        }

        company.forretningsadresseKommunenummer?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), it))
        }

        company.forretningsadresseKommune?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), it))
        }

        company.forretningsadresseLandkode?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), it))
        }

        company.forretningsadresseLand?.let {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), it))
        }

        if (forretningsadresseList.size > 0) {
            val forretningsadresseHeading = resources.getString(R.string.company_detail_heading_forettningsadresse)
            companyDetailItems[forretningsadresseHeading] = forretningsadresseList
            companyDetailGroups.add(forretningsadresseHeading)
        }

        val beliggenhetsadresseList = ArrayList<CompanyDetailsDescription>()

        company.beliggenhetsadresseAdresse?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_beliggenhetsadresse), it))
        }

        company.beliggenhetsadressePostnummer?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), it))
        }

        company.beliggenhetsadressePoststed?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), it))
        }

        company.beliggenhetsadresseKommunenummer?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), it))
        }

        company.beliggenhetsadresseKommune?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), it))
        }

        company.beliggenhetsadresseLandkode?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), it))
        }

        company.beliggenhetsadresseLand?.let {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), it))
        }

        if (beliggenhetsadresseList.size > 0) {
            val beliggenhetsadresseHeading = resources.getString(R.string.company_detail_heading_beliggenhetsadresse)
            companyDetailItems[beliggenhetsadresseHeading] = beliggenhetsadresseList
            companyDetailGroups.add(beliggenhetsadresseHeading)
        }

        return Pair(companyDetailGroups, companyDetailItems)
    }

    private fun convertYesNoValue(description: String) : String {
        return when(description) {
            "J" -> resources.getString(R.string.yes)
            "N" -> resources.getString(R.string.no)
            else -> description
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        companyDetailsNavigation = activity as CompanyDetailsNavigation
    }

    companion object {
        const val ARG_COMPANY = "COMPANY"
    }
}