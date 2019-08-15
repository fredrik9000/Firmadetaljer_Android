package com.github.fredrik9000.firmadetaljer_android.company_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.github.fredrik9000.firmadetaljer_android.databinding.ListviewItemBinding
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company
import androidx.recyclerview.widget.DiffUtil

class CompanyListAdapter(
        private val clickListener: OnItemClickListener?,
        private var companyList: List<Company>?)
    : RecyclerView.Adapter<CompanyListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ListviewItemBinding.inflate(inflater, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(companyItemViewHolder: ViewHolder, position: Int) {
        val companyItem = companyList!![position]
        companyItemViewHolder.bind(companyItem)
    }

    override fun getItemCount(): Int {
        return companyList!!.size
    }

    fun update(companyList: List<Company>) {
        this.companyList = companyList
        this.notifyDataSetChanged()
    }

    fun updateWithAnimation(companyList: List<Company>) {
        val diffCallback = CompanyDiffCallback(this.companyList!!, companyList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.companyList = companyList
        diffResult.dispatchUpdatesTo(this)
    }

    interface OnItemClickListener {
        fun onItemClick(company: Company)
    }

    inner class ViewHolder(private val binding: ListviewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val parentLayout = binding.parentLayout

        init {
            parentLayout.setOnClickListener {
                val position = adapterPosition
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(companyList!![position])
                }
            }
        }

        fun bind(company: Company) {
            binding.company = company
            binding.executePendingBindings()
        }
    }
}
