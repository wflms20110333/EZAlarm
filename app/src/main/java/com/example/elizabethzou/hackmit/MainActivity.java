package com.example.elizabethzou.hackmit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.provider.AlarmClock.ACTION_DISMISS_ALARM;
import static android.provider.AlarmClock.ACTION_SET_ALARM;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    public static long MILLISECONDS_PER_DAY = 86400000L;
    public static long MILLISECONDS_PER_SECOND = 1000L;
    public static long SECONDS_PER_MINUTE = 60L;
    public static long UTC_TO_EDT = -14400L;

    //Button button2; //button for alarm
    Long time = null; //time for earliest event
    Long prelay = new Long("18000000"); //default morning routine time
    String filename = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)+"/Elizabeth_bot.txt";
    File file = new File(filename);
    OutputStreamWriter osw;
    InputStreamReader isr;  //file stuff

    Long toKill;

    protected LocationManager locationManager;
    protected double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);


        //initialize file;
        try  {
            if (!file.exists())  {
                Boolean bool = file.createNewFile();
                if (!bool) throw new Exception("File Creation Fail");
            }
        }
        catch (Exception e)  {
            Log.i("Error", e.getMessage());
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }


    //-------------------------CALENDAR COMPONENTS--------------------------

    /**
     * Represents a Calendar Event, storing the title, start minute, and location.
     */
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
        long currDay = Time.getJulianDay(start, UTC_TO_EDT);
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
        Log.i("getEarliestEvent", "lol " + events);
        for (CalEvent e : events)
            if (start + e.start * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND > System.currentTimeMillis())
                return e;
        return null;
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


    //-------------------------LOCATION COMPONENTS--------------------------

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        TextView txtLat = (TextView) findViewById(R.id.textview1);
        txtLat.setText("Latitude:" + latitude + ", Longitude:" + longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }


    //-------------------------ON CLICK--------------------------

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.button: {

                Calendar c = Calendar.getInstance();
                setToMidnight(c);
                getEarliestEvent(c);


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("didnotpasstest", "rip");
                    return;
                }
                Log.i("passedTest", "hey");
                //cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);
                Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    if (cursor != null) {
                        int id_2 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                        int id_5 = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                        int id_6 = cursor.getColumnIndex(CalendarContract.Events.DTEND);
                        String titleValue = cursor.getString(id_2);
                        String startValue = cursor.getString(id_5);
                        time = calculateTime(Long.parseLong(startValue));
                        String endValue = cursor.getString(id_6);
                        Toast.makeText(this, startValue, Toast.LENGTH_SHORT).show();
                        //makeAlarm(titleValue, time);
                        Toast.makeText(this, titleValue + ", " + startValue + ", " + endValue, Toast.LENGTH_SHORT).show();
                        break;
                    } else {
                        Toast.makeText(this, "Event is not present.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
            case R.id.submit: {
                String txt = ((EditText) findViewById(R.id.input)).getText().toString();
                ((EditText) findViewById(R.id.input)).setText("");
                try {
                    int numMinutes = Integer.parseInt(txt);
                    Toast.makeText(this, "Yay! You entered: " + numMinutes, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Please enter a valid integer.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //-------------------------FILE COMPONENTS--------------------------

    public class Record
    {
        String title;
        Long millis;

        public Record(String title, Long millis)
        {
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

    public void killAll()
    {
        try
        {
            FileInputStream fIn = new FileInputStream(file);
            isr = new InputStreamReader(fIn);
            BufferedReader in = new BufferedReader(isr);
            String str;
            Log.i("Erasing begins","");
            Integer count = 0;
            while ((str = in.readLine()) != null && str.length() != 0)  //read until EOF
            {
                Log.i("to be erased", str);
                String[] str1 = str.split(" ");
                toKill = Long.parseLong(str1[1]);
                killAlarm(Long.parseLong(str1[1]));
                count++;
            }
            Log.i("Erasing ends",count.toString());
            in.close();
        }
        catch (Exception e)
        {
            Log.i("Error",e.getMessage());//TODO: push error message
        }
    }

    //API for Elizabeth
    public Long retrieveTime(String title)
    {
        try
        {
            String goal = title.replace(" ","_");
            FileInputStream fIn = openFileInput(filename);
            isr = new InputStreamReader(fIn);
            BufferedReader in = new BufferedReader(isr);
            String str;
            while ((str = in.readLine()) != null && str.length() != 0)  //read until EOF
            {
                 String[] str1 = str.split(" ");
                 if (str1[0].equals(goal))
                 {
                     return Long.parseLong(str1[1]);
                 }
            }
            return null;
        }
        catch (Exception e)
        {
            //TODO: push error message
        }
        return null;
    }


    //-------------------------ALARM COMPONENTS-------------------------

    //calculate time to set alarm
    public Long calculateTime(Long time)
    {
        return time - prelay; //TODO: discretion
    }

    public void killAlarm(String title)
    {
        killAlarm(retrieveTime(title));
    }
    //erase alarm given time
    Intent eraseAlarm;

    public void killAlarm(Long millis)
    {
        Boolean permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED;
        Boolean calenderSet = time == null;
        if (permission || calenderSet) {
            Log.i("didnotpasstest", "rip");
            return;
        }
        //postponed
    }

    //setAlarm
    public void makeAlarm(String title, Long millis)
    {
        killAll();

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
        Record rc = new Record(title, millis);
        startActivity(setAlarm);

        if (!toKill.equals(millis))
        {
            Log.i("???", ((Long)(millis - toKill)).toString());
            eraseAlarm = new Intent();
            Calendar calendar1 = new GregorianCalendar();
            calendar1.setTimeInMillis(toKill);
            eraseAlarm.setAction(ACTION_DISMISS_ALARM);
            eraseAlarm.putExtra(AlarmClock.EXTRA_HOUR, calendar1.HOUR);
            eraseAlarm.putExtra(AlarmClock.EXTRA_MINUTES, calendar1.MINUTE);
            eraseAlarm.putExtra(AlarmClock.EXTRA_IS_PM, false);
            eraseAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            startActivity(eraseAlarm);
        }
        //put record
        try {
            wait(1000);
        }
        catch (Exception e){

        }
        putRecord(rc);
    }
}
