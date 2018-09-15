package com.example.elizabethzou.hackmit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.provider.AlarmClock.ACTION_SET_ALARM;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
    Button button2;
    Cursor cursor;
    Long time = null; //time for earliest event
    Long prelay = new Long("18000000"); //default morning routine time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
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
                cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    if (cursor != null) {
                        int id_1 = cursor.getColumnIndex(CalendarContract.Events._ID);
                        int id_2 = cursor.getColumnIndex(CalendarContract.Events.TITLE);
                        int id_3 = cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION);
                        int id_4 = cursor.getColumnIndex(CalendarContract.Events.EVENT_LOCATION);
                        int id_5 = cursor.getColumnIndex(CalendarContract.Events.DTSTART);
                        int id_6 = cursor.getColumnIndex(CalendarContract.Events.DTEND);
                        String idValue = cursor.getColumnName(id_1);
                        String titleValue = cursor.getString(id_2);
                        String descriptionValue = cursor.getString(id_3);
                        String eventValue = cursor.getString(id_4);
                        String startValue = cursor.getString(id_5);
                        time = Long.parseLong(startValue);
                        String endValue = cursor.getString(id_6);
                        Toast.makeText(this, startValue, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Event is not present.", Toast.LENGTH_SHORT).show();
                    }
                }
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
}
