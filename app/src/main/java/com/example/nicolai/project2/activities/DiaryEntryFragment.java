package com.example.nicolai.project2.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.storage.DiaryEntryStorage;

public class DiaryEntryFragment extends android.support.v4.app.Fragment {

    private long trip_id;
    private SimpleCursorAdapter adapter;

    public DiaryEntryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip_id = getArguments().getLong("TRIP_ID");
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

    class getDiaryEntriesAsyncTask extends AsyncTask<Void,Void,DiaryEntryStorage.DiaryEntryWrapper>{

        private DiaryEntryStorage storage;

        @Override
        protected DiaryEntryStorage.DiaryEntryWrapper doInBackground(Void... voids) {
            storage = DiaryEntryStorage.getInstance(getContext());
            return storage.getAll(trip_id);
        }

        @Override
        protected void onPostExecute(DiaryEntryStorage.DiaryEntryWrapper diaryEntryWrapper) {
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
            } else {
                adapter.changeCursor(diaryEntryWrapper);
            }
        }
    }
}
