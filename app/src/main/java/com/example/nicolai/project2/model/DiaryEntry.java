package com.example.nicolai.project2.model;

import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class DiaryEntry {
    private long id;
    private String title;
    private String description;
    private LatLng location;
    private Date date;
    private ArrayList<URL> links;
    private long trip_id;

    public DiaryEntry(long id, String title, String description, LatLng location, long trip_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.trip_id = trip_id;
    }


    public long getId() {
        return id;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getTitle() {
        return title;
    }
}
