package com.example.nicolai.project2.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolai.project2.model.Trip;
import com.example.nicolai.project2.storage.TripStorage;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.takisoft.datetimepicker.DatePickerDialog;

import com.example.nicolai.project2.R;

import java.util.Calendar;
import java.util.Date;

public class AddTripActivity extends AppCompatActivity {


    public static final String TRIP_EXTRA = "TRIP_EXTRA";
    DatePickerDialog dpd;
    Trip trip = new Trip();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        Calendar cal = Calendar.getInstance();
        dpd = new DatePickerDialog(this, null, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
    }

    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                TextView locationTxt = findViewById(R.id.locationTxt);
                locationTxt.setText(place.getName());
                trip.location = place.getLatLng();
            } else {
                Log.d("ERROR", "results error: " + resultCode);
            }
        }
    }

    public void onSetLocationClick(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onStartDateClick(View view) {
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.takisoft.datetimepicker.widget.DatePicker view, int year, int month, int dayOfMonth) {
                Date startDate = new Date(year, month, dayOfMonth);
                TextView startDateTxt = findViewById(R.id.startDateTxt);

                startDateTxt.setText(year + "/" + month + "/" + dayOfMonth);
                trip.startDate = startDate;
            }
        });
        dpd.show();
    }

    public void onEndDateClick(View view) {
        dpd.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.takisoft.datetimepicker.widget.DatePicker view, int year, int month, int dayOfMonth) {
                Date endDate = new Date(year, month, dayOfMonth);
                TextView endDateTxt = findViewById(R.id.endDateTxt);

                endDateTxt.setText(year + "/" + month + "/" + dayOfMonth);
                trip.endDate = endDate;
            }
        });
        dpd.show();
    }

    public void onSaveClick(View view) {
        new SaveTripAsyncTask().execute();
    }

    class SaveTripAsyncTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected void onPreExecute() {
            String title = ((TextView)findViewById(R.id.title)).getText().toString();
            String description = ((TextView)findViewById(R.id.desc)).getText().toString();

            if (title.isEmpty()) {
                throw new Error("no title");
            }
            if (description.isEmpty()) {
                throw new Error("no desc");
            }
            if (trip.location == null) {
                throw new Error("no location");
            }
            if (trip.startDate == null) {
                throw new Error("no start date");
            }
            if (trip.endDate == null) {
                throw new Error("no end date");
            }

            trip.title = title;
            trip.description =  description;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            TripStorage storage = TripStorage.getInstance(AddTripActivity.this);
            return storage.insertTrip(trip.title, trip.description, trip.location, trip.startDate, trip.endDate);
        }

        @Override
        protected void onPostExecute(Long tripId) {
            Intent i = new Intent();
            i.putExtra(TRIP_EXTRA, tripId);
            setResult(RESULT_OK, i);
            finish();
        }
    }
}
