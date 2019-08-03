package com.github.fredrik9000.firmadetaljer_android.company_details

import android.content.Context
import android.os.Bundle

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import com.github.fredrik9000.firmadetaljer_android.interfaces.ICompanyDetails
import com.github.fredrik9000.firmadetaljer_android.R

import com.github.fredrik9000.firmadetaljer_android.databinding.FragmentCompanyDetailsBinding
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

import java.util.ArrayList
import java.util.HashMap

class CompanyDetailsFragment : Fragment() {

    private lateinit var company: Company
    private lateinit var companyDetailsInterface: ICompanyDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        company = arguments!!.getParcelable(ARG_COMPANY)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentCompanyDetailsBinding>(
                inflater, R.layout.fragment_company_details, container, false)
        val view = binding.root

        val expandableListView = binding.companyDetailsList

        val (companyDetailGroups, companyDetailItems) = fillData()

        val adapter = CompanyDetailsAdapter(this.context!!, companyDetailGroups, companyDetailItems)
        expandableListView.setAdapter(adapter)

        expandableListView.setOnChildClickListener(ExpandableListView.OnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val companyDetailsDescription = companyDetailItems[companyDetailGroups[groupPosition]]!![childPosition]
            if (companyDetailsDescription.label == resources.getString(R.string.company_detail_details_overordnet_enhet)) {
                companyDetailsInterface.navigateToParentCompany(company.overordnetEnhet!!)
                return@OnChildClickListener true
            } else if (companyDetailsDescription.label == resources.getString(R.string.company_detail_details_hjemmeside)) {
                companyDetailsInterface.navigateToHomepage(company.hjemmeside!!)
                return@OnChildClickListener true
            }
            false
        })
        expandGroups(expandableListView, adapter)

        return view
    }

    private fun expandGroups(expandableListView: ExpandableListView, adapter: CompanyDetailsAdapter) {
        for (position in 1..adapter.groupCount)
            expandableListView.expandGroup(position - 1)
    }

    private fun fillData() : Pair<MutableList<String>, MutableMap<String, List<CompanyDetailsDescription>>> {
        val companyDetailGroups = ArrayList<String>()
        val companyDetailItems = HashMap<String, List<CompanyDetailsDescription>>()

        val detailsList = ArrayList<CompanyDetailsDescription>()

        if (company.navn != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_firmanavn), company.navn!!))
        }

        detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_organisasjonsnummer), (company.organisasjonsnummer).toString()))

        if (company.stiftelsesdato != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_stiftelsesdato), company.stiftelsesdato!!))
        }

        if (company.registreringsdatoEnhetsregisteret != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_enhetsregisteret), company.registreringsdatoEnhetsregisteret!!))
        }

        if (company.oppstartsdato != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_oppstartsdato), company.oppstartsdato!!))
        }

        if (company.datoEierskifte != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_eierskife), company.datoEierskifte!!))
        }

        if (company.organisasjonsform != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_organisasjonsform), company.organisasjonsform!!))
        }

        if (company.hjemmeside != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_hjemmeside), company.hjemmeside!!))
        }

        if (company.registertIFrivillighetsregisteret != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_frivillighetsregisteret), convertYesNoValue(company.registertIFrivillighetsregisteret!!)))
        }

        if (company.registrertIMvaregisteret != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_mva_registeret), convertYesNoValue(company.registrertIMvaregisteret!!)))
        }

        if (company.registrertIForetaksregisteret != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_foretaksregisteret), convertYesNoValue(company.registrertIForetaksregisteret!!)))
        }

        if (company.registrertIStiftelsesregisteret != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_registrert_i_stiftelsesregisteret), convertYesNoValue(company.registrertIStiftelsesregisteret!!)))
        }

        if (company.antallAnsatte != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_antall_ansatte), (company.antallAnsatte!!).toString()))
        }

        if (company.sisteInnsendteAarsregnskap != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_sist_innsendte_årsregnskap), (company.sisteInnsendteAarsregnskap!!).toString()))
        }

        if (company.konkurs != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_konkurs), convertYesNoValue(company.konkurs!!)))
        }

        if (company.underAvvikling != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_under_avvikling), convertYesNoValue(company.underAvvikling!!)))
        }

        if (company.underTvangsavviklingEllerTvangsopplosning != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_under_tvangsavvikling_eller_tvangsoppløsning), convertYesNoValue(company.underTvangsavviklingEllerTvangsopplosning!!)))
        }

        if (company.overordnetEnhet != null) {
            detailsList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_details_overordnet_enhet), (company.overordnetEnhet!!).toString()))
        }

        val detailsHeading = resources.getString(R.string.company_detail_heading_details)
        companyDetailItems[detailsHeading] = detailsList
        companyDetailGroups.add(detailsHeading)

        val institusjonellSektorKodeList = ArrayList<CompanyDetailsDescription>()

        if (company.institusjonellSektorkodeKode != null) {
            institusjonellSektorKodeList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), company.institusjonellSektorkodeKode!!))
        }

        if (company.institusjonellSektorkodeBeskrivelse != null) {
            institusjonellSektorKodeList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), company.institusjonellSektorkodeBeskrivelse!!))
        }

        if (institusjonellSektorKodeList.size > 0) {
            val institusjonellSektorKodeHeading = resources.getString(R.string.company_detail_heading_institusjonell_sektor_kode)
            companyDetailItems[institusjonellSektorKodeHeading] = institusjonellSektorKodeList
            companyDetailGroups.add(institusjonellSektorKodeHeading)
        }

        val naeringskode1List = ArrayList<CompanyDetailsDescription>()

        if (company.naeringskode1Kode != null) {
            naeringskode1List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), company.naeringskode1Kode!!))
        }

        if (company.naeringskode1Beskrivelse != null) {
            naeringskode1List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), company.naeringskode1Beskrivelse!!))
        }

        if (naeringskode1List.size > 0) {
            val naeringsKode1Heading = resources.getString(R.string.company_detail_heading_naeringskode1)
            companyDetailItems[naeringsKode1Heading] = naeringskode1List
            companyDetailGroups.add(naeringsKode1Heading)
        }

        val naeringskode2List = ArrayList<CompanyDetailsDescription>()

        if (company.naeringskode2Kode != null) {
            naeringskode2List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), company.naeringskode2Kode!!))
        }

        if (company.naeringskode2Beskrivelse != null) {
            naeringskode2List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), company.naeringskode2Beskrivelse!!))
        }

        if (naeringskode2List.size > 0) {
            val naeringsKode2Heading = resources.getString(R.string.company_detail_heading_naeringskode2)
            companyDetailItems[naeringsKode2Heading] = naeringskode2List
            companyDetailGroups.add(naeringsKode2Heading)
        }

        val naeringskode3List = ArrayList<CompanyDetailsDescription>()

        if (company.naeringskode3Kode != null) {
            naeringskode3List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_kode), company.naeringskode3Kode!!))
        }

        if (company.naeringskode3Beskrivelse != null) {
            naeringskode3List.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_kode_beskrivelse), company.naeringskode3Beskrivelse!!))
        }

        if (naeringskode3List.size > 0) {
            val naeringsKode3Heading = resources.getString(R.string.company_detail_heading_naeringskode3)
            companyDetailItems[naeringsKode3Heading] = naeringskode3List
            companyDetailGroups.add(naeringsKode3Heading)
        }

        val postadresseList = ArrayList<CompanyDetailsDescription>()

        if (company.postadresseAdresse != null) {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_adresse), company.postadresseAdresse!!))
        }

        if (company.postadressePostnummer != null) {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), company.postadressePostnummer!!))
        }

        if (company.postadressePoststed != null) {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), company.postadressePoststed!!))
        }

        if (company.postadresseKommunenummer != null) {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), company.postadresseKommunenummer!!))
        }

        if (company.postadresseKommune != null) {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), company.postadresseKommune!!))
        }

        if (company.postadresseLandkode != null) {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), company.postadresseLandkode!!))
        }

        if (company.postadresseLand != null) {
            postadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), company.postadresseLand!!))
        }

        if (postadresseList.size > 0) {
            val postadresseHeading = resources.getString(R.string.company_detail_heading_postadresse)
            companyDetailItems[postadresseHeading] = postadresseList
            companyDetailGroups.add(postadresseHeading)
        }

        val forretningsadresseList = ArrayList<CompanyDetailsDescription>()

        if (company.forretningsadresseAdresse != null) {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_adresse), company.forretningsadresseAdresse!!))
        }

        if (company.forretningsadressePostnummer != null) {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), company.forretningsadressePostnummer!!))
        }

        if (company.forretningsadressePoststed != null) {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), company.forretningsadressePoststed!!))
        }

        if (company.forretningsadresseKommunenummer != null) {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), company.forretningsadresseKommunenummer!!))
        }

        if (company.forretningsadresseKommune != null) {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), company.forretningsadresseKommune!!))
        }

        if (company.forretningsadresseLandkode != null) {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), company.forretningsadresseLandkode!!))
        }

        if (company.forretningsadresseLand != null) {
            forretningsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), company.forretningsadresseLand!!))
        }

        if (forretningsadresseList.size > 0) {
            val forretningsadresseHeading = resources.getString(R.string.company_detail_heading_forettningsadresse)
            companyDetailItems[forretningsadresseHeading] = forretningsadresseList
            companyDetailGroups.add(forretningsadresseHeading)
        }

        val beliggenhetsadresseList = ArrayList<CompanyDetailsDescription>()

        if (company.beliggenhetsadresseAdresse != null) {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_adresse), company.beliggenhetsadresseAdresse!!))
        }

        if (company.beliggenhetsadressePostnummer != null) {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_postnummer), company.beliggenhetsadressePostnummer!!))
        }

        if (company.beliggenhetsadressePoststed != null) {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_poststed), company.beliggenhetsadressePoststed!!))
        }

        if (company.beliggenhetsadresseKommunenummer != null) {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommunenummer), company.beliggenhetsadresseKommunenummer!!))
        }

        if (company.beliggenhetsadresseKommune != null) {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_kommune), company.beliggenhetsadresseKommune!!))
        }

        if (company.beliggenhetsadresseLandkode != null) {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_landskode), company.beliggenhetsadresseLandkode!!))
        }

        if (company.beliggenhetsadresseLand != null) {
            beliggenhetsadresseList.add(CompanyDetailsDescription(resources.getString(R.string.company_detail_adresse_land), company.beliggenhetsadresseLand!!))
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
        companyDetailsInterface = activity as ICompanyDetails
    }

    companion object {
        const val ARG_COMPANY = "COMPANY"
    }
}