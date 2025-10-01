package com.example.iot_lab4_20197115;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iot_lab4_20197115.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding b;

    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.btnIngresar.setOnClickListener(v -> {
            if (!hasInternet()) {
                new AlertDialog.Builder(this)
                        .setTitle("Sin conexión")
                        .setMessage("Necesitas Internet para continuar.\n¿Abrir Configuración de red?")
                        .setPositiveButton("Ir a Ajustes", (d, w) -> {
                            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            } else {
                startActivity(new Intent(this, com.example.iot_lab4_20197115.ui.AppActivity.class));
            }
        });
    }

    private boolean hasInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        var nw = cm.getActiveNetwork();
        if (nw == null) return false;
        var caps = cm.getNetworkCapabilities(nw);
        return caps != null && (
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }
}