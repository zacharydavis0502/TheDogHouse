package com.zdavis.thedoghouse.db.typeConverters;

import android.util.Log;

import androidx.room.TypeConverter;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import com.zdavis.thedoghouse.User;

public class Converters {
    @TypeConverter
    public static ArrayList<Integer> idStringToArrayList(String idString) {
        String[] ids = idString.split(",");
        ArrayList<Integer> idsArrayList = new ArrayList<>();
        for (String id : ids) {
            try {
                idsArrayList.add(Integer.parseInt(id));
            } catch(NumberFormatException e) {
                Log.i(e.toString(), "ERROR: Couldn't convert id " + id + " to integer!");
            }
        }
        return (idString == null && idString.trim().isEmpty()) ? new ArrayList<>() : idsArrayList;
    }

    @TypeConverter
    public static String idArrayListToString(ArrayList<Integer> ids) {
        StringJoiner joiner = new StringJoiner(",");
        for (Integer item: ids) {
            joiner.add(item.toString());
        }
         return ids.size() == 0 ? "" : joiner.toString();
    }

    @TypeConverter
    public static HashMap<Date, String> notesStringToHashMap(String notes) {
        return new HashMap<Date, String>();
    }

    @TypeConverter
    public static String notesHashMapToString(HashMap<Date, String> notes) {
        StringJoiner joiner = new StringJoiner(";");

        for (Map.Entry<Date, String> entry : notes.entrySet()) {
            Long longDate = entry.getKey().getTime();
            String note = entry.getValue();
            note = note.replaceAll(",", "/,").replaceAll(";", "/;");
            joiner.add(longDate.toString() + "," + note);
        }

        return notes.size() == 0 ? null : joiner.toString();
    }

    @TypeConverter
    public static LocalDate stringToDate(String dateString) {
        return (dateString.isEmpty()) ? null : LocalDate.parse(dateString);
    }

    @TypeConverter
    public static String localDateToString(LocalDate date) {
        return date == null ? null : date.toString();
    }
}
