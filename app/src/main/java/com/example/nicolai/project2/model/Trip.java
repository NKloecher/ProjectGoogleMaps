package com.example.nicolai.project2.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Trip {
    public long id;
    public String title;
    public String description;
    public LatLng location;
    public Date startDate;
    public Date endDate;

    public Trip(long id, String title, String description, LatLng location, Date startDate, Date endDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public Trip() {

    }



    @Override
    public String toString() {
        return title + ", " + location;
    }
}
