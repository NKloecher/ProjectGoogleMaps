package com.example.nicolai.project2.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.model.Trip;
import com.example.nicolai.project2.storage.TripStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplicationContext().deleteDatabase("TRIP_LIST");
        new InsertTestDataAsyncTask().execute();
    }


    class OnMapReady implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            new GetTripsAsyncTask().execute();
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater()));
            map.setOnInfoWindowClickListener(this);
        }
        @Override
        public void onInfoWindowClick(Marker marker) {
//            nicolaisTestMetode(marker);

        }
    }

    public void nicolaisTestMetode(Marker marker){
        Intent intent = new Intent(MainActivity.this, DiaryEntryActivity.class);
        Trip trip = (Trip) marker.getTag();
        intent.putExtra(DiaryEntryActivity.TRIP_ID, trip.id);
        startActivity(intent);
    }



    class GetTripsAsyncTask extends AsyncTask<Void, Void,Void> {

        TripStorage tripStorage;

        @Override
        protected Void doInBackground(Void... voids) {
            tripStorage = TripStorage.getInstance(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            TripStorage.TripCursorWrapper cursor = tripStorage.getAll();
            while (cursor.moveToNext()) {
                Trip trip = cursor.get();
                Marker m = map.addMarker(new MarkerOptions().position(trip.location).title(trip.title).snippet(trip.description));
                m.setTag(trip);
                builder.include(m.getPosition());
            }

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 170));
        }
    }

    class InsertTestDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private TripStorage storage;

        @Override
        protected Void doInBackground(Void... voids) {
            storage =  TripStorage.getInstance(MainActivity.this);

            storage.insertTrip(
                    "Moskva",
                    "Sommeren 2017",
                    new LatLng(55.751244, 37.618423),
                    new Date(1496275200),
                    new Date(1502275200)
            );
            storage.insertTrip(
                    "LONGEVITY TEST",
                    "THIS IS A TEST TO SEE HOW LONG THE SNIPPET" +
                            "EXPANDS UNDE!!!!R PRESSURE AND IF IT EVER JUST CONCATENATES THE CONTENTS," +
                            "I THINK JUST A FEW MORE LINES SHOULD DO IT!",
                    new LatLng(50,50),
                    new Date(0),
                    new Date(0)
            );
            storage.insertTrip(
                    "London",
                    "test description",
                    new LatLng(51.509865, -0.118092),
                    new Date(new Date(1970,1,1).getTime()),
                    new Date(0)); //TODO decide on date format?
            storage.insertTrip(
                    "København",
                    "also a test description",
                    new LatLng(55.676098, 12.568337),
                    new Date(new Date().getTime()),
                    new Date(new Date().getTime()));

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            TripStorage.TripCursorWrapper cursor = storage.getAll();
//            while (cursor.moveToNext()) Log.d("debug", cursor.get().toString() + "-t");
//            cursor.moveToFirst(); //til at teste inserts
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(new OnMapReady());
        }
    }
}
