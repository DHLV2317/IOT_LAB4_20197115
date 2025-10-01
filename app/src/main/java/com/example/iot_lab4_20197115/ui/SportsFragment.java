package com.example.iot_lab4_20197115.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iot_lab4_20197115.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SportsFragment extends Fragment {

    private EditText inQuery;
    private Button btnSearch;
    private ProgressBar progress;
    private TextView empty;
    private RecyclerView recycler;
    private SportsAdapter adapter;

    private static final String API_KEY = "becac55206564c98b87224707250110";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        inQuery = v.findViewById(R.id.inQuery);
        btnSearch = v.findViewById(R.id.btnSearch);
        progress = v.findViewById(R.id.progress);
        empty = v.findViewById(R.id.empty);
        recycler = v.findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SportsAdapter();
        recycler.setAdapter(adapter);

        btnSearch.setOnClickListener(x -> search());
    }

    private void search() {
        String q = inQuery.getText().toString().trim();
        if (TextUtils.isEmpty(q)) {
            empty.setText(getString(R.string.empty_search));
            empty.setVisibility(View.VISIBLE);
            return;
        }
        new TaskSports().execute(q);
    }

    private class TaskSports extends AsyncTask<String, Void, List<SportItem>> {
        private String error;

        @Override protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }

        @Override
        protected List<SportItem> doInBackground(String... p) {
            String q = p[0];
            String urlS = "https://api.weatherapi.com/v1/sports.json?key="
                    + API_KEY + "&q=" + q;
            try {
                URL url = new URL(urlS);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                c.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line; while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONObject root = new JSONObject(sb.toString());
                JSONArray football = root.optJSONArray("football");
                List<SportItem> out = new ArrayList<>();
                if (football != null) {
                    for (int i = 0; i < football.length(); i++) {
                        JSONObject o = football.getJSONObject(i);
                        String stadium    = o.optString("stadium", "");
                        String country    = o.optString("country", "");
                        String region     = o.optString("region", "");
                        String tournament = o.optString("tournament", "");
                        String start      = o.optString("start", "");
                        // Algunos esquemas incluyen "match" o "home/away"
                        String match = o.optString("match", "");
                        if (TextUtils.isEmpty(match)) {
                            String home = o.optString("home", o.optString("home_team", ""));
                            String away = o.optString("away", o.optString("away_team", ""));
                            match = (home.isEmpty() && away.isEmpty()) ? "" : (home + " vs " + away);
                        }
                        out.add(new SportItem(match, tournament, start, stadium, country, region));
                    }
                }
                return out;
            } catch (Exception e) {
                error = e.getMessage();
                return null;
            }
        }

        @Override protected void onPostExecute(List<SportItem> res) {
            progress.setVisibility(View.GONE);
            if (res == null) {
                empty.setText("Error: " + error);
                empty.setVisibility(View.VISIBLE);
                adapter.submit(new ArrayList<>());
            } else if (res.isEmpty()) {
                empty.setText(R.string.no_data);
                empty.setVisibility(View.VISIBLE);
                adapter.submit(new ArrayList<>());
            } else {
                empty.setVisibility(View.GONE);
                adapter.submit(res);
            }
        }
    }
}