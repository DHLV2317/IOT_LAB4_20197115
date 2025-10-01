package com.example.iot_lab4_20197115.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM posts ORDER BY id")
    LiveData<List<Post>> observeAll();

    @Query("SELECT * FROM posts WHERE id=:id LIMIT 1")
    LiveData<Post> observeById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<Post> items);

    @Query("DELETE FROM posts")
    void clear();
}