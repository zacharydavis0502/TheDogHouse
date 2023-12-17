package com.zdavis.thedoghouse.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zdavis.thedoghouse.House;
import com.zdavis.thedoghouse.User;

import java.util.List;

@Dao
public interface HouseDAO {
    @Insert
    long insert(House house);

    @Insert
    long[] insert(House... house);
    @Update
    void update(House... house);
    @Delete
    void delete(House... house);
    @Query("SELECT * FROM " + AppDatabase.HOUSE_TABLE)
    List<House> getHouses();

    @Query("SELECT * FROM " + AppDatabase.HOUSE_TABLE + " WHERE houseId = :logId")
    List<House> getHouseById(int logId);

    @Query("SELECT * FROM " + AppDatabase.HOUSE_TABLE + " WHERE name = :houseName")
    List<House> getByName(String houseName);
}
