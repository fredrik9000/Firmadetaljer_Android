package com.github.fredrik9000.firmadetaljer_android.repository.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Company::class], version = 1, exportSchema = false)
abstract class CompanyDatabase : RoomDatabase() {
    abstract fun companyDao(): CompanyDao
}