package com.github.fredrik9000.firmadetaljer_android.repository.room

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Company::class], version = 1, exportSchema = false)
abstract class CompanyDatabase : RoomDatabase() {

    abstract val companyDao: CompanyDao

    companion object {

        @Volatile
        private var INSTANCE: CompanyDatabase? = null

        @Synchronized
        fun getInstance(context: Context): CompanyDatabase {
            var instance = INSTANCE

            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext,
                        CompanyDatabase::class.java, "company_database")
                        .fallbackToDestructiveMigration()
                        .build()
            }

            return instance
        }
    }
}