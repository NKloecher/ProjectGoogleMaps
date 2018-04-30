package com.example.nicolai.project2.model;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDate;
import java.util.Date;

public class Trip {
    public long id;
    public String title;
    public String description;
    public LatLng location;
    public LocalDate startDate;
    public Date endDate;

    public Trip(long id, String title, String description, LatLng location, LocalDate startDate, Date endDate) {
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
