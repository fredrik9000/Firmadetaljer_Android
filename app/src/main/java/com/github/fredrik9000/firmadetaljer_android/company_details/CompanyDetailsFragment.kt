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

    private val companyDetailGroups = ArrayList<String>()
    private val companyDetailItems = HashMap<String, List<CompanyDetailsDescription>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCompanyDetailsBinding.inflate(inflater, container, false)

        val expandableListView = binding.companyDetailsList

        val companyOrgNumber = requireArguments().getInt(ARG_COMPANY_ORG_NUMBER)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            companyEntity = companyDetailsViewModel.getCompanyByOrgNumber(companyOrgNumber)!!

            fillData()

            val adapter = CompanyDetailsAdapter(
                context = this@CompanyDetailsFragment.requireContext(),
                companyDetailGroups = companyDetailGroups,
                companyDetailItems = companyDetailItems
            )
            expandableListView.setAdapter(adapter)

            expandableListView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                val companyDetailsDescription = companyDetailItems[companyDetailGroups[groupPosition]]!![childPosition]
                // TODO: It's not good to use the labels as identifiers here.
                //  Should instead use something that is sure to be unique if the text changes.
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

    private fun fillData() {
        addMainDetailsListToCompanyDetails()
        addForretningsadresseListToCompanyDetails()
        addBeliggenhetsadresseListToCompanyDetails()
        addPostadresseListToCompanyDetails()
        addInstitusjonellSektorKodeListToDetails()
        addNaeringskode1ListToCompanyDetails()
        addNaeringskode2ListToCompanyDetails()
        addNaeringskode3ListToCompanyDetails()
    }

    private fun addMainDetailsListToCompanyDetails() {
        val detailsList = ArrayList<CompanyDetailsDescription>()

        detailsList.addDetailsDescription(
            labelResourceId = R.string.company_detail_details_organisasjonsnummer,
            description = (companyEntity.organisasjonsnummer).toString()
        )

        companyEntity.hjemmeside?.let {
            detailsList.addDetailsDescription(labelResourceId = R.string.company_detail_details_hjemmeside, description = it)
        }

        companyEntity.overordnetEnhet?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_overordnet_enhet,
                description = it.toString()
            )
        }

        companyEntity.stiftelsesdato?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_stiftelsesdato,
                description = it
            )
        }

        companyEntity.organisasjonsform?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_organisasjonsform,
                description = it
            )
        }

        companyEntity.antallAnsatte?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_antall_ansatte,
                description = it.toString()
            )
        }

        companyEntity.registreringsdatoEnhetsregisteret?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_registrert_i_enhetsregisteret,
                description = it
            )
        }

        companyEntity.oppstartsdato?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_oppstartsdato,
                description = it
            )
        }

        companyEntity.datoEierskifte?.let {
            detailsList.addDetailsDescription(labelResourceId = R.string.company_detail_details_eierskife, description = it)
        }

        companyEntity.registertIFrivillighetsregisteret?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_registrert_i_frivillighetsregisteret,
                description = it.toYesOrNo()
            )
        }

        companyEntity.registrertIMvaregisteret?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_registrert_i_mva_registeret,
                description = it.toYesOrNo()
            )
        }

        companyEntity.registrertIForetaksregisteret?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_registrert_i_foretaksregisteret,
                description = it.toYesOrNo()
            )
        }

        companyEntity.registrertIStiftelsesregisteret?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_registrert_i_stiftelsesregisteret,
                description = it.toYesOrNo()
            )
        }

        companyEntity.sisteInnsendteAarsregnskap?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_sist_innsendte_årsregnskap,
                description = it.toString()
            )
        }

        companyEntity.konkurs?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_konkurs,
                description = it.toYesOrNo()
            )
        }

        companyEntity.underAvvikling?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_under_avvikling,
                description = it.toYesOrNo()
            )
        }

        companyEntity.underTvangsavviklingEllerTvangsopplosning?.let {
            detailsList.addDetailsDescription(
                labelResourceId = R.string.company_detail_details_under_tvangsavvikling_eller_tvangsoppløsning,
                description = it.toYesOrNo()
            )
        }

        val detailsHeading = companyEntity.navn ?: resources.getString(R.string.company_detail_heading_details)
        companyDetailGroups.add(detailsHeading)
        companyDetailItems[detailsHeading] = detailsList
    }

    private fun addForretningsadresseListToCompanyDetails() {
        addGroupAndDetails(
            headingResourceId = R.string.company_detail_heading_forettningsadresse,
            companyDetailList = createAddressList(
                adresse = companyEntity.forretningsadresseAdresse,
                postnummer = companyEntity.forretningsadressePostnummer,
                poststed = companyEntity.forretningsadressePoststed,
                kommunenummer = companyEntity.forretningsadresseKommunenummer,
                kommune = companyEntity.forretningsadresseKommune,
                landkode = companyEntity.forretningsadresseLandkode,
                land = companyEntity.forretningsadresseLand,
                addressLabelId = R.string.company_detail_adresse_forretningsadresse
            )
        )
    }

    private fun addBeliggenhetsadresseListToCompanyDetails() {
        addGroupAndDetails(
            headingResourceId = R.string.company_detail_heading_beliggenhetsadresse,
            companyDetailList = createAddressList(
                adresse = companyEntity.beliggenhetsadresseAdresse,
                postnummer = companyEntity.beliggenhetsadressePostnummer,
                poststed = companyEntity.beliggenhetsadressePoststed,
                kommunenummer = companyEntity.beliggenhetsadresseKommunenummer,
                kommune = companyEntity.beliggenhetsadresseKommune,
                landkode = companyEntity.beliggenhetsadresseLandkode,
                land = companyEntity.beliggenhetsadresseLand,
                addressLabelId = R.string.company_detail_adresse_beliggenhetsadresse
            )
        )
    }

    private fun addPostadresseListToCompanyDetails() {
        addGroupAndDetails(
            headingResourceId = R.string.company_detail_heading_postadresse,
            companyDetailList = createAddressList(
                adresse = companyEntity.postadresseAdresse,
                postnummer = companyEntity.postadressePostnummer,
                poststed = companyEntity.postadressePoststed,
                kommunenummer = companyEntity.postadresseKommunenummer,
                kommune = companyEntity.postadresseKommune,
                landkode = companyEntity.postadresseLandkode,
                land = companyEntity.postadresseLand,
                addressLabelId = R.string.company_detail_adresse_postadresse
            )
        )
    }

    private fun createAddressList(
        adresse: String?,
        postnummer: String?,
        poststed: String?,
        kommunenummer: String?,
        kommune: String?,
        landkode: String?,
        land: String?,
        addressLabelId: Int
    ): ArrayList<CompanyDetailsDescription> {
        val addressList = ArrayList<CompanyDetailsDescription>()

        adresse?.let {
            addressList.addDetailsDescription(labelResourceId = addressLabelId, description = it)
        }

        postnummer?.let {
            addressList.addDetailsDescription(labelResourceId = R.string.company_detail_adresse_postnummer, description = it)
        }

        poststed?.let {
            addressList.addDetailsDescription(labelResourceId = R.string.company_detail_adresse_poststed, description = it)
        }

        kommunenummer?.let {
            addressList.addDetailsDescription(
                labelResourceId = R.string.company_detail_adresse_kommunenummer,
                description = it
            )
        }

        kommune?.let {
            addressList.addDetailsDescription(labelResourceId = R.string.company_detail_adresse_kommune, description = it)
        }

        landkode?.let {
            addressList.addDetailsDescription(labelResourceId = R.string.company_detail_adresse_landskode, description = it)
        }

        land?.let {
            addressList.addDetailsDescription(labelResourceId = R.string.company_detail_adresse_land, description = it)
        }

        return addressList
    }

    private fun addInstitusjonellSektorKodeListToDetails() {
        addGroupAndDetails(
            headingResourceId = R.string.company_detail_heading_institusjonell_sektor_kode,
            companyDetailList = createKodeBeskrivelseList(
                kode = companyEntity.institusjonellSektorkodeKode,
                beskrivelse = companyEntity.institusjonellSektorkodeBeskrivelse
            )
        )
    }

    private fun addNaeringskode1ListToCompanyDetails() {
        addGroupAndDetails(
            headingResourceId = R.string.company_detail_heading_naeringskode1,
            companyDetailList = createKodeBeskrivelseList(
                kode = companyEntity.naeringskode1Kode,
                beskrivelse = companyEntity.naeringskode1Beskrivelse
            )
        )
    }

    private fun addNaeringskode2ListToCompanyDetails() {
        addGroupAndDetails(
            headingResourceId = R.string.company_detail_heading_naeringskode2,
            companyDetailList = createKodeBeskrivelseList(
                kode = companyEntity.naeringskode2Kode,
                beskrivelse = companyEntity.naeringskode2Beskrivelse
            )
        )
    }

    private fun addNaeringskode3ListToCompanyDetails() {
        addGroupAndDetails(
            headingResourceId = R.string.company_detail_heading_naeringskode3,
            companyDetailList = createKodeBeskrivelseList(
                kode = companyEntity.naeringskode3Kode,
                beskrivelse = companyEntity.naeringskode3Beskrivelse
            )
        )
    }

    private fun addGroupAndDetails(
        headingResourceId: Int,
        companyDetailList: ArrayList<CompanyDetailsDescription>
    ) {
        if (companyDetailList.isEmpty()) {
            return
        }

        val heading = resources.getString(headingResourceId)
        companyDetailGroups.add(heading)
        companyDetailItems[heading] = companyDetailList
    }

    private fun createKodeBeskrivelseList(kode: String?, beskrivelse: String?): ArrayList<CompanyDetailsDescription> {
        val kodeBeskrivelseList = ArrayList<CompanyDetailsDescription>()

        kode?.let {
            kodeBeskrivelseList.addDetailsDescription(labelResourceId = R.string.company_detail_kode_kode, description = it)
        }

        beskrivelse?.let {
            kodeBeskrivelseList.addDetailsDescription(
                labelResourceId = R.string.company_detail_kode_beskrivelse,
                description = it
            )
        }

        return kodeBeskrivelseList
    }

    private fun ArrayList<CompanyDetailsDescription>.addDetailsDescription(labelResourceId: Int, description: String) {
        this.add(CompanyDetailsDescription(label = resources.getString(labelResourceId), description = description))
    }

    private fun Boolean.toYesOrNo(): String {
        return when (this) {
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