package com.example.nicolai.project2.Fragments;

import android.content.Intent;
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
import com.example.nicolai.project2.activities.AddDiaryEntryActivity;
import com.example.nicolai.project2.activities.DiaryEntryActivity;
import com.example.nicolai.project2.storage.DiaryEntryStorage;

import java.text.DateFormat;

public class DiaryEntryListFragment extends android.support.v4.app.Fragment {

    private long trip_id;
    private SimpleCursorAdapter adapter;

    public DiaryEntryListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip_id = getArguments().getLong("TRIP_ID",-1);
        new GetDiaryEntriesAsyncTask().execute();
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

    public void runAsync(long trip_id){
        new GetDiaryEntriesAsyncTask().execute();
    }

    class GetDiaryEntriesAsyncTask extends AsyncTask<Void,Void,DiaryEntryStorage.DiaryEntryWrapper>{

        private final long CEST_CORRECTION = 7200 * 1000;
        private DiaryEntryStorage storage;

        public GetDiaryEntriesAsyncTask(){}

        @Override
        protected DiaryEntryStorage.DiaryEntryWrapper doInBackground(Void... voids) {
            storage = DiaryEntryStorage.getInstance(getContext());
            return storage.getAll(trip_id);
        }

        @Override
        protected void onPostExecute(DiaryEntryStorage.DiaryEntryWrapper diaryEntryWrapper) {
            //move this to background thread?
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
                registerForContextMenu(list);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getContext(), AddDiaryEntryActivity.class);
                        intent.putExtra(AddDiaryEntryActivity.ENTRY_ID, id);
                        intent.putExtra(AddDiaryEntryActivity.TRIP_ID, trip_id);
                        getActivity().startActivityForResult(intent, DiaryEntryActivity.UPDATE_ENTRY_REQUEST);
                    }
                });
            } else {
                adapter.changeCursor(diaryEntryWrapper);
            }
            if (adapter.getViewBinder() == null){
                adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                        if (cursor.getColumnIndex(DiaryEntryStorage.DATE) == columnIndex){
                            String t = cursor.getString(columnIndex);
                            TextView textView = (TextView) view;
                            textView.setText(t);
//                            long dateTime = Long.parseLong(cursor.getString(columnIndex));
//                            long actualDate = dateTime + CEST_CORRECTION; //correction for GMT -> CEST
//                            textView.setText(DateFormat.getDateInstance(DateFormat.LONG).format(actualDate));
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetDiaryEntriesAsyncTask().execute();
            Toast.makeText(getContext(), "Notatet er blevet slettet", Toast.LENGTH_LONG).show();
        }
    }
}
