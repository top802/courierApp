package wisehands.me.deliverty;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static wisehands.me.deliverty.ProfileActivity.API_HOST;

public class ServiceGPS extends Service {

    private Context self = this;

    private static final String TAG = "TEST!!!!";
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5;

    Application mainActivity;

    private LocationManager mLocationManager = null;

//    private static Location lastLocation = null;

    private class MyLocationListener implements LocationListener
    {
        Location mLastLocation;

        public MyLocationListener(String provider)
        {
            Log.i(TAG, "Service: LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override // send gps to sever
        public void onLocationChanged(Location location)
        {
            if (mLastLocation == null) {
                mLastLocation = location;
            }
            showLocation(location);

            Log.i(TAG, "Service: onLocationChanged: " + location);
            mLastLocation.set(location);
        }


        public void showLocation(Location location) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            String urlPath = "update-courier";
            @SuppressLint("DefaultLocale")
            String params = String.format("latitude=%f&longitude=%f", latitude, longitude);
            String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);

            RequestQueue queue = Volley.newRequestQueue(self);
            StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {

                            Log.i(TAG, "Service: BG location response " + response);

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.i(TAG, "Service:  ERROR BG location response ");

                        }
                    }
            );queue.add(postRequest);

        }


        @Override
        public void onProviderDisabled(String provider)
        {
            Log.i(TAG, "Service: onProviderDisabled: " + provider);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider)
        {
            showLocation(mLocationManager.getLastKnownLocation(provider));
            Log.i(TAG, "Service: onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.i(TAG, "Service: onStatusChanged: " + provider);
        }
    }

    MyLocationListener[] mLocationListeners = new MyLocationListener[] {
            new MyLocationListener(LocationManager.GPS_PROVIDER),
            new MyLocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(ServiceGPS.this, "start service",
                Toast.LENGTH_SHORT).show();

        mainActivity = getApplication();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Log.i(TAG, "Service: onStartCommand");

        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Toast.makeText(ServiceGPS.this, "fail to request location update, ignore",
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.i(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

    }



    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
