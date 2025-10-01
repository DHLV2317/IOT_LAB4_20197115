package com.example.iot_lab4_20197115.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iot_lab4_20197115.R;
import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    public interface OnLocationClick {
        void onClick(LocationItem item);
    }

    private final OnLocationClick listener;
    private final List<LocationItem> data = new ArrayList<>();

    public LocationAdapter(OnLocationClick listener) {
        this.listener = listener;
    }

    public void submit(List<LocationItem> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        LocationItem item = data.get(pos);
        h.tName.setText(item.getName());
        h.tRegion.setText("Región: " + item.getRegion());
        h.tCountry.setText("País: " + item.getCountry());
        h.tCoords.setText("Lat: " + item.getLat() + " | Lon: " + item.getLon());
        h.tUrl.setText("URL: " + item.getUrl());

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tName, tRegion, tCountry, tCoords, tUrl;
        ViewHolder(View v) {
            super(v);
            tName = v.findViewById(R.id.tName);
            tRegion = v.findViewById(R.id.tRegion);
            tCountry = v.findViewById(R.id.tCountry);
            tCoords = v.findViewById(R.id.tCoords);
            tUrl = v.findViewById(R.id.tUrl);
        }
    }
}