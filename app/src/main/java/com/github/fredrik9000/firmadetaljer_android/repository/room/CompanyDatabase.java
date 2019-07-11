package com.github.fredrik9000.firmadetaljer_android.repository.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Company.class}, version = 1)
public abstract class CompanyDatabase extends RoomDatabase {

    private static CompanyDatabase instance;

    public static synchronized CompanyDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CompanyDatabase.class, "company_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract CompanyDao companyDao();
}