package com.github.fredrik9000.firmadetaljer_android.di

import android.content.Context
import androidx.room.Room
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    internal fun provideDatabase(@ApplicationContext application: Context) : CompanyDatabase {
        return Room.databaseBuilder(application, CompanyDatabase::class.java, "company_database")
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    @Singleton
    internal fun provideCompanyDao(companyDatabase: CompanyDatabase): CompanyDao {
        return companyDatabase.companyDao()
    }
}