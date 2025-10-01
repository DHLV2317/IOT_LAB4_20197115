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

public class SportsAdapter extends RecyclerView.Adapter<SportsAdapter.Holder> {

    private final List<SportItem> data = new ArrayList<>();

    public void submit(List<SportItem> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sport, parent, false);
        return new Holder(v);
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        SportItem it = data.get(pos);
        h.tMatch.setText(it.match.isEmpty() ? "—" : it.match);
        h.tTournament.setText("Torneo: " + (it.tournament.isEmpty() ? "—" : it.tournament));
        h.tStart.setText("Inicio: " + (it.start.isEmpty() ? "—" : it.start));
        String venue = "";
        if (!it.stadium.isEmpty()) venue += it.stadium;
        if (!it.region.isEmpty())  venue += (venue.isEmpty() ? "" : " · ") + it.region;
        if (!it.country.isEmpty()) venue += (venue.isEmpty() ? "" : " · ") + it.country;
        h.tVenue.setText(venue.isEmpty() ? "—" : venue);
    }

    @Override public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tMatch, tTournament, tStart, tVenue;
        Holder(View v) {
            super(v);
            tMatch      = v.findViewById(R.id.tMatch);
            tTournament = v.findViewById(R.id.tTournament);
            tStart      = v.findViewById(R.id.tStart);
            tVenue      = v.findViewById(R.id.tVenue);
        }
    }
}