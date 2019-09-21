package com.github.fredrik9000.firmadetaljer_android.company_details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.fredrik9000.firmadetaljer_android.R

class CompanyDetailsAdapter(
        private val context: Context,
        private val companyDetailGroups: List<String>,
        private val companyDetailItems: Map<String, List<CompanyDetailsDescription>>) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return companyDetailGroups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return companyDetailItems.getValue(companyDetailGroups[groupPosition]).size
    }

    override fun getGroup(groupPosition: Int): Any {
        return companyDetailGroups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return companyDetailItems.getValue(companyDetailGroups[groupPosition])[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return (groupPosition * 100 + childPosition).toLong() // Kind of a hack to get a unique id
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, view: View?, viewGroup: ViewGroup): View {
        var viewMutable = view
        val heading = getGroup(groupPosition) as String

        if (viewMutable == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            viewMutable = inflater.inflate(R.layout.company_details_listview_parent, viewGroup, false)
        }

        val textView = viewMutable!!.findViewById<TextView>(R.id.group_heading)
        textView.text = heading

        return viewMutable
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, viewGroup: ViewGroup): View {
        var viewMutable = view
        val child = getChild(groupPosition, childPosition) as CompanyDetailsDescription

        if (viewMutable == null) {
            val inflater = LayoutInflater.from(viewGroup.context)
            viewMutable = inflater.inflate(R.layout.company_details_listview_child, viewGroup, false)
        }

        val labelTextView = viewMutable!!.findViewById<TextView>(R.id.label)
        labelTextView.text = child.label

        val descriptionTextView = viewMutable.findViewById<TextView>(R.id.description)
        descriptionTextView.text = child.description

        if (child.label == context.resources.getString(R.string.company_detail_details_hjemmeside) || child.label == context.resources.getString(R.string.company_detail_details_overordnet_enhet)) {
            viewMutable.findViewById<ImageView>(R.id.arrow_forward).visibility = View.VISIBLE
            descriptionTextView.setTextColor(ContextCompat.getColor(context, R.color.colorTextDetailDescriptionNavigatable))
            descriptionTextView.alpha = 1.0F
        } else {
            viewMutable.findViewById<ImageView>(R.id.arrow_forward).visibility = View.GONE
            descriptionTextView.setTextColor(getDescriptionTextColor(context))
            descriptionTextView.alpha = 0.65F
        }

        return viewMutable
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        val child = getChild(groupPosition, childPosition) as CompanyDetailsDescription
        return child.label == context.resources.getString(R.string.company_detail_details_hjemmeside)
                || child.label == context.resources.getString(R.string.company_detail_details_overordnet_enhet)
    }

    private fun getDescriptionTextColor(context: Context) : Int {
        val typedArray = context.obtainStyledAttributes(null, intArrayOf(android.R.attr.textColorPrimary))
        try {
            return typedArray.getColor(0, ContextCompat.getColor(context, R.color.colorTextListDescriptionPrimaryFallback))
        } finally {
            typedArray.recycle()
        }
    }
}
