package com.github.fredrik9000.firmadetaljer_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CompanyDetailAdapter extends BaseExpandableListAdapter {

    private List<String> companyDetailGroups;
    private Map<String, List<CompanyDetailDescription>> companyDetailItems;

    public CompanyDetailAdapter(List<String> companyDetailGroups, Map<String, List<CompanyDetailDescription>> companyDetailItems) {
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
            view = inflater.inflate(R.layout.company_detail_listview_parent, viewGroup, false);
        }

        TextView textView = view.findViewById(R.id.group_heading);
        textView.setText(heading);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        CompanyDetailDescription detail = (CompanyDetailDescription)getChild(i, i1);

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.company_detail_listview_child, viewGroup, false);
        }

        TextView labelTextView = view.findViewById(R.id.label);
        labelTextView.setText(detail.getLabel());

        TextView descriptionTextView = view.findViewById(R.id.description);
        descriptionTextView.setText(detail.getDescription());

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        //TODO: The homepage and parent company should be selectable
        return false;
    }
}
