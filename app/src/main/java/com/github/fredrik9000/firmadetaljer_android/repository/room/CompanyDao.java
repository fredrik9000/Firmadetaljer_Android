package com.github.fredrik9000.firmadetaljer_android.repository.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CompanyDao {

    @Insert
    void insert(Company company);

    @Update
    void update(Company company);

    @Delete
    void delete(Company company);

    @Query("SELECT * FROM copmany_table ORDER BY navn DESC")
    LiveData<List<Company>> getAllCompanies();
}
