package com.github.fredrik9000.firmadetaljer_android.company_list

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.fredrik9000.firmadetaljer_android.R
import com.github.fredrik9000.firmadetaljer_android.databinding.ListviewItemBinding
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company

// This class isn't very decoupled due to having the isViewedCompaniesList variable.
// TODO: Improve upon implementation
class CompanyListAdapter(
        private val context: Context,
        private val clickListener: OnItemClickListener?,
        private var companyList: List<Company>?,
        private val highlightSelectedItem: Boolean,
        private val isViewedCompaniesList: Boolean)
    : RecyclerView.Adapter<CompanyListAdapter.ViewHolder>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ListviewItemBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(companyItemViewHolder: ViewHolder, position: Int) {
        val companyItem = companyList!![position]
        companyItemViewHolder.bind(companyItem)
        if (highlightSelectedItem) {
            companyItemViewHolder.itemView.setBackgroundColor(if (selectedPosition == position) ContextCompat.getColor(context, R.color.colorSelectedCompany) else Color.TRANSPARENT)
        }
    }

    override fun getItemCount(): Int {
        return companyList!!.size
    }

    fun update(companyList: List<Company>) {
        this.companyList = companyList
        this.notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(company: Company, isViewedCompaniesList: Boolean)
    }

    inner class ViewHolder(private val binding: ListviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val parentLayout = binding.parentLayout

        init {
            parentLayout.setOnClickListener {
                val position = adapterPosition

                if (highlightSelectedItem) {
                    notifyItemChanged(selectedPosition) // Update previously selected item
                    selectedPosition = position
                    notifyItemChanged(selectedPosition) // Update newly selected item
                }

                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(companyList!![position], isViewedCompaniesList)
                }
            }
        }

        fun bind(company: Company) {
            binding.company = company
            binding.executePendingBindings()
        }
    }
}
