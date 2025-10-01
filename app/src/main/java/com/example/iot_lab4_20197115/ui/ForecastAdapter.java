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

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.Holder> {

    private final List<ForecastItem> data = new ArrayList<>();

    public void submit(List<ForecastItem> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new Holder(v);
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        ForecastItem it = data.get(pos);
        h.tDate.setText(it.date);
        h.tCond.setText("Condición: " + it.condition);
        h.tTemps.setText("Máx: " + it.maxC + "°C  |  Mín: " + it.minC + "°C");
        h.tExtra.setText("Precip: " + it.precipMm + " mm  •  Viento máx: " + it.maxWindKph + " kph");
    }

    @Override public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tDate, tCond, tTemps, tExtra;
        Holder(View v) {
            super(v);
            tDate  = v.findViewById(R.id.tDate);
            tCond  = v.findViewById(R.id.tCond);
            tTemps = v.findViewById(R.id.tTemps);
            tExtra = v.findViewById(R.id.tExtra);
        }
    }
}