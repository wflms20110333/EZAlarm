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
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.provider.AlarmClock.ACTION_DISMISS_ALARM;
import static android.provider.AlarmClock.ACTION_SET_ALARM;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button; //button for calender
    Cursor cursor;
    Button button2; //button for alarm
    Long time = null; //time for earliest event
    Long prelay = new Long("18000000"); //default morning routine time
    List<CalEvent> events;
    String filename = "Elizabeth_bot.txt";
    File file = new File(filename);
    OutputStreamWriter osw;
    InputStreamReader isr;  //file stuff
    Record[] records = new Record[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        //initialize file;
        try  {
            if (!file.exists())  {
                Boolean bool = file.createNewFile();
                if (!bool) throw new Exception("File Creation Fail");
            }
            FileOutputStream fOut = openFileOutput(filename, MODE_PRIVATE);
            osw = new OutputStreamWriter(fOut);
            //TODO: remove set alarm;
        }
        catch (Exception e)  {
            //TODO: push appropriate alarm message;
            break;
        }
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

                        //int id_1 = cursor.getColumnIndex(CalendarContract.Events._ID);
                        int id_2 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                        //int id_3 = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                        //int id_4 = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
                        int id_5 = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                        int id_6 = cursor.getColumnIndex(CalendarContract.Events.DTEND);
                        //String idValue = cursor.getColumnName(id_1);
                        String titleValue = cursor.getString(id_2);
                        String descriptionValue = cursor.getString(id_3);
                        String eventValue = cursor.getString(id_4);
                        String startValue = cursor.getString(id_5);
                        time = calculateTime(Long.parseLong(startValue));
                        String endValue = cursor.getString(id_6);
                        Toast.makeText(this, startValue, Toast.LENGTH_SHORT).show();
                        setAlarm(titleValue, time);
                        //String descriptionValue = cursor.getString(id_3);
                        //String eventValue = cursor.getString(id_4);
                        Date startValue = new Date(Long.parseLong(cursor.getString(id_5)));
                        Date endValue = new Date(Long.parseLong(cursor.getString(id_6)));
                        events.add(new CalEvent(titleValue, startValue, endValue));
                        Toast.makeText(this, titleValue + ", " + startValue + ", " + endValue, Toast.LENGTH_SHORT).show();
                        //Log.i("title", "lol " + titleValue);
                        //Log.i("dtstart", "lol " + startValue);
                        //Log.i("dtend", "lol " + endValue);

                    } else {
                        Toast.makeText(this, "Event is not present.", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.i("rip", "lol " + events);
                break;
        }
    }
    //-------------------------FILE COMPONENTS--------------------------
    public class Record
    {
        String title;
        Long millis;
        String valid;
        public Record(String title, Long millis) {
            this.title = title.replaceAll(" ", "_"); //erase spaces to avoid trouble
            this.millis = millis;
        }
        @Override
        public String toString()
        {
            return title + " " + millis.toString();
        }
    }

    public void putRecord(Record r)
    {
        try
        {
            PrintWriter pt = new PrintWriter(file);
            pt.write(r.title + " " + r.millis.toString());
            pt.close(); //print new records& erase existing ones
        }
        catch (Exception e)
        {
            //TODO: push error message
        }
    }
    public Long eraseAll()
    {
        try
        {
            FileInputStream fIn = openFileInput(filename);
            isr = new InputStreamReader(fIn);
            BufferedReader in = new BufferedReader(isr);
            String str;
            while ((str = in.readLine()) != null)  //read until EOF
            {
                String[] str1 = str.split(" ");
                eraseAlarm(str1[1]);
            }
        }
        catch (Exception e)
        {
            //TODO: push error message
        } 
    }
    //API for Elizabeth
    public Long retrieveTime(String title)  {
        try
        {
            String goal = title.replace(" ","_")
            FileInputStream fIn = openFileInput(filename);
            isr = new InputStreamReader(fIn);
            BufferedReader in = new BufferedReader(isr);
            String str;
            while ((str = in.readLine()) != null)  //read until EOF
            {
                 String[] str1 = str.split(" ");
                 if (str1[0].equals(goal))
                 {
                     return Long.parseLong(str1[1]);
                 }
            }
        }
        catch (Exception e)
        {
            //TODO: push error message
        }
    }
    //-------------------------ALARM COMPONENTS-------------------------
    //calculate time to set alarm
    public Long calculateTime(Long time)
    {
        return time - prelay; //TODO: discretion
    }

    public void eraseAlarm(String title)
    {
        eraseAlarm(retrieveTime(title));
    }
    //erase alarm given time
    public void eraseAlarm(Long millis)  {
        Boolean permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED;
        Boolean calenderSet = time == null;
        if (permission || calenderSet) {
            Log.i("didnotpasstest", "rip");
            return;
        }
        Intent eraseAlarm = new Intent();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(millis);
        eraseAlarm.setAction(ACTION_DISMISS_ALARM);
        eraseAlarm.putExtra(AlarmClock.EXTRA_HOUR, calendar.HOUR);
        eraseAlarm.putExtra(AlarmClock.EXTRA_MINUTES, calendar.MINUTE);
        eraseAlarm.putExtra(AlarmClock.EXTRA_IS_PM, false);
        startActivity(eraseAlarm);
    }


    //setAlarm
    public void setAlarm(String title, Long millis)
    {
        eraseAll();
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
        calendar.setTimeInMillis(millis);
        setAlarm.putExtra(AlarmClock.EXTRA_HOUR, calendar.HOUR);
        setAlarm.putExtra(AlarmClock.EXTRA_MINUTES, calendar.MINUTE);
        //in the morning;
        setAlarm.putExtra(AlarmClock.EXTRA_IS_PM, false);
        startActivity(setAlarm);
        //put record
        Record rc = new Record(title, millis);
        putRecord(rc);
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


    private final LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // A new location update is received.  Do something useful with it.  In this case,
            // we're sending the update to a handler which then updates the UI with the new
            // location.
            Message.obtain(mHandler,
                    UPDATE_LATLNG,
                    location.getLatitude() + ", " +
                            location.getLongitude()).sendToTarget();
        }
    };

    LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000, 100, listener);

}
