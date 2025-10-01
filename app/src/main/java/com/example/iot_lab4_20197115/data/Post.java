package com.example.iot_lab4_20197115.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "posts")
public class Post {
    @PrimaryKey public int id;
    public int userId;
    public String title;
    public String body;
}