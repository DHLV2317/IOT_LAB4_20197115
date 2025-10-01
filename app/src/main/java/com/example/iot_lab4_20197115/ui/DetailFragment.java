package com.example.iot_lab4_20197115.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.iot_lab4_20197115.databinding.FragmentDetailBinding;
import com.example.iot_lab4_20197115.vm.PostViewModel;

public class DetailFragment extends Fragment {
    private FragmentDetailBinding b;
    private PostViewModel vm;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        b = FragmentDetailBinding.inflate(inf, c, false);
        return b.getRoot();
    }

    @Override public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        vm = new ViewModelProvider(this).get(PostViewModel.class);
        int id = getArguments()!=null ? getArguments().getInt("postId", -1) : -1;
        if (id >= 0) vm.post(id).observe(getViewLifecycleOwner(), p -> {
            if (p != null){ b.tTitle.setText(p.title); b.tBody.setText(p.body); }
        });
    }
}