package com.jofiagtech.earthquake.model;

public class EarthQuake {
    private String place;
    private double magnitude;
    private long time;
    private String detailsLink;
    private String type;
    private double latitude;
    private double longitude;

    public EarthQuake() {
    }

    public EarthQuake(String place, double magnitude, long time, String detailsLink, String type, double latitude, double longitude) {
        this.place = place;
        this.magnitude = magnitude;
        this.time = time;
        this.detailsLink = detailsLink;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDetailsLink() {
        return detailsLink;
    }

    public void setDetailsLink(String detailsLink) {
        this.detailsLink = detailsLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
