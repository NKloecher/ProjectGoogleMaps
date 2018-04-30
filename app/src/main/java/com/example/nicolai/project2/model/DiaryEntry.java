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
    private ArrayList<URL> links; //maybe not needed, just query db--- need to make db first
    private long trip_id;
        //TODO Still need to figure out what to do about the weblinks --?
    public DiaryEntry(long id, String title, String description, LatLng location,Date date, long trip_id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.trip_id = trip_id;
    }

    public String getDescription() {
        return description;
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

    @Override
    public String toString() {
        String s = title + " " + date + " " + location + "\n" +
            description + "\n";

        if (links != null) {
            for (URL link : links) {
                s += link.toString() + "\n";
            }
        }

        return s;
    }
}
