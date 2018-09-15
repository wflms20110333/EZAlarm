package com.example.elizabethzou.hackmit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
    Cursor cursor;
    List<CalEvent> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("didnotpasstest", "rip");
                    return;
                }
                Log.i("passedTest", "hey");
                events = new ArrayList<>();
                cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);
                //cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    if (cursor != null) {
                        int id_1 = cursor.getColumnIndex(CalendarContract.Instances.TITLE);
                        int id_2 = cursor.getColumnIndex(CalendarContract.Instances.BEGIN);
                        String title = cursor.getString(id_1);
                        Date start = new Date(Long.parseLong(cursor.getString(id_2)));
                        events.add(new CalEvent(title, start));
                        /*
                        //int id_1 = cursor.getColumnIndex(CalendarContract.Events._ID);
                        int id_2 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                        //int id_3 = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                        //int id_4 = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
                        int id_5 = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                        int id_6 = cursor.getColumnIndex(CalendarContract.Events.DTEND);
                        //String idValue = cursor.getColumnName(id_1);
                        String titleValue = cursor.getString(id_2);
                        //String descriptionValue = cursor.getString(id_3);
                        //String eventValue = cursor.getString(id_4);
                        Date startValue = new Date(Long.parseLong(cursor.getString(id_5)));
                        Date endValue = new Date(Long.parseLong(cursor.getString(id_6)));
                        events.add(new CalEvent(titleValue, startValue, endValue));
                        Toast.makeText(this, titleValue + ", " + startValue + ", " + endValue, Toast.LENGTH_SHORT).show();
                        //Log.i("title", "lol " + titleValue);
                        //Log.i("dtstart", "lol " + startValue);
                        //Log.i("dtend", "lol " + endValue);
                        */
                    } else {
                        Toast.makeText(this, "Event is not present.", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i("rip", "lol " + events);
                break;
        }
    }

    public static class CalEvent
    {
        String title;
        Date start;

        public CalEvent(String a, Date b)
        {
            title = a;
            start = b;
        }

        public String toString()
        {
            return title + ": " + start;
        }
    }
}
