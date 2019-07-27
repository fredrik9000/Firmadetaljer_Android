package com.github.fredrik9000.firmadetaljer_android.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CompanyDao {

    @get:Query("SELECT * FROM company_table ORDER BY id DESC")
    val allCompaniesOrderedByLastInserted: LiveData<List<Company>>

    @Insert
    suspend fun insert(company: Company)

    @Delete
    suspend fun delete(company: Company)
}
