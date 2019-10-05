package com.github.fredrik9000.firmadetaljer_android.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsViewModel
import com.github.fredrik9000.firmadetaljer_android.company_list.CompanyListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CompanyListViewModel::class)
    protected abstract fun companyListViewModel(companyListViewModel: CompanyListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CompanyDetailsViewModel::class)
    protected abstract fun companyDetailsViewModel(companyDetailsViewModel: CompanyDetailsViewModel): ViewModel
}