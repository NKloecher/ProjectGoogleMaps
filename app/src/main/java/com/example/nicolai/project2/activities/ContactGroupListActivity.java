package com.example.nicolai.project2.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicolai.project2.R;
import com.example.nicolai.project2.model.DiaryEntry;
import com.example.nicolai.project2.storage.DiaryEntryStorage;

import java.util.ArrayList;
import java.util.Arrays;

public class ContactGroupListActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public static final String DIARY_ENTRY_ID_EXTRA = "DIARY_ENTRY_ID_EXTRA";
    private long diaryEntryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        diaryEntryId = getIntent().getLongExtra(DIARY_ENTRY_ID_EXTRA, 1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_group_list);

        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            showContacts();
        }
    }

    private void showContacts() {
            Cursor groupCursor = getContentResolver().query(
                    ContactsContract.Groups.CONTENT_URI,
                    new String[]{
                            ContactsContract.Groups._ID,
                            ContactsContract.Groups.TITLE
                    }, null, null, null
            );

        GroupCursorAdapter adapter = new GroupCursorAdapter(this, groupCursor, 0);
            ListView groupList = findViewById(R.id.group_list);
            groupList.setAdapter(adapter);
    }

    private void sendMailToGroup(long groupId) {
        if (diaryEntryId == -1) {
            throw new Error("NO DIARY ENTRY ID");
        }

        DiaryEntry diaryEntry = DiaryEntryStorage.getInstance(this).get(diaryEntryId);

        Cursor groupCursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Email._ID,
                        ContactsContract.CommonDataKinds.Email.DATA,

                },
                ContactsContract.CommonDataKinds.Email.LABEL + "=?",
                new String[] {Long.toString(groupId)},
                null
        );

        String[] emails = new String[groupCursor.getCount()];
        int i = 0;

        while (!groupCursor.isAfterLast()) {
            groupCursor.moveToNext();
            String email = groupCursor.getString(groupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            emails[i] = email;
            i++;
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, emails);
        intent.putExtra(Intent.EXTRA_SUBJECT, diaryEntry.getTitle());
        intent.putExtra(Intent.EXTRA_TEXT, diaryEntry.toString());
        startActivity(Intent.createChooser(intent, "Send Diary Entry"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    class GroupCursorAdapter extends CursorAdapter {
        public GroupCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final long groupID = cursor.getLong(cursor.getColumnIndex(ContactsContract.Groups._ID));
            String title = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE));

            TextView titleTxt = view.findViewById(android.R.id.text1);
            titleTxt.setText(title);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMailToGroup(groupID);
                }
            });
        }
    }
}
