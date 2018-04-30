package com.example.nicolai.project2.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MapsOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "TRIP_LIST";
    private static final int VERSION = 1;
    private static MapsOpenHelper instance;

    public static MapsOpenHelper getInstance(Context context){
        if (instance == null){
            instance = new MapsOpenHelper(context);
        }
        return instance;
    }

    private MapsOpenHelper(Context context){
        super(context.getApplicationContext(),DB_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDB(db, 0, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDB(db, oldVersion,newVersion);
    }

    private void updateDB(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < 1){
            db.execSQL("CREATE TABLE TRIP_LIST (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "LOCATION TEXT," +
                    "START_DATE INT," +
                    "END_DATE INT," +
                    "TITLE TEXT," +
                    "DESCRIPTION TEXT)");

            db.execSQL("CREATE TABLE DIARY_ENTRY (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "TITLE TEXT," +
                    "DESCRIPTION TEXT," +
                    "DATE TEXT," +
                    "LOCATION TEXT," +
                    "TRIP_ID INT," +
                    "FOREIGN KEY (TRIP_ID) REFERENCES TRIP_LIST(_id))");

            db.execSQL("CREATE TABLE DIARY_LINKS (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "URL TEXT," +
                    "DIARY_ID INT," +
                    "FOREIGN KEY (DIARY_ID) REFERENCES DIARY_ENTRY(_id))");


        }
    }
}
