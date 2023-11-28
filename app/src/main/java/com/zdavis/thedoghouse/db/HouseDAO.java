package com.zdavis.thedoghouse.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zdavis.thedoghouse.House;

import java.util.List;

@Dao
public interface HouseDAO {
    @Insert
    void insert(House... occupants);
    @Update
    void update(House... occupants);
    @Delete
    void delete(House... occupants);
    @Query("SELECT * FROM " + AppDatabase.HOUSE_TABLE)
    List<House> getHouses();

    @Query("SELECT * FROM " + AppDatabase.HOUSE_TABLE + " WHERE houseId = :logId")
    House getHousesById(int logId);


}
