package com.github.fredrik9000.firmadetaljer_android.repository.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CompanyDao {

    @get:Query("SELECT * FROM company_table ORDER BY id DESC")
    val allCompaniesOrderedByLastInserted: LiveData<List<Company>>

    @Insert
    suspend fun insert(company: Company)

    @Query("DELETE FROM company_table WHERE organisasjonsnummer = :organisasjonsnummer")
    suspend fun deleteByOrganizationNumber(organisasjonsnummer: Int)

    @Query("DELETE FROM company_table")
    suspend fun deleteAll()
}
