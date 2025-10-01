package com.example.iot_lab4_20197115.vm;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.iot_lab4_20197115.data.Post;
import com.example.iot_lab4_20197115.data.PostRepository;
import java.util.List;

public class PostViewModel extends AndroidViewModel {
    private final PostRepository repo;
    public PostViewModel(@NonNull Application app){ super(app); repo = new PostRepository(app); }
    public LiveData<List<Post>> posts(){ return repo.observeAll(); }
    public LiveData<Post> post(int id){ return repo.observeById(id); }
    public void refreshBlocking(){ repo.refreshBlocking(); }
}