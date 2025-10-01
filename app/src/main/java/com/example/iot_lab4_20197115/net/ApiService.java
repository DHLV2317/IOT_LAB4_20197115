package com.example.iot_lab4_20197115.net;

import com.example.iot_lab4_20197115.data.Post;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/posts")
    Call<List<Post>> getPosts(); // JSONPlaceholder
}