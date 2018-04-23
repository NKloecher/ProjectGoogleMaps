package com.example.nicolai.project2;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.nicolai.project2.storage.DiaryEntryStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DiaryEntryActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public static final String TRIP_ID = "TRIP_ID"; //trip id for diary selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        new getDiaryEntriesAsyncTask().execute();

    }

    public class getDiaryEntriesAsyncTask extends AsyncTask<Void,Void,Void>{
        private DiaryEntryStorage storage;
        @Override
        protected Void doInBackground(Void... voids) {
            storage = DiaryEntryStorage.getInstance(DiaryEntryActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            DiaryEntryStorage.DiaryEntryWrapper cursor = storage.getAll(getIntent().getLongExtra(TRIP_ID,-1));
            while (cursor.moveToNext()){
                LatLng position = cursor.get().getLocation();
                String title = cursor.get().getTitle();
                mMap.addMarker(new MarkerOptions().position(position)).setTitle(title);
            }
        }
    }
}
