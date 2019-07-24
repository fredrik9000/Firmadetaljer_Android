package com.github.fredrik9000.firmadetaljer_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.fredrik9000.firmadetaljer_android.databinding.ListviewItemBinding;
import com.github.fredrik9000.firmadetaljer_android.repository.room.Company;

import java.util.List;


public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private final OnItemClickListener clickListener;
    private List<Company> companyList;

    protected CompanyAdapter(OnItemClickListener clickListener, List<Company> companyList) {
        this.clickListener = clickListener;
        this.companyList = companyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        ListviewItemBinding binding = ListviewItemBinding.inflate(inflater, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder companyItemViewHolder, final int position) {
        Company companyItem = companyList.get(position);
        companyItemViewHolder.bind(companyItem);
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public void update(List<Company> companyList) {
        this.companyList = companyList;
        this.notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Company company);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ListviewItemBinding binding;
        private final LinearLayout parentLayout;

        ViewHolder(ListviewItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            parentLayout = binding.parentLayout;

            parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (clickListener != null && position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(companyList.get(position));
                    }
                }
            });
        }

        void bind(Company company) {
            binding.setCompany(company);
            binding.executePendingBindings();
        }
    }
}
