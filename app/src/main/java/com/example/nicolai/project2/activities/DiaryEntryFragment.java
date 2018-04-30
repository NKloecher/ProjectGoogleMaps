package com.example.nicolai.project2.activities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.storage.DiaryEntryStorage;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DiaryEntryFragment extends android.support.v4.app.Fragment {

    private long trip_id;
    private SimpleCursorAdapter adapter;

    public DiaryEntryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip_id = getArguments().getLong("TRIP_ID",-1);
        new getDiaryEntriesAsyncTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (container != null){
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.diary_entry_fragment, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = new MenuInflater(getContext());
        inflater.inflate(R.menu.diary_entry_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.context_delete_entry:
                new deleteDiaryEntryAsyncTask(info.id).execute();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    class getDiaryEntriesAsyncTask extends AsyncTask<Void,Void,DiaryEntryStorage.DiaryEntryWrapper>{

        private final long CEST_CORRECTION = 7200 * 1000;
        private DiaryEntryStorage storage;

        @Override
        protected DiaryEntryStorage.DiaryEntryWrapper doInBackground(Void... voids) {
            storage = DiaryEntryStorage.getInstance(getContext());
            return storage.getAll(trip_id);
        }

        @Override
        protected void onPostExecute(DiaryEntryStorage.DiaryEntryWrapper diaryEntryWrapper) {
            //todo move this to background thread?
            View view = getView();
            if (adapter == null){
                adapter = new SimpleCursorAdapter(
                        getContext(),
                        android.R.layout.simple_list_item_2,
                        diaryEntryWrapper,
                        new String[] {DiaryEntryStorage.TITLE, DiaryEntryStorage.DATE},
                        new int[] {android.R.id.text1, android.R.id.text2},
                        0);
                ListView list = view.findViewById(R.id.diary_fragment_list);
                list.setAdapter(adapter);
                registerForContextMenu(list); //todo clickListener too? or maybe just add all to contextmenu
            } else {
                adapter.changeCursor(diaryEntryWrapper);
            }
            if (adapter.getViewBinder() == null){
                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (cursor.getColumnIndex(DiaryEntryStorage.DATE) == columnIndex){
                            long dateTime = Long.parseLong(cursor.getString(columnIndex));
                            long actualDate = dateTime + CEST_CORRECTION; //correction for GMT -> CEST
                            TextView textView = (TextView) view;
                            textView.setText(DateFormat.getDateInstance(DateFormat.LONG).format(actualDate));
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private class deleteDiaryEntryAsyncTask extends AsyncTask<Void,Void,Void> {

        private DiaryEntryStorage storage;
        long id;

        deleteDiaryEntryAsyncTask(long id){
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            storage = DiaryEntryStorage.getInstance(getContext());
            storage.remove(id);
            new getDiaryEntriesAsyncTask().execute();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getContext(), "Notatet er blevet slettet", Toast.LENGTH_LONG).show();
        }
    }
}
