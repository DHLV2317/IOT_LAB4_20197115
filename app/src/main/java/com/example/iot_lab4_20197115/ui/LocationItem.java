package com.example.iot_lab4_20197115.ui;

public class LocationItem {
    private int id;
    private String name;
    private String region;
    private String country;
    private double lat;
    private double lon;
    private String url;

    public LocationItem(int id, String name, String region, String country, double lat, double lon, String url) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
        this.url = url;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRegion() { return region; }
    public String getCountry() { return country; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public String getUrl() { return url; }
}