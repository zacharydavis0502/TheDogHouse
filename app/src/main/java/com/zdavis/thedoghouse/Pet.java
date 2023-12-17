package com.zdavis.thedoghouse;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.zdavis.thedoghouse.db.AppDatabase;

import java.time.LocalDate;
import java.util.Date;

@Entity(tableName = AppDatabase.PET_TABLE)
public class Pet {
    @PrimaryKey(autoGenerate = true)
    private int petId;
    private String name;
    private String type;
    private String color;
    private LocalDate birthdate;
    private String notes;

    public Pet(String name, String type, String color, LocalDate birthdate) {
        this.name = name;
        this.type = type;
        this.color = color;
        this.birthdate = birthdate;
        this.notes = "";
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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
