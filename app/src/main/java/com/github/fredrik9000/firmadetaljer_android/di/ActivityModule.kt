package com.github.fredrik9000.firmadetaljer_android.di

import com.github.fredrik9000.firmadetaljer_android.company_details.CompanyDetailsActivity
import com.github.fredrik9000.firmadetaljer_android.company_list.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeCompanyDetailsActivity(): CompanyDetailsActivity
}