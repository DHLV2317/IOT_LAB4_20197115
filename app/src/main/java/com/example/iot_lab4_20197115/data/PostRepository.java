package com.example.iot_lab4_20197115.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.iot_lab4_20197115.net.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class PostRepository {
    private final PostDao dao;

    public PostRepository(Context c){
        dao = AppDatabase.get(c).postDao();
    }

    public LiveData<List<Post>> observeAll(){ return dao.observeAll(); }
    public LiveData<Post> observeById(int id){ return dao.observeById(id); }

    /** Descarga y guarda (se llama desde Worker o al hacer shake). */
    public boolean refreshBlocking(){
        try {
            Response<List<Post>> res = RetrofitClient.get().getPosts().execute();
            if (res.isSuccessful() && res.body()!=null){
                dao.upsertAll(res.body());
                return true;
            }
        } catch (IOException ignore) {}
        return false;
    }
}