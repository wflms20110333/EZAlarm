package com.example.elizabethzou.hackmit;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

public class Place{
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
                AlertDialog.Builder enableLocation = new AlertDialog.Builder(get(Activity()));
                enableLocation.setMessage("Location services not currently enabled. Enable?");
                enableLocation.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        enableLocationSettings();
                    }
                });
                enableLocation.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                AlertDialog dialog = enableLocation.create();
            }
        }

        private void enableLocationSettings () {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
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

    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000, 100, listener);
}
