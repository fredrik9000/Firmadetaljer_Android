package com.github.fredrik9000.firmadetaljer_android;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

public class CompanyDetailFragment extends Fragment {

    public static final String ARG_COMPANY = "company";

    private Company company;

    public CompanyDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            company = getArguments().getParcelable(ARG_COMPANY);
        } else {
            company = new Company();
            //TODO: handle error
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_detail, container, false);

        TextView textView = view.findViewById(R.id.test_text);
        textView.setText(company.getNavn());

        return view;
    }
}
