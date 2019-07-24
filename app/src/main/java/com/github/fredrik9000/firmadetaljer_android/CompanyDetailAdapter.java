package com.github.fredrik9000.firmadetaljer_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CompanyDetailAdapter extends BaseExpandableListAdapter {

    private List<String> companyDetailGroups;
    private Map<String, List<CompanyDetailsDescription>> companyDetailItems;
    private Context context;

    public CompanyDetailAdapter(Context context, List<String> companyDetailGroups, Map<String, List<CompanyDetailsDescription>> companyDetailItems) {
        this.context = context;
        this.companyDetailGroups = companyDetailGroups;
        this.companyDetailItems = companyDetailItems;
    }

    @Override
    public int getGroupCount() {
        return companyDetailGroups.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return companyDetailItems.get(companyDetailGroups.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return companyDetailGroups.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return companyDetailItems.get(companyDetailGroups.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i*100 + i1; // Kind of hack to get a unique id
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String heading = (String)getGroup(i);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.company_details_listview_parent, viewGroup, false);
        }

        TextView textView = view.findViewById(R.id.group_heading);
        textView.setText(heading);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        CompanyDetailsDescription detail = (CompanyDetailsDescription)getChild(i, i1);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.company_details_listview_child, viewGroup, false);
        }

        TextView labelTextView = view.findViewById(R.id.label);
        labelTextView.setText(detail.getLabel());

        TextView descriptionTextView = view.findViewById(R.id.description);
        descriptionTextView.setText(detail.getDescription());

        CompanyDetailsDescription childElement = (CompanyDetailsDescription) getChild(i, i1);
        if(childElement.getLabel().equals(context.getResources().getString(R.string.company_detail_details_hjemmeside))
        || childElement.getLabel().equals(context.getResources().getString(R.string.company_detail_details_overordnet_enhet))) {
            view.findViewById(R.id.arrow_forward).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.arrow_forward).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        CompanyDetailsDescription childElement = (CompanyDetailsDescription) getChild(i, i1);
        return childElement.getLabel().equals(context.getResources().getString(R.string.company_detail_details_hjemmeside))
                || childElement.getLabel().equals(context.getResources().getString(R.string.company_detail_details_overordnet_enhet));
    }
}
