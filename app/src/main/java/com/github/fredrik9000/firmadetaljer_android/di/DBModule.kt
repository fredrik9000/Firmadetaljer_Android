package com.github.fredrik9000.firmadetaljer_android.di

import android.app.Application
import androidx.room.Room
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDao
import com.github.fredrik9000.firmadetaljer_android.repository.room.CompanyDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DBModule {
    @JvmStatic
    @Provides
    @Singleton
    internal fun provideDatabase(application: Application) : CompanyDatabase {
        return Room.databaseBuilder(application, CompanyDatabase::class.java, "company_database")
                .fallbackToDestructiveMigration()
                .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    internal fun provideCompanyDao(companyDatabase: CompanyDatabase): CompanyDao {
        return companyDatabase.companyDao()
    }
}