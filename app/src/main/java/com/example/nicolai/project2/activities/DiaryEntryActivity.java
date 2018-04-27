package com.example.nicolai.project2.activities;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.model.DiaryEntry;
import com.example.nicolai.project2.storage.DiaryEntryStorage;
import com.example.nicolai.project2.storage.TripStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DiaryEntryActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    //TODO Decide -> Query db for info on Trip? or Just pass along id?
    //TODO cont -> need id for getAll(trip_id) in DiaryEntryStorage. Other info needed?
    //TODO cont -> might need title etc. when action/toolbar comes into play

    //TODO Decide -> Size of desc for DiaryEntries might be too big for snippet
    //TODO cont -> Make a custom infoView to handle it? or Other solution?

    //TODO style toolbar + ContextMenu (delete trip) -> Can't context menu infowindow
    //TODO setMapToolbarEnabled(boolean) -> for at fjerne original funktioner

    //TODO Ambitious -> Make both a listview and the map-view -> can change via toolbar context menu

    public static final String TRIP_TITLE = "TRIP_TITLE";
    public static final String TRIP_ID = "TRIP_ID"; //trip id for diary selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(getIntent().getStringExtra(TRIP_TITLE));
        supportActionBar.setDisplayHomeAsUpEnabled(true);


        Log.d("debug", Long.toString(getIntent().getLongExtra(TRIP_ID,-1)));
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.diary_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diary_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            while (cursor.moveToNext()){
                LatLng position = cursor.get().getLocation();
                String title = cursor.get().getTitle();
                String description = cursor.get().getDescription();

                Marker marker = mMap.addMarker(new MarkerOptions().position(position).title(title).snippet(description));
                builder.include(marker.getPosition());
            }
            if (!cursor.isBeforeFirst()){
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),300));
            }
        }
    }
}
