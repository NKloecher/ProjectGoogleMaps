package com.example.nicolai.project2.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import com.example.nicolai.project2.model.DiaryEntry;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class DiaryEntryStorage {
    public static final String TABLE_NAME = "DIARY_ENTRY";
    public static final String _id = "_id";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String LOCATION = "LOCATION";
    public static final String DATE = "DATE";
    public static final String TRIP_ID = "TRIP_ID";

    private static DiaryEntryStorage instance;
    private static MapsOpenHelper openHelper;

    public static DiaryEntryStorage getInstance(Context context) {
        if (instance == null) instance = new DiaryEntryStorage(context);
        return instance;
    }

    private DiaryEntryStorage(Context context){
        openHelper = MapsOpenHelper.getInstance(context);
    }

    public long insert(String title, String description, LatLng location, Date date, long trip_id){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, title);
        values.put(DESCRIPTION, description);
        values.put(LOCATION, location.latitude + ", " + location.longitude);
        values.put(DATE, date.getTime());
        values.put(TRIP_ID, trip_id);
        return db.insert(TABLE_NAME,null,values);
    }


    public long update(DiaryEntry diaryEntry, ContentValues values) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db.update(TABLE_NAME,values, "_id=?", new String[] {Long.toString(diaryEntry.getId())});
    }

    public long remove(DiaryEntry diaryEntry) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db.delete(TABLE_NAME, "_id=?", new String[] {Long.toString(diaryEntry.getId())});
    }

    public DiaryEntry get(long id){
        SQLiteDatabase db = openHelper.getReadableDatabase();
        DiaryEntryWrapper cursor = new DiaryEntryWrapper(db.query(TABLE_NAME, new String[]{_id, TITLE, DESCRIPTION, LOCATION,DATE,TRIP_ID},
                "_id=?", new String[] {Long.toString(id)}, null, null, null, null));
        cursor.moveToNext();
        return cursor.get();
    }

    public DiaryEntryWrapper getAll(long trip_id) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return new DiaryEntryWrapper(db.query(TABLE_NAME, new String[]{_id, TITLE, DESCRIPTION, LOCATION,DATE,TRIP_ID},
                "trip_id=?", new String[]{Long.toString(trip_id)}, null, null, null, null));
    }

    public class DiaryEntryWrapper extends CursorWrapper{

        public DiaryEntryWrapper(Cursor cursor) {  super(cursor);    }

        public DiaryEntry get(){
            if (isBeforeFirst() || isAfterLast()) return null;

            String[] location = getString(getColumnIndex(LOCATION)).split(" ");
            LatLng latLng = new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));

            return new DiaryEntry(
                    getInt(getColumnIndex(_id)),
                    getString(getColumnIndex(TITLE)),
                    getString(getColumnIndex(DESCRIPTION)),
                    latLng,
                    getInt(getColumnIndex(TRIP_ID))
            );
        }

    }
}
