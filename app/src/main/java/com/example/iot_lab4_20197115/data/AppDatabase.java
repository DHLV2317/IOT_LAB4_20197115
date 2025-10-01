package com.example.iot_lab4_20197115.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Post.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PostDao postDao();

    private static volatile AppDatabase I;
    public static AppDatabase get(Context c){
        if (I == null){
            synchronized (AppDatabase.class){
                if (I == null){
                    I = Room.databaseBuilder(c.getApplicationContext(),
                            AppDatabase.class, "lab4.db").build();
                }
            }
        }
        return I;
    }
}