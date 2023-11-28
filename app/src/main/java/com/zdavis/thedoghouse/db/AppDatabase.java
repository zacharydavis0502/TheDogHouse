package com.zdavis.thedoghouse.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.zdavis.thedoghouse.House;

@Database(entities = {House.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "com.zdavis.thedoghouse.HOUSE_DATABASE";
    public static final String HOUSE_TABLE = "com.zdavis.thedoghouse.HOUSE_TABLE";
    public abstract  HouseDAO getHouseDao();
    public static final String PET_TABLE = "com.zdavis.thedoghouse.PET_TABLE";
    public abstract  HouseDAO getPetDao();
    public static final String USER_TABLE = "com.zdavis.thedoghouse.USER_TABLE";
    public abstract  HouseDAO getUserDao();
}
