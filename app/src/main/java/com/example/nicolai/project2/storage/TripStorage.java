package com.example.nicolai.project2.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.nicolai.project2.model.Trip;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class TripStorage {
    public static final String TABLE_NAME = "TRIP_LIST";
    public static final String _id = "_id";
    public static final String LOCATION = "LOCATION";
    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";

    private static TripStorage instance;
    private static MapsOpenHelper openHelper;

    public static TripStorage getInstance(Context context) {
        if (instance == null) instance = new TripStorage(context);
        return instance;
    }

    private TripStorage(Context context) {
        openHelper = MapsOpenHelper.getInstance(context);
    }

    public TripCursorWrapper getAll() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return new TripCursorWrapper(db.query(TABLE_NAME, new String[] {_id, TITLE, DESCRIPTION, LOCATION, START_DATE, END_DATE}, null, null, null, null, null, null));
    }

    public Trip get(long id){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        TripCursorWrapper cursor = new TripCursorWrapper(db.query(TABLE_NAME, new String[]{_id, TITLE, DESCRIPTION, LOCATION,START_DATE,END_DATE},
                "_id=?", new String[] {Long.toString(id)}, null, null, null, null));
        cursor.moveToNext();
        return cursor.get();
    }

    public long insertTrip(String title, String description, LatLng location, Date startDate, Date endDate) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TITLE, title);
        values.put(DESCRIPTION, description);
        values.put(LOCATION, location.latitude + "," + location.longitude);
        values.put(START_DATE, startDate.getTime());
        values.put(END_DATE, endDate.getTime());

        return db.insert(TABLE_NAME, null, values);
    }

    public class TripCursorWrapper extends CursorWrapper {
        public TripCursorWrapper(Cursor cursor) {
            super(cursor);
        }

        public Trip get() {
            if (isBeforeFirst() || isAfterLast()) return null;

            String[] ls = getString(getColumnIndex(LOCATION)).split(",");
            LatLng location = new LatLng(Double.parseDouble(ls[0]), Double.parseDouble(ls[1]));
            Date startDate = new Date(getInt(getColumnIndex(START_DATE)));
            Date endDate = new Date(getInt(getColumnIndex(END_DATE)));

            return new Trip(getInt(getColumnIndex(_id)),getString(getColumnIndex(TITLE)), getString(getColumnIndex(DESCRIPTION)), location, startDate, endDate);
        }
    }
}
