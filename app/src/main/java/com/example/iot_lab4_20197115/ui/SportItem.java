package com.example.iot_lab4_20197115.ui;

public class SportItem {
    public final String match;
    public final String tournament;
    public final String start;
    public final String stadium;
    public final String country;
    public final String region;

    public SportItem(String match, String tournament, String start,
                     String stadium, String country, String region) {
        this.match = match;
        this.tournament = tournament;
        this.start = start;
        this.stadium = stadium;
        this.country = country;
        this.region = region;
    }
}