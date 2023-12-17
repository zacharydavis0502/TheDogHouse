package com.zdavis.thedoghouse.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zdavis.thedoghouse.Pet;

import java.util.List;

@Dao
public interface PetDAO {
    @Insert
    long insert(Pet pets);

    @Insert
    long[] insert(Pet... pets);
    @Update
    void update(Pet... pets);
    @Delete
    void delete(Pet... pets);
    @Query("SELECT * FROM " + AppDatabase.PET_TABLE)
    List<Pet> getPets();

    @Query("SELECT * FROM " + AppDatabase.PET_TABLE + " WHERE petId = :logId")
    List<Pet> getPetById(int logId);


}
