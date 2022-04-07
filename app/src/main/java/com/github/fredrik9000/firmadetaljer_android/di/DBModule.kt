package com.github.fredrik9000.firmadetaljer_android.di

import android.content.Context
import com.github.fredrik9000.firmadetaljer_android.repository.data.CompanyDataSource
import com.github.fredrik9000.firmadetaljer_android.repository.data.CompanyDataSourceImpl
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sqldelight.companydb.CompanyDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    internal fun provideSqlDriver(@ApplicationContext application: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = CompanyDatabase.Schema,
            context = application,
            name = "company.db"
        )
    }

    @Provides
    @Singleton
    internal fun provideCompanyDataSource(driver: SqlDriver): CompanyDataSource {
        return CompanyDataSourceImpl(db = CompanyDatabase(driver))
    }
}