package com.example.elizabethzou.hackmit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
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
                    //Log.i("didnotpasstest", "rip");
                    return;
                }
                //Log.i("passedTest", "hey");
                events = new ArrayList<>();

                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long start = c.getTimeInMillis();
                c.add(Calendar.DAY_OF_MONTH, 1);
                long end = c.getTimeInMillis();
                long day = Time.getJulianDay(System.currentTimeMillis(), -14400);
                Uri myUri = Uri.parse("content://com.android.calendar/instances/when/" + start + "/" + end);
                Cursor instancesCursor = getContentResolver().query(myUri, null, null, null, null);
                while (instancesCursor.moveToNext()) {
                    if (instancesCursor != null) {
                        int id_1 = instancesCursor.getColumnIndex(CalendarContract.Instances.TITLE);
                        int id_2 = instancesCursor.getColumnIndex(CalendarContract.Instances.START_MINUTE);
                        int id_3 = instancesCursor.getColumnIndex(CalendarContract.Instances.START_DAY);
                        int id_4 = instancesCursor.getColumnIndex(CalendarContract.Instances.END_MINUTE);
                        String title = instancesCursor.getString(id_1);
                        int startMinute = Integer.parseInt(instancesCursor.getString(id_2));
                        long startDay = Long.parseLong(instancesCursor.getString(id_3));
                        int endMinute = Integer.parseInt(instancesCursor.getString(id_4));
                        if (startDay != day || startMinute <= 0 && endMinute >= 1440)
                            continue;
                        events.add(new CalEvent(title, startMinute));
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
        int start;

        public CalEvent(String a, int b)
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
