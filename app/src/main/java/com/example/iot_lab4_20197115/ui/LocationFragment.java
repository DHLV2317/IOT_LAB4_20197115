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
import androidx.navigation.Navigation;
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

public class LocationFragment extends Fragment {

    private EditText inQuery;
    private Button btnSearch;
    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView empty;
    private LocationAdapter adapter;

    private static final String API_KEY = "becac55206564c98b87224707250110";

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        inQuery = v.findViewById(R.id.inQuery);
        btnSearch = v.findViewById(R.id.btnSearch);
        recycler = v.findViewById(R.id.recycler);
        progress = v.findViewById(R.id.progress);
        empty = v.findViewById(R.id.empty);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LocationAdapter(item -> {
            // Al hacer click ir a ForecastFragment con id y name:
            Bundle b = new Bundle();
            b.putInt("locationId", item.getId());
            b.putString("locationName", item.getName());
            Navigation.findNavController(v).navigate(R.id.forecastFragment, b);
        });
        recycler.setAdapter(adapter);

        btnSearch.setOnClickListener(x -> searchLocation());
    }

    private void searchLocation() {
        String query = inQuery.getText().toString().trim();
        if (TextUtils.isEmpty(query)) {
            empty.setText(getString(R.string.empty_search));
            empty.setVisibility(View.VISIBLE);
            return;
        }
        new TaskSearch().execute(query);
    }

    private class TaskSearch extends AsyncTask<String, Void, List<LocationItem>> {
        private String error;

        @Override protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }

        @Override
        protected List<LocationItem> doInBackground(String... params) {
            String q = params[0];
            String urlS = "https://api.weatherapi.com/v1/search.json?key="
                    + API_KEY + "&q=" + q;
            try {
                URL url = new URL(urlS);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                c.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                JSONArray arr = new JSONArray(sb.toString());
                List<LocationItem> list = new ArrayList<>();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    list.add(new LocationItem(
                            o.optInt("id"),
                            o.optString("name"),
                            o.optString("region"),
                            o.optString("country"),
                            o.optDouble("lat"),
                            o.optDouble("lon"),
                            o.optString("url")
                    ));
                }
                return list;
            } catch (Exception e) {
                error = e.getMessage();
                return null;
            }
        }

        @Override protected void onPostExecute(List<LocationItem> res) {
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