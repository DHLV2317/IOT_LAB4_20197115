package com.example.iot_lab4_20197115.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iot_lab4_20197115.R;
import com.example.iot_lab4_20197115.data.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.VH> {

    private final Consumer<Post> onClick;
    private final List<Post> data = new ArrayList<>();

    public PostAdapter(Consumer<Post> onClick) {
        this.onClick = onClick;
    }

    public void submit(List<Post> items){
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    public List<Post> getCurrentItems(){
        return new ArrayList<>(data);
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Post p = data.get(position);
        h.tTitle.setText(p.title == null ? "(sin título)" : p.title);
        h.tBody.setText(p.body == null ? "" : p.body);
        h.itemView.setOnClickListener(v -> onClick.accept(p));
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tTitle, tBody;
        VH(@NonNull View v) {
            super(v);
            tTitle = v.findViewById(R.id.tTitle);
            tBody  = v.findViewById(R.id.tBody);
        }
    }
}