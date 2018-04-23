package com.example.nicolai.project2.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.model.Trip;
import com.example.nicolai.project2.storage.TripStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {

    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new InsertTestDataAsyncTask().execute();
    }

    class OnMapReady implements OnMapReadyCallback {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            new GetTripsAsyncTask().execute();
        }
    }

    class GetTripsAsyncTask extends AsyncTask<Void, Void, TripStorage.TripCursorWrapper> {

        @Override
        protected TripStorage.TripCursorWrapper doInBackground(Void... voids) {
            return TripStorage.getInstance(MainActivity.this).getAll();
        }

        @Override
        protected void onPostExecute(TripStorage.TripCursorWrapper cursor) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            while (cursor.moveToNext()) {
                Trip trip = cursor.get();
                Marker m = map.addMarker(new MarkerOptions().position(trip.location).title(trip.title + "\n" + trip.description));
                builder.include(m.getPosition());
                cursor.moveToNext();
            }

            map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 170));
        }
    }

    class InsertTestDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            /*TripStorage.getInstance(MainActivity.this).insertTrip(
                    "London",
                    "test description",
                    new LatLng(51.509865, -0.118092),
                    new Date(1970,1,1),
                    new Date(1970,1,1));
            TripStorage.getInstance(MainActivity.this).insertTrip(
                    "København",
                    "test description",
                    new LatLng(55.676098, 12.568337),
                    new Date(1970,1,1),
                    new Date(1970,1,1));*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(new OnMapReady());
        }
    }
}
