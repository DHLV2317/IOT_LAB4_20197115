package com.example.iot_lab4_20197115.ui;

public class ForecastItem {
    public final String date;
    public final String condition;
    public final double maxC;
    public final double minC;
    public final double precipMm;
    public final double maxWindKph;

    public ForecastItem(String date, String condition, double maxC, double minC, double precipMm, double maxWindKph) {
        this.date = date;
        this.condition = condition;
        this.maxC = maxC;
        this.minC = minC;
        this.precipMm = precipMm;
        this.maxWindKph = maxWindKph;
    }
}