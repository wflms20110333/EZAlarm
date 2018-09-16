package com.example.elizabethzou.hackmit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.provider.AlarmClock.ACTION_DISMISS_ALARM;
import static android.provider.AlarmClock.ACTION_SET_ALARM;
import static java.lang.Thread.sleep;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button; //button for calender
    Cursor cursor;
    Button button2; //button for alarm
    Long time = null; //time for earliest event
    Long prelay = new Long("18000000"); //default morning routine time
    String filename = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)+"/Elizabeth_bot.txt";
    File file = new File(filename);
    OutputStreamWriter osw;
    InputStreamReader isr;  //file stuff

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
        }
        catch (Exception e)  {
            //TODO: push appropriate alarm message;
            Log.i("Error", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button: {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Log.i("didnotpasstest", "rip");
                    return;
                }
                Log.i("passedTest", "hey");
                cursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), null, null, null, null);
                cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    if (cursor != null) {
                        //int id_1 = cursor.getColumnIndex(CalendarContract.Instances.TITLE);
                        //int id_2 = cursor.getColumnIndex(CalendarContract.Instances.BEGIN);
                        //String title = cursor.getString(id_1);
                        //Date start = new Date(Long.parseLong(cursor.getString(id_2)));
                        //events.add(new CalEvent(title, start));

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
                        String startValue = cursor.getString(id_5);
                        time = calculateTime(Long.parseLong(startValue));
                        String endValue = cursor.getString(id_6);
                        Toast.makeText(this, startValue, Toast.LENGTH_SHORT).show();
                        makeAlarm(titleValue, time);
                        //String descriptionValue = cursor.getString(id_3);
                        //String eventValue = cursor.getString(id_4);
                        //Date startValue = new Date(Long.parseLong(cursor.getString(id_5)));
                        //Date endValue = new Date(Long.parseLong(cursor.getString(id_6)));
                        //events.add(new CalEvent(titleValue, startValue, endValue));
                        Toast.makeText(this, titleValue + ", " + startValue + ", " + endValue, Toast.LENGTH_SHORT).show();
                        //Log.i("title", "lol " + titleValue);
                        //Log.i("dtstart", "lol " + startValue);
                        //Log.i("dtend", "lol " + endValue);
                        break;
                    } else {
                        Toast.makeText(this, "Event is not present.", Toast.LENGTH_SHORT).show();
                    }
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
    Long toKill;
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
    public Long retrieveTime(String title)  {
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

    public void killAlarm(Long millis)  {
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
