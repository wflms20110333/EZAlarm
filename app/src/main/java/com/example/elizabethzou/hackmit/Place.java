package com.example.elizabethzou.hackmit;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

public class Place{
    public static final String NETWORK_PROVIDER;
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);


    public static void Run() {
        protected void onStart () {
            super.onStart();

            // This verification should be done during onStart() because the system calls
            // this method when the user returns to the activity, which ensures the desired
            // location provider is enabled each time the activity resumes from the stopped state.
            LocationManager locationManager =
                    (LocationManager).getSystemService(Context.LOCATION_SERVICE);
            final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!gpsEnabled) {
                // Build an alert dialog here that requests that the user enable
                // the location services, then when the user clicks the "OK" button,
                // call enableLocationSettings()
            }
        }

        private void enableLocationSettings () {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
    }
}
