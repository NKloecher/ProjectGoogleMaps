package com.example.nicolai.project2.activities;

import com.example.nicolai.project2.model.DiaryEntry;
import com.example.nicolai.project2.storage.DiaryEntryStorage;
import com.google.android.gms.maps.model.LatLng;
import com.takisoft.datetimepicker.DatePickerDialog;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolai.project2.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Calendar;
import java.util.Date;

public class AddDiaryEntryActivity extends AppCompatActivity {

    public static final String ENTRY_ID = "ENTRY_ID";
    DatePickerDialog dialog;

    private LatLng location;
    private Date date;
    private long trip_id;
    private long entry_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary_entry);

        entry_id = getIntent().getLongExtra(ENTRY_ID, -1);
        Log.d("debug", entry_id +": entry id");
        if (entry_id != -1){
            new FillTemplateAsyncTask(entry_id).execute();
        }

        Calendar cal = Calendar.getInstance();
        dialog = new DatePickerDialog(this, null, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                TextView locationTxt = findViewById(R.id.locationTxt);
                locationTxt.setText(place.getName());
                location = place.getLatLng();
            } else {
                Log.d("ERROR", "results error: " + resultCode);
            }
        }
    }

    public void onAddDateClick(View view) {
        dialog.setOnDateSetListener(new com.takisoft.datetimepicker.DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(com.takisoft.datetimepicker.widget.DatePicker view, int year, int month, int dayOfMonth) {
                date = new Date(year, month, dayOfMonth);
                TextView startDateTxt = findViewById(R.id.dateTxt);
                startDateTxt.setText(year + "/" + (month+1) + "/" + dayOfMonth); //Placeholders here
            }
        });
        dialog.show();
    }

    public void onSaveClick(View view) {
        new SaveEntryAsyncTask().execute();
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

    public void onSendAsMailClick(View view) {
        Intent i = new Intent(this, ContactGroupListActivity.class);
        i.putExtra(ContactGroupListActivity.DIARY_ENTRY_ID_EXTRA, entry_id);
        startActivity(i);
    }

    class FillTemplateAsyncTask extends AsyncTask<Void, Void,DiaryEntry>{
        long id;
        public FillTemplateAsyncTask(long id) {
            this.id = id;
        }

        @Override
        protected DiaryEntry doInBackground(Void... voids) {
            DiaryEntryStorage storage = DiaryEntryStorage.getInstance(AddDiaryEntryActivity.this);
            return storage.get(id);
        }

        @Override
        protected void onPostExecute(DiaryEntry entry) {
            final EditText titleText = findViewById(R.id.title);
            final EditText descText = findViewById(R.id.desc);
            final TextView locationText = findViewById(R.id.locationTxt);
            final TextView dateText = findViewById(R.id.dateTxt);

            titleText.setText(entry.getTitle());
            descText.setText(entry.getDescription());

//                locationText.setText(entry.getLocation().toString());
            locationText.setText(entry.getTitle());
            location = entry.getLocation();

            dateText.setText(String.format("%s/%s/%s", date.getYear()+1900,date.getMonth()+1,date.getDate()));
            date = entry.getDate();
            trip_id = entry.getTrip_id();
            //TODO dates..... so fucked.....
        }
    }

    private class SaveEntryAsyncTask extends AsyncTask<Void, Void, Long> {

        String title;
        String desc;


        @Override
        protected void onPreExecute() {
            EditText titleText = findViewById(R.id.title);
            title = titleText.getText().toString();

            EditText descText = findViewById(R.id.desc);
            desc = descText.getText().toString();

            if (title.isEmpty()){
                Toast.makeText(AddDiaryEntryActivity.this,"Please Enter Title", Toast.LENGTH_LONG).show();
                cancel(true);
            }
            else if (desc.isEmpty()){
                Toast.makeText(AddDiaryEntryActivity.this,"Please enter Description", Toast.LENGTH_LONG).show();
                cancel(true);
            }
            else if (location == null) {
                Toast.makeText(AddDiaryEntryActivity.this,"Please select a location", Toast.LENGTH_LONG).show();
                cancel(true);
            }
            else if (date == null){
                Toast.makeText(AddDiaryEntryActivity.this,"Please enter date", Toast.LENGTH_LONG).show();
                cancel(true);
            }
            else if (trip_id == -1){
                Toast.makeText(AddDiaryEntryActivity.this,"How did you even manage to get this error?!", Toast.LENGTH_LONG).show();
                cancel(true);
            }
        }

        @Override
        protected Long doInBackground(Void... voids) {
            DiaryEntryStorage storage = DiaryEntryStorage.getInstance(AddDiaryEntryActivity.this);
            long id = getIntent().getLongExtra(ENTRY_ID,-1);
            if (id != -1){
                return storage.update(id, title, desc,location,date);
            }
            else return storage.insert(title, desc, location,date,trip_id);
        }

        @Override
        protected void onPostExecute(Long id) {
            Intent intent = new Intent();
            intent.putExtra(ENTRY_ID, id);
            setResult(RESULT_OK, intent);
            finish(); //todo fails to update listview on update or insert?!
        }
    }
}
