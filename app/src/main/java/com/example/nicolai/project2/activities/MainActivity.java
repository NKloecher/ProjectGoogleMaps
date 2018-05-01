package com.example.nicolai.project2.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.nicolai.project2.Fragments.CustomInfoWindowAdapter;
import com.example.nicolai.project2.R;
import com.example.nicolai.project2.model.Trip;
import com.example.nicolai.project2.storage.DiaryEntryStorage;
import com.example.nicolai.project2.storage.TripStorage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private GoogleMap map;
    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getApplicationContext().deleteDatabase("TRIP_LIST");
        new InsertTestDataAsyncTask().execute();
    }

    public void startDiaryEntryActivity(Marker marker){
        Intent intent = new Intent(MainActivity.this, DiaryEntryActivity.class);
        Trip trip = (Trip) marker.getTag();
        if (trip != null){
            intent.putExtra(DiaryEntryActivity.TRIP_ID, trip.id);
            intent.putExtra(DiaryEntryActivity.TRIP_TITLE, trip.title);
            startActivity(intent);
        }
    }

    private static final int ADD_ACTIVITY_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK) {
                long tripId = data.getLongExtra(AddTripActivity.TRIP_EXTRA, -1);

                if (tripId == -1) {
                    Log.d("ERROR", "no trip id");
                    return;
                }

                new AddTripAsyncTask(tripId).execute();
            }
        }
    }

    public void onAddTripClick(View view) {
        startActivityForResult(new Intent(this, AddTripActivity.class), ADD_ACTIVITY_REQUEST);
    }

    private void addTripToMap(Trip trip) {
        Resources resources = MainActivity.this.getResources();
        Bitmap icon = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_map_marker), 90, 90, false);
        float scale = resources.getDisplayMetrics().density;
        int textSize = (int) (15 * scale);
        Rect bounds = new Rect();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        paint.setShadowLayer(2f, 0f, 1f, Color.WHITE);
        paint.getTextBounds(trip.title, 0, trip.title.length(), bounds);

        int bitmapHeight = Math.max(icon.getHeight(),bounds.height());

        Bitmap bitmap = Bitmap.createBitmap(icon.getWidth()+bounds.width()*2+10, bitmapHeight, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);

        c.drawBitmap(icon, bounds.width(), 0, paint);
        c.drawText(trip.title, bounds.width()+icon.getWidth(), bitmapHeight-10, paint);

        Marker m = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap)).position(trip.location).title(trip.title).snippet(trip.description));
        m.setTag(trip);

        builder.include(m.getPosition());
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 170));
    }

    class InsertTestDataAsyncTask extends AsyncTask<Void, Void, Void> {

        private TripStorage storage;
        private DiaryEntryStorage diaryEntryStorage;

        @Override
        protected Void doInBackground(Void... voids) {
            storage =  TripStorage.getInstance(MainActivity.this);
            diaryEntryStorage = DiaryEntryStorage.getInstance(MainActivity.this);

            storage.insertTrip(
                    "Moskva",
                    "Sommeren 2014",
                    new LatLng(55.751244, 37.618423),
                    new Date(1496275200),
                    new Date(1502275200)
            );
            storage.insertTrip(
                    "LONGEVITY TEST",
                    "THIS IS A TEST TO SEE HOW LONG THE SNIPPET" +
                            "EXPANDS UNDE!!!!R PRESSURE AND IF IT EVER JUST CONCATENATES THE CONTENTS," +
                            "I THINK JUST A FEW MORE LINES SHOULD DO IT! TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST" +
                            " TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST",
                    new LatLng(50,50),
                    new Date(0),
                    new Date(0)
            );
            long london = storage.insertTrip(
                    "London",
                    "test description",
                    new LatLng(51.509865, -0.118092),
                    new Date(0),
                    new Date(0)); //TODO decide on date format?
            long kbh = storage.insertTrip(
                    "København",
                    "Rundrejse i Danmark 2017",
                    new LatLng(55.676098, 12.568337),
                    new Date(new Date().getTime()),
                    new Date(new Date().getTime()));

            // String title, String description, LatLng location, long trip_id
            diaryEntryStorage.insert(
                    "London, monday",
                    "We started out wonderfully at te B&B wtih ham and sausages",
                    new LatLng(51.509865, -0.118092),
                    new Date("1970/1/1"),
                    london);
            diaryEntryStorage.insert(
                    "London, Tueday",
                    "Buckingham palace trip. We saw the queen!!! And the soldiers and everything, it was truly a fantastic place." +
                            "Mark ate something that disagreed with his stomach, probably the clams we had for lunch, but he's okay again." +
                            "The skies might be grey but the people are so refreshing to talk to, they always have a smile for strangers passing by." +
                            "High hopes for the last day!",
                    new LatLng(51.501476, -0.140634),
                    new Date("1970/1/1"),
                    london);
            diaryEntryStorage.insert(
                    "London, last day",
                    "I felled a tear as we left, we will have to return again soon!",
                    new LatLng(51.509865, -0.128092),
                    new Date("1970/1/1"),
                    london);

            diaryEntryStorage.insert(
                    "Århus",
                    "We visited the beautiful cathedral and tons of other stuff. The people are very friendly" +
                            ", who knew!",
                    new LatLng(56.162939,10.203921),
                    new Date("2017/6/7"),
                    kbh);
            diaryEntryStorage.insert(
                    "Odense",
                    "Odense is awesome",
                    new LatLng(55.403756,10.402370),
                    new Date(1496959200000L),
                    kbh);
            diaryEntryStorage.insert(
                    "Slagelse",
                    "A quaint little town bustling with activity",
                    new LatLng(55.40276, 11.35459),
                    new Date(1497045600000L),
                    kbh);
            diaryEntryStorage.insert(
                    "København",
                    "Wunderschöne Koben-Hagen, das ist naturlich ein freuer stadt!",
                    new LatLng(55.676098, 12.568337),
                    new Date(1497132001000L),
                    kbh);



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

    class GetTripsAsyncTask extends AsyncTask<Void, Void,Void> {

        TripStorage tripStorage;

        @Override
        protected Void doInBackground(Void... voids) {
            tripStorage = TripStorage.getInstance(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            TripStorage.TripCursorWrapper cursor = tripStorage.getAll();
            while (cursor.moveToNext()) {
                Trip trip = cursor.get();
                addTripToMap(trip);
            }
        }
    }

    class OnMapReady implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            new GetTripsAsyncTask().execute();
            map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getLayoutInflater()));
            map.setOnInfoWindowClickListener(this);
        }
        @Override
        public void onInfoWindowClick(Marker marker) {
            startDiaryEntryActivity(marker);

        }

    }

    class AddTripAsyncTask extends AsyncTask<Void, Void, Trip> {
        private long tripId;
        public AddTripAsyncTask(long tripId) {
            this.tripId = tripId;
        }

        @Override
        protected Trip doInBackground(Void... voids) {
            TripStorage storage = TripStorage.getInstance(MainActivity.this);
            return storage.get(tripId);
        }

        @Override
        protected void onPostExecute(Trip trip) {
            addTripToMap(trip);
        }
    }
}
