package com.zdavis.thedoghouse.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.zdavis.thedoghouse.House;
import com.zdavis.thedoghouse.Pet;
import com.zdavis.thedoghouse.User;
import com.zdavis.thedoghouse.db.typeConverters.Converters;

@Database(entities = {House.class, Pet.class, User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB_NAME = "DOGHOUSE_DATABASE.db";
    public static final String HOUSE_TABLE = "HOUSE_TABLE";
    public abstract  HouseDAO getHouseDao();
    public static final String PET_TABLE = "PET_TABLE";
    public abstract  PetDAO getPetDao();
    public static final String USER_TABLE = "USER_TABLE";
    public abstract  UserDAO getUserDao();

    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if(instance == null) {
                   instance = Room.databaseBuilder(context.getApplicationContext(),
                           AppDatabase.class,
                           DB_NAME)
                           .allowMainThreadQueries()
                           .createFromAsset("database/DOGHOUSE_DATABASE_pop.db")
                           .build();
                }
            }
        }
        return instance;
    }
}
