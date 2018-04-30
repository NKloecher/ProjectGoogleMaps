package com.example.nicolai.project2.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.nicolai.project2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.zip.Inflater;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final String TITLE = "TITLE";
    private final String DESCRIPTION = "DESCRIPTION";

    private View contents;
    private LayoutInflater inflater;

    public CustomInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }



    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }


    @Override
    public View getInfoContents(Marker marker) {
        if (contents == null){
            contents = inflater.inflate(R.layout.trip_custom_info_window, null);
        }

        String title = marker.getTitle();
        TextView titleUi = contents.findViewById(R.id.title);
        if (title != null){
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK),0,titleText.length(),0);
            titleUi.setText(titleText);
        }

        //TODO Style contents ------
        String desc = marker.getSnippet();
        TextView descUi = contents.findViewById(R.id.snippet);
        if (desc != null){
            SpannableString descText = new SpannableString(desc);
            descText.setSpan(new ForegroundColorSpan(Color.BLACK),0,descText.length(),0);
            descUi.setText(descText);
        }

        String diaryEntries;


        return contents;
    }

}
