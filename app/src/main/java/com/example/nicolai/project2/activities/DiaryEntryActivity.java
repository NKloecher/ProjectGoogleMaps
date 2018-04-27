package com.example.nicolai.project2.activities;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.storage.DiaryEntryStorage;
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
    //TODO 27/04 -> ContextMenu onOptionsItemSelected
    //TODO setMapToolbarEnabled(boolean) -> for at fjerne original funktioner

    //TODO Ambitious -> Make both a listview and the map-view -> can change via toolbar context menu

    public static final String TRIP_TITLE = "TRIP_TITLE";
    public static final String TRIP_ID = "TRIP_ID"; //trip id for diary selection
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(getIntent().getStringExtra(TRIP_TITLE));
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.diary_map);
        mapFragment.getMapAsync(this);
    }

    public void changeToMap(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        SupportMapFragment fragment = new SupportMapFragment();
        transaction.add(R.id.diary_map, fragment, "");
        transaction.commit();
        mapFragment = fragment;
        mapFragment.getMapAsync(this);
    }

    public void changeToList(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        DiaryEntryFragment fragment = new DiaryEntryFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(TRIP_ID, getIntent().getLongExtra(TRIP_ID,-1));
        fragment.setArguments(bundle);

        transaction.add(R.id.diary_map, fragment, "");
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.diary_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem test = menu.findItem(R.id.diary_entry_menu_delete_action);
        String deleteActionTitle = getResources().getString(R.string.delete_current_trip,
        getIntent().getStringExtra(TRIP_TITLE));
        test.setTitle(deleteActionTitle);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.diary_entry_menu_change_to_listview_action:
                changeToList();
                return true;
            case R.id.diary_entry_menu_change_to_map_action:
                changeToMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
