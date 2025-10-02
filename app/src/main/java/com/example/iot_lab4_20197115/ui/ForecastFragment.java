package com.example.iot_lab4_20197115.ui;

import android.app.AlertDialog;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class ForecastFragment extends Fragment implements SensorEventListener {

    private TextView tHeader, empty;
    private EditText inDays, inLocationId;
    private Button btnLoad;
    private ProgressBar progress;
    private RecyclerView recycler;
    private ForecastAdapter adapter;

    private final List<ForecastItem> forecastList = new ArrayList<>();

    // Datos que pueden llegar desde LocationFragment
    private int locationId = 0;
    private String locationName = "";

    // API Key
    private static final String API_KEY = "becac55206564c98b87224707250110";

    // Sensor (shake-to-delete)
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD_G = 2.2f;
    private static final long SHAKE_COOLDOWN_MS = 1500L;
    private long lastShakeTime = 0L;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        tHeader      = v.findViewById(R.id.tHeader);
        inDays       = v.findViewById(R.id.inDays);
        inLocationId = v.findViewById(R.id.inLocationId);
        btnLoad      = v.findViewById(R.id.btnLoad);
        progress     = v.findViewById(R.id.progress);
        empty        = v.findViewById(R.id.empty);
        recycler     = v.findViewById(R.id.recycler);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ForecastAdapter();
        recycler.setAdapter(adapter);

        // Args desde Locations (si existen, rellenan los campos)
        Bundle args = getArguments();
        if (args != null) {
            locationId   = args.getInt("locationId", 0);
            locationName = args.getString("locationName", "");
        }
        if (locationId > 0) inLocationId.setText(String.valueOf(locationId));
        tHeader.setText(locationName.isEmpty()
                ? "Pronóstico"
                : "Pronóstico de: " + locationName + " (id " + locationId + ")");

        // Por comodidad, prellenar 7 días
        inDays.setText("7");

        btnLoad.setOnClickListener(x -> {
            // 1) validar ID
            String sId = inLocationId.getText().toString().trim();
            if (TextUtils.isEmpty(sId)) {
                inLocationId.setError("Ingresa el ID de locación");
                return;
            }
            int id;
            try { id = Integer.parseInt(sId); }
            catch (NumberFormatException e) {
                inLocationId.setError("ID inválido");
                return;
            }
            // 2) validar días (1..14)
            String sDays = inDays.getText().toString().trim();
            if (TextUtils.isEmpty(sDays)) {
                inDays.setError("Requerido (1-14)");
                return;
            }
            int days;
            try { days = Integer.parseInt(sDays); }
            catch (NumberFormatException e) {
                inDays.setError("Número inválido");
                return;
            }
            if (days < 1 || days > 14) {
                inDays.setError("Rango 1 a 14");
                return;
            }

            // Guardamos el id actual por si llegó vacío en args
            locationId = id;
            new TaskForecast().execute(id, days);
        });

        sensorManager = (SensorManager) requireContext()
                .getSystemService(android.content.Context.SENSOR_SERVICE);
        if (sensorManager != null) accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    // ===== Ciclo de vida sensor =====
    @Override public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }
    @Override public void onPause() {
        super.onPause();
        if (sensorManager != null) sensorManager.unregisterListener(this);
    }

    // ===== Shake-to-delete último item =====
    @Override
    public void onSensorChanged(SensorEvent e) {
        if (e.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
        if (forecastList.isEmpty()) return;

        float gX = e.values[0] / SensorManager.GRAVITY_EARTH;
        float gY = e.values[1] / SensorManager.GRAVITY_EARTH;
        float gZ = e.values[2] / SensorManager.GRAVITY_EARTH;
        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        long now = System.currentTimeMillis();
        if (gForce > SHAKE_THRESHOLD_G && now - lastShakeTime > SHAKE_COOLDOWN_MS) {
            lastShakeTime = now;
            showConfirmDialog();
        }
    }
    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    private void showConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmar acción")
                .setMessage("¿Deseas eliminar el último pronóstico obtenido?")
                .setPositiveButton("Sí", (d, w) -> {
                    if (!forecastList.isEmpty()) {
                        forecastList.remove(forecastList.size() - 1);
                        adapter.submit(new ArrayList<>(forecastList));
                        if (forecastList.isEmpty()) {
                            empty.setText(R.string.no_data);
                            empty.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ===== Llamada a la API: forecast por id + days (incluye hoy) =====
    private class TaskForecast extends AsyncTask<Integer, Void, List<ForecastItem>> {
        private String error;

        @Override protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }

        @Override
        protected List<ForecastItem> doInBackground(Integer... p) {
            int id = p[0];
            int days = p[1];

            String urlS = "https://api.weatherapi.com/v1/forecast.json?key="
                    + API_KEY + "&q=id:" + id + "&days=" + days;

            try {
                URL url = new URL(urlS);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                c.connect();

                int code = c.getResponseCode();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        code >= 200 && code < 300 ? c.getInputStream() : c.getErrorStream()
                ));
                StringBuilder sb = new StringBuilder();
                String line; while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                if (code < 200 || code >= 300) {
                    error = "HTTP " + code + ": " + sb.toString();
                    return null;
                }

                JSONObject root = new JSONObject(sb.toString());
                JSONArray daysArr = root.getJSONObject("forecast").getJSONArray("forecastday");

                List<ForecastItem> out = new ArrayList<>();
                for (int i = 0; i < daysArr.length(); i++) {
                    JSONObject d = daysArr.getJSONObject(i);
                    String date = d.optString("date"); // incluye hoy como primer elemento
                    JSONObject day = d.getJSONObject("day");
                    double maxC = day.optDouble("maxtemp_c");
                    double minC = day.optDouble("mintemp_c");
                    double precip = day.optDouble("totalprecip_mm");
                    double wind = day.optDouble("maxwind_kph");
                    String cond = day.getJSONObject("condition").optString("text");
                    out.add(new ForecastItem(date, cond, maxC, minC, precip, wind));
                }
                return out;

            } catch (Exception e) {
                error = e.getMessage();
                return null;
            }
        }

        @Override protected void onPostExecute(List<ForecastItem> res) {
            progress.setVisibility(View.GONE);
            if (res == null) {
                empty.setText("Error: " + (error == null ? "desconocido" : error));
                empty.setVisibility(View.VISIBLE);
                forecastList.clear();
                adapter.submit(new ArrayList<>());
            } else if (res.isEmpty()) {
                empty.setText(R.string.no_data);
                empty.setVisibility(View.VISIBLE);
                forecastList.clear();
                adapter.submit(new ArrayList<>());
            } else {
                empty.setVisibility(View.GONE);
                forecastList.clear();
                forecastList.addAll(res);
                adapter.submit(new ArrayList<>(forecastList));
            }
        }
    }
}