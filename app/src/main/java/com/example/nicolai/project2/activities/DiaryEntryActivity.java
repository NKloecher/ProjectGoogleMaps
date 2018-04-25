package com.example.nicolai.project2.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.model.DiaryEntry;
import com.example.nicolai.project2.storage.DiaryEntryStorage;
import com.example.nicolai.project2.storage.TripStorage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DiaryEntryActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //TODO Decide -> Query db for info on Trip? or Just pass along id?
    //TODO cont -> need id for getAll(trip_id) in DiaryEntryStorage. Other info needed?
    //TODO cont -> might need title etc. when action/toolbar comes into play

    //TODO Decide -> Size of desc for DiaryEntries might be too big for snippet
    //TODO cont -> Make a custom infoView to handle it? or Other solution?

    private String trip;
    public static final String TRIP_ID = "TRIP_ID"; //trip id for diary selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);
        Log.d("debug", Long.toString(getIntent().getLongExtra(TRIP_ID,-1)));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new getDiaryEntriesAsyncTask().execute();

    }

    public class getDiaryEntriesAsyncTask extends AsyncTask<Void,Void,DiaryEntryStorage.DiaryEntryWrapper>{
        private DiaryEntryStorage storage;
        @Override
        protected DiaryEntryStorage.DiaryEntryWrapper doInBackground(Void... voids) {
            storage = DiaryEntryStorage.getInstance(DiaryEntryActivity.this);
            return storage.getAll(getIntent().getLongExtra(TRIP_ID,-1));
        }

        @Override
        protected void onPostExecute(DiaryEntryStorage.DiaryEntryWrapper cursor) {
            while (cursor.moveToNext()){
                LatLng position = cursor.get().getLocation();
                String title = cursor.get().getTitle();
                mMap.addMarker(new MarkerOptions().position(position)).setTitle(title);
            }
        }
    }
}
