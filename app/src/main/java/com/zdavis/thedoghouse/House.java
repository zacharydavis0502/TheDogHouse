package com.zdavis.thedoghouse;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zdavis.thedoghouse.db.AppDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

@Entity(tableName = AppDatabase.HOUSE_TABLE)
public class House {
    @PrimaryKey(autoGenerate = true)
    private int houseId;
    private String name;
    private ArrayList<User> users;
    private ArrayList<Pet> pets;

    public House(String name) {
        this.name = name;
        this.users = new ArrayList<>();
        this.pets = new ArrayList<>();
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
    }
}
