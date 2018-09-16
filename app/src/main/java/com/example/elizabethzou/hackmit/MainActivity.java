package com.example.elizabethzou.hackmit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static android.provider.AlarmClock.ACTION_SET_ALARM;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static long MILLISECONDS_PER_DAY = 86400000L;
    Button button2; //button for alarm
    Long time = null; //time for earliest event
    Long prelay = new Long("18000000"); //default morning routine time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        //erase existing
    }

    /**
     * Returns the earliest event of a day.
     * @param day the day in question, with time set to midnight.
     * @return the earliest event of the day
     */
    public CalEvent getEarliestEvent(Calendar day)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)
            return null;

        List<CalEvent> events = new ArrayList<>();
        long start = day.getTimeInMillis();
        long end = start + MILLISECONDS_PER_DAY;
        long currDay = Time.getJulianDay(start, -14400);
        Uri myUri = Uri.parse("content://com.android.calendar/instances/when/" + start + "/" + end);
        Cursor instancesCursor = getContentResolver().query(myUri, null, null, null, null);
        while (instancesCursor.moveToNext()) {
            if (instancesCursor != null) {
                int id_1 = instancesCursor.getColumnIndex(CalendarContract.Instances.TITLE);
                int id_2 = instancesCursor.getColumnIndex(CalendarContract.Instances.START_MINUTE);
                int id_3 = instancesCursor.getColumnIndex(CalendarContract.Instances.START_DAY);
                int id_4 = instancesCursor.getColumnIndex(CalendarContract.Instances.END_MINUTE);
                int id_5 = instancesCursor.getColumnIndex(CalendarContract.Instances.EVENT_LOCATION);
                String title = instancesCursor.getString(id_1);
                int startMinute = Integer.parseInt(instancesCursor.getString(id_2));
                long startDay = Long.parseLong(instancesCursor.getString(id_3));
                int endMinute = Integer.parseInt(instancesCursor.getString(id_4));
                String location = instancesCursor.getString(id_5);
                if (startDay != currDay || startMinute <= 0 && endMinute >= 1440)
                    continue;
                events.add(new CalEvent(title, startMinute, location));
            }
        }
        Collections.sort(events);
        //Log.i("rip", "lol " + events);
        return events.isEmpty() ? null: events.get(0);
    }

    /**
     * Sets a Calendar object to midnight.
     * @param c the Calendar object
     */
    private void setToMidnight(Calendar c)
    {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Calendar c = Calendar.getInstance();
                setToMidnight(c);
                getEarliestEvent(c);
                break;

            case R.id.button2:
                Boolean permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED;
                Boolean calenderSet = time == null;
                if (permission || calenderSet) {
                    Log.i("didnotpasstest", "rip");
                    return;
                }
                Log.i("passedTest", "Setting Alarm");
                Intent setAlarm = new Intent();
                //setAlarm;
                setAlarm.setAction(ACTION_SET_ALARM);
                setAlarm.setData(null);
                //don't show UI
                setAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                //calculate Date
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(time - prelay);
                setAlarm.putExtra(AlarmClock.EXTRA_HOUR, calendar.HOUR);
                setAlarm.putExtra(AlarmClock.EXTRA_MINUTES, calendar.MINUTE);
                //in the morning;
                setAlarm.putExtra(AlarmClock.EXTRA_IS_PM, false);
                startActivity(setAlarm);
                break;
        }
    }

    public static class CalEvent implements Comparable<CalEvent>
    {
        String title;
        int start;
        String location;

        public CalEvent(String a, int b, String c)
        {
            title = a;
            start = b;
            location = c;
        }

        @Override
        public String toString()
        {
            return title + ": " + start + ", " + location;
        }

        @Override
        public int compareTo(CalEvent o)
        {
            return this.start - o.start;
        }
    }
}
