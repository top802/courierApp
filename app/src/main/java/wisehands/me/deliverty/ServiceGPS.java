package wisehands.me.deliverty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
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
                            // error
                            Toast.makeText(ServiceGPS.this, "FAILRequestToServer",
                                    Toast.LENGTH_SHORT).show();
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

        @Override
        public void onProviderEnabled(String provider)
        {

            if (ActivityCompat.checkSelfPermission(self, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(self, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
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

    private void sendLocation(double latitude, double longitude) {

        String urlPath = "update-courier";
        @SuppressLint("DefaultLocale")
        String params = String.format("latitude=%f&longitude=%f", latitude, longitude);
        String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);

        RequestQueue queue = Volley.newRequestQueue(self);
        StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(ServiceGPS.this, "RequestToServer",
                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(ServiceGPS.this, response,
                                Toast.LENGTH_SHORT).show();
                        Log.d("Response", "RESPONSE" + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(ServiceGPS.this, "FAILRequestToServer",
                                Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );
        queue.add(postRequest);

    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

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


//        LocationListener listener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                if (lastLocation == null) {
//                    lastLocation = location;
//                }
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//                sendLocation(latitude, longitude);
//                Toast.makeText(ServiceGPS.this, latitude + "" + longitude, Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//
//            }
//        };
//        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, listener);

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

////        Toast.makeText(ServiceGPS.this, "onCreate",
////                Toast.LENGTH_SHORT).show();
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
