package com.github.fredrik9000.firmadetaljer_android;

import android.content.Context;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.github.fredrik9000.firmadetaljer_android.databinding.FragmentCompanyDetailBinding;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyDetailFragment extends Fragment {

    public static final String ARG_COMPANY = "company";

    private Company company;
    private List<String> companyDetailGroups;
    private Map<String, List<CompanyDetailDescription>> companyDetailItems;

    private ICompanyDetails companyDetailsInterface;

    public CompanyDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        company = getArguments().getParcelable(ARG_COMPANY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCompanyDetailBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_company_detail, container, false);
        View view = binding.getRoot();

        ExpandableListView expandableListView = binding.companyDetailList;

        fillData();

        CompanyDetailAdapter adapter = new CompanyDetailAdapter(this.getContext(), companyDetailGroups, companyDetailItems);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                CompanyDetailDescription companyDetailDescription = companyDetailItems.get(companyDetailGroups.get(i)).get(i1);
                if (companyDetailDescription.getLabel().equals(getResources().getString(R.string.company_detail_details_overordnet_enhet))) {
                    companyDetailsInterface.navigateToParentCompany(company.getOverordnetEnhet());
                    return true;
                } else if (companyDetailDescription.getLabel().equals(getResources().getString(R.string.company_detail_details_hjemmeside))) {
                    companyDetailsInterface.navigateToHomepage(company.getHjemmeside());
                    return true;
                }
                return false;
            }
        });
        expandGroups(expandableListView, adapter);

        return view;
    }

    private void expandGroups(ExpandableListView expandableListView, CompanyDetailAdapter adapter) {
        int count = adapter.getGroupCount();
        for (int position = 1; position <= count; position++)
            expandableListView.expandGroup(position - 1);
    }

    private void fillData() {
        companyDetailGroups = new ArrayList<>();
        List<CompanyDetailDescription> detailsList = new ArrayList<>();

        if (company.getNavn() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_firmanavn), company.getNavn()));
        }

        if (company.getOrganisasjonsnummer() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_organisasjonsnummer), Integer.toString(company.getOrganisasjonsnummer())));
        }

        if (company.getStiftelsesdato() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_stiftelsesdato), company.getStiftelsesdato()));
        }

        if (company.getRegistreringsdatoEnhetsregisteret() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_registrert_i_enhetsregisteret), company.getRegistreringsdatoEnhetsregisteret()));
        }

        if (company.getOppstartsdato() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_oppstartsdato), company.getOppstartsdato()));
        }

        if (company.getDatoEierskifte() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_eierskife), company.getDatoEierskifte()));
        }

        if (company.getOrganisasjonsform() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_organisasjonsform), company.getOrganisasjonsform()));
        }

        if (company.getHjemmeside() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_hjemmeside), company.getHjemmeside()));
        }

        if (company.getRegistertIFrivillighetsregisteret() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_registrert_i_frivillighetsregisteret), company.getRegistertIFrivillighetsregisteret()));
        }

        if (company.getRegistrertIMvaregisteret() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_registrert_i_mva_registeret), company.getRegistrertIMvaregisteret()));
        }

        if (company.getRegistrertIForetaksregisteret() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_registrert_i_foretaksregisteret), company.getRegistrertIForetaksregisteret()));
        }

        if (company.getRegistrertIStiftelsesregisteret() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_registrert_i_stiftelsesregisteret), company.getRegistrertIStiftelsesregisteret()));
        }

        if (company.getAntallAnsatte() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_antall_ansatte), Integer.toString(company.getAntallAnsatte())));
        }

        if (company.getSisteInnsendteAarsregnskap() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_sist_innsendte_årsregnskap), Integer.toString(company.getSisteInnsendteAarsregnskap())));
        }

        if (company.getKonkurs() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_konkurs), company.getKonkurs()));
        }

        if (company.getUnderAvvikling() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_under_avvikling), company.getUnderAvvikling()));
        }

        if (company.getUnderTvangsavviklingEllerTvangsopplosning() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_under_tvangsavvikling_eller_tvangsoppløsning), company.getUnderTvangsavviklingEllerTvangsopplosning()));
        }

        if (company.getOverordnetEnhet() != null) {
            detailsList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_details_overordnet_enhet), Integer.toString(company.getOverordnetEnhet())));
        }

        companyDetailItems = new HashMap<>();
        String detailsHeading = getResources().getString(R.string.company_detail_heading_details);
        companyDetailItems.put(detailsHeading, detailsList);
        companyDetailGroups.add(detailsHeading);

        List<CompanyDetailDescription> institusjonellSektorKodeList = new ArrayList<>();

        if (company.getInstitusjonellSektorkodeKode() != null) {
            institusjonellSektorKodeList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_kode), company.getInstitusjonellSektorkodeKode()));
        }

        if (company.getInstitusjonellSektorkodeBeskrivelse() != null) {
            institusjonellSektorKodeList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_beskrivelse), company.getInstitusjonellSektorkodeBeskrivelse()));
        }

        if (institusjonellSektorKodeList.size() > 0) {
            String institusjonellSektorKodeHeading = getResources().getString(R.string.company_detail_heading_institusjonell_sektor_kode);
            companyDetailItems.put(institusjonellSektorKodeHeading, institusjonellSektorKodeList);
            companyDetailGroups.add(institusjonellSektorKodeHeading);
        }

        List<CompanyDetailDescription> naeringskode1List = new ArrayList<>();

        if (company.getNaeringskode1Kode() != null) {
            naeringskode1List.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_kode), company.getNaeringskode1Kode()));
        }

        if (company.getNaeringskode1Beskrivelse() != null) {
            naeringskode1List.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_beskrivelse), company.getNaeringskode1Beskrivelse()));
        }

        if (naeringskode1List.size() > 0) {
            String naeringsKode1Heading = getResources().getString(R.string.company_detail_heading_naeringskode1);
            companyDetailItems.put(naeringsKode1Heading, naeringskode1List);
            companyDetailGroups.add(naeringsKode1Heading);
        }

        List<CompanyDetailDescription> naeringskode2List = new ArrayList<>();

        if (company.getNaeringskode2Kode() != null) {
            naeringskode2List.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_kode), company.getNaeringskode2Kode()));
        }

        if (company.getNaeringskode2Beskrivelse() != null) {
            naeringskode2List.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_beskrivelse), company.getNaeringskode2Beskrivelse()));
        }

        if (naeringskode2List.size() > 0) {
            String naeringsKode2Heading = getResources().getString(R.string.company_detail_heading_naeringskode1);
            companyDetailItems.put(naeringsKode2Heading, naeringskode2List);
            companyDetailGroups.add(naeringsKode2Heading);
        }

        List<CompanyDetailDescription> naeringskode3List = new ArrayList<>();

        if (company.getNaeringskode3Kode() != null) {
            naeringskode3List.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_kode), company.getNaeringskode3Kode()));
        }

        if (company.getNaeringskode3Beskrivelse() != null) {
            naeringskode3List.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_kode_beskrivelse), company.getNaeringskode3Beskrivelse()));
        }

        if (naeringskode3List.size() > 0) {
            String naeringsKode3Heading = getResources().getString(R.string.company_detail_heading_naeringskode1);
            companyDetailItems.put(naeringsKode3Heading, naeringskode3List);
            companyDetailGroups.add(naeringsKode3Heading);
        }

        List<CompanyDetailDescription> postadresseList = new ArrayList<>();

        if (company.getPostadresseAdresse() != null) {
            postadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_adresse), company.getPostadresseAdresse()));
        }

        if (company.getPostadressePostnummer() != null) {
            postadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_postnummer), company.getPostadressePostnummer()));
        }

        if (company.getPostadressePoststed() != null) {
            postadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_poststed), company.getPostadressePoststed()));
        }

        if (company.getPostadresseKommunenummer() != null) {
            postadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_kommunenummer), company.getPostadresseKommunenummer()));
        }

        if (company.getPostadresseKommune() != null) {
            postadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_kommune), company.getPostadresseKommune()));
        }

        if (company.getPostadresseLandkode() != null) {
            postadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_landskode), company.getPostadresseLandkode()));
        }

        if (company.getPostadresseLand() != null) {
            postadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_land), company.getPostadresseLand()));
        }

        if (postadresseList.size() > 0) {
            String postadresseHeading = getResources().getString(R.string.company_detail_heading_postadresse);
            companyDetailItems.put(postadresseHeading, postadresseList);
            companyDetailGroups.add(postadresseHeading);
        }

        List<CompanyDetailDescription> forretningsadresseList = new ArrayList<>();

        if (company.getForretningsadresseAdresse() != null) {
            forretningsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_adresse), company.getForretningsadresseAdresse()));
        }

        if (company.getForretningsadressePostnummer() != null) {
            forretningsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_postnummer), company.getForretningsadressePostnummer()));
        }

        if (company.getForretningsadressePoststed() != null) {
            forretningsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_poststed), company.getForretningsadressePoststed()));
        }

        if (company.getForretningsadresseKommunenummer() != null) {
            forretningsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_kommunenummer), company.getForretningsadresseKommunenummer()));
        }

        if (company.getForretningsadresseKommune() != null) {
            forretningsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_kommune), company.getForretningsadresseKommune()));
        }

        if (company.getForretningsadresseLandkode() != null) {
            forretningsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_landskode), company.getForretningsadresseLandkode()));
        }

        if (company.getForretningsadresseLand() != null) {
            forretningsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_land), company.getForretningsadresseLand()));
        }

        if (forretningsadresseList.size() > 0) {
            String forretningsadresseHeading = getResources().getString(R.string.company_detail_heading_forettningsadresse);
            companyDetailItems.put(forretningsadresseHeading, forretningsadresseList);
            companyDetailGroups.add(forretningsadresseHeading);
        }

        List<CompanyDetailDescription> beliggenhetsadresseList = new ArrayList<>();

        if (company.getBeliggenhetsadresseAdresse() != null) {
            beliggenhetsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_adresse), company.getBeliggenhetsadresseAdresse()));
        }

        if (company.getBeliggenhetsadressePostnummer() != null) {
            beliggenhetsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_postnummer), company.getBeliggenhetsadressePostnummer()));
        }

        if (company.getBeliggenhetsadressePoststed() != null) {
            beliggenhetsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_poststed), company.getBeliggenhetsadressePoststed()));
        }

        if (company.getBeliggenhetsadresseKommunenummer() != null) {
            beliggenhetsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_kommunenummer), company.getBeliggenhetsadresseKommunenummer()));
        }

        if (company.getBeliggenhetsadresseKommune() != null) {
            beliggenhetsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_kommune), company.getBeliggenhetsadresseKommune()));
        }

        if (company.getBeliggenhetsadresseLandkode() != null) {
            beliggenhetsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_landskode), company.getBeliggenhetsadresseLandkode()));
        }

        if (company.getBeliggenhetsadresseLand() != null) {
            beliggenhetsadresseList.add(new CompanyDetailDescription(getResources().getString(R.string.company_detail_adresse_land), company.getBeliggenhetsadresseLand()));
        }

        if (beliggenhetsadresseList.size() > 0) {
            String beliggenhetsadresseHeading = getResources().getString(R.string.company_detail_heading_beliggenhetsadresse);
            companyDetailItems.put(beliggenhetsadresseHeading, beliggenhetsadresseList);
            companyDetailGroups.add(beliggenhetsadresseHeading);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        companyDetailsInterface = (ICompanyDetails) getActivity();
    }
}
