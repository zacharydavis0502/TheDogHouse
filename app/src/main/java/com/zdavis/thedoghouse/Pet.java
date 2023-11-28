package com.zdavis.thedoghouse;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zdavis.thedoghouse.db.AppDatabase;

import java.util.Date;
import java.util.HashMap;

@Entity(tableName = AppDatabase.PET_TABLE)
public class Pet {
    @PrimaryKey(autoGenerate = true)
    private int petId;
    private String name;
    private String type;
    private String color;
    private Date birthdate;
    private HashMap<Date, String> notes;

    public Pet(String name, String type, String color, Date birthdate) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.birthdate = birthdate;
        this.notes = new HashMap<>();
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public HashMap<Date, String> getNotes() {
        return notes;
    }

    public void setNotes(HashMap<Date, String> notes) {
        this.notes = notes;
    }
}
