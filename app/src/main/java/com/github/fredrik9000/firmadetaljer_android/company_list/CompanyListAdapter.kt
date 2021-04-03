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

    var selectedPosition = UNSELECTED_ITEM_POSITION

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        return ViewHolder(ListviewItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false))
    }

    override fun onBindViewHolder(companyItemViewHolder: ViewHolder, position: Int) {
        companyItemViewHolder.bind(companyList!![position])
        if (highlightSelectedItem) {
            companyItemViewHolder.itemView.setBackgroundColor(if (selectedPosition == position) ContextCompat.getColor(context, R.color.colorSelectedCompany) else Color.TRANSPARENT)
        }
    }

    override fun getItemCount(): Int {
        return companyList!!.size
    }

    fun update(companyList: List<Company>) {
        selectedPosition = UNSELECTED_ITEM_POSITION // When the list updates remove the selected item highlight
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

    private companion object {
        const val UNSELECTED_ITEM_POSITION = -1
    }
}