package com.example.elizabethzou.hackmit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Message;
import android.provider.Settings;

/*
public class Place{
    // public static void Run() {
    LocationManager locationManager;
    LocationProvider provider;
    protected void onStart(){
        super.onStart();
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            AlertDialog.Builder enableLocation = new AlertDialog.Builder(get(Activity()));
            enableLocation.setMessage("Location services not currently enabled. Enable?");
            enableLocation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
//help I don't know how activities work
                    activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    dialog.dismiss();
                }
            });
            enableLocation.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    }
            });
            AlertDialog dialog = enableLocation.create();
            dialog.show();
        }

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
    }

    //Location should be enabled now

    private final LocationListener listener = new LocationListener() {

        // @Override
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

<<<<<<< HEAD
    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000, 100, listener);
}
*/
=======
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000, 100, listener);
}
>>>>>>> e204225fda5a4185c3c799e9b2580bd9e16cf599
