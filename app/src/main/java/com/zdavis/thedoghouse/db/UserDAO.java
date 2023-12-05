package com.zdavis.thedoghouse.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.zdavis.thedoghouse.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Insert
    void insert(User... user);
    @Update
    void update(User... user);
    @Delete
    void delete(User... user);
    @Query("SELECT * FROM " + AppDatabase.USER_TABLE)
    List<User> getUsers();

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE userId = :logId")
    List<User> getUsersById(int logId);

    @Query("SELECT * FROM (SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE username = :username) WHERE password = :password")
    List<User> authenticateUser(String username, String password);

    @Query("SELECT * FROM " + AppDatabase.USER_TABLE + " WHERE username = :username")
    List<User> getByUsername(String username);


}
