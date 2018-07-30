package wisehands.me.deliverty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TEST!!!!";

    public static final String API_HOST = "http://192.168.1.3:8080";
//    public static final String API_HOST = "http://192.168.1.88:8080";
//    public static final String API_HOST = "http://192.168.0.13:8080";
    private static String firebaseToken;

    private TextView testtest, testtest2, gpsstatus, mynetstatus, mylocation;
    private TextView txtName, txtEmail,vToken;

    private LocationManager locationManager;

    private ProfileActivity context = this;
    public static String jwttoken;

    private FirebaseAuth mAuth;

    private final String TOPIC = "JavaSampleApproach";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        testtest = (TextView) findViewById(R.id.url);
        testtest2 = (TextView) findViewById(R.id.url2);
        mylocation = (TextView) findViewById(R.id.mylocation);
        mynetstatus = (TextView) findViewById(R.id.netstatus);
        gpsstatus = (TextView) findViewById(R.id.gpsstatus);
        vToken = (TextView) findViewById(R.id.vToken);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        findViewById(R.id.locSetting).setOnClickListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());

        findViewById(R.id.bn_accept).setOnClickListener(this);
        findViewById(R.id.bn_cancel).setOnClickListener(this);

//      create Token, JWT
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "1 step");
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            startService(new Intent(context, ServiceGPS.class));

                            final String fbToken = task.getResult().getToken();
                            context.firebaseToken = fbToken;
                            vToken.setText("firebaseToken received");
//                            send Token and create Volley POST
                            String urlPath = "authenticate";
                            String params = String.format("token=%s", fbToken);
                            String url = String.format("%s/%s?%s", API_HOST, urlPath, params);

                            RequestQueue queue = Volley.newRequestQueue(context);
//                            Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            jwttoken = response;
                                            testtest.setText("jwtoken received and registration is complete");
                                            Toast.makeText(ProfileActivity.this, "registration is complete.",
                                                    Toast.LENGTH_SHORT).show();
                                            Log.i(TAG, "2 step");
                                            context.onJWTTokenReceived();
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(ProfileActivity.this, "registration is failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                            // Add the request to the RequestQueue.
                            queue.add(stringRequest);

                        } else {
                            Toast.makeText(ProfileActivity.this, "Token not generated.",
                                    Toast.LENGTH_SHORT).show();

                            // Handle error -> task.getException();
                        }
                    }
                });



    }

    public void onJWTTokenReceived(){

        testtest2.setText("send deviceId and check json web token");
        String deviceId = FirebaseInstanceId.getInstance().getToken();
        String urlPath = "save-device-token";
        String params = String.format("jwttoken=%s&deviceId=%s", jwttoken, deviceId);
        String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);


        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i(TAG, "3.1 step");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.i(TAG, "ERROR, ERROR, ERROR  of RESPONSE after sending device token");
                    }
                }
        );queue.add(postRequest);
        Log.i(TAG, "3.2 step");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bn_accept:
                acceptOrder();
                break;
            case R.id.bn_cancel:
                cancelOrder();
                break;
            case R.id.locSetting:
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                break;
        }
    }


    private void acceptOrder() {
        boolean isactive = true;
        String urlPath = "isactivecourier";
        String params = String.format("isactive=%s", isactive);
        String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i(TAG, "RESPONSE: you are ready to accept order " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.i(TAG, "FAILRequest: you aren't ready to accept order");
                    }
                }
        );
        queue.add(postRequest);
    }

    private void cancelOrder() {
        boolean isactive = false;
        String urlPath = "finishtheorder";
        String params = String.format("isactive=%s", isactive);
        String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.i(TAG, "RESPONSE: you are finishing delivery now " + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.i(TAG, "FAILRequest: you aren't ready to accept order");
                    }
                }
        );
        queue.add(postRequest);
    }

    //Request.Method.POST
    private void sendRequestToServer() {
//        Toast.makeText(ServiceGPS.this, "sendRequestToServer",
//                Toast.LENGTH_SHORT).show();

        double latitude = 16.20;
        double longitude = 4.20;

        String urlPath = "update-courier";
        @SuppressLint("DefaultLocale") String params = String.format("latitude=%f&longitude=%f", latitude, longitude);
        String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Toast.makeText(ProfileActivity.this, "RequestToServer",
                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(ProfileActivity.this, response,
                                Toast.LENGTH_SHORT).show();
                        Log.d("Response", "RESPONSE" + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(ProfileActivity.this, "FAILRequestToServer",
                                Toast.LENGTH_SHORT).show();
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );
        queue.add(postRequest);
    }


    // start get location

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                gpsstatus.setText("GPS status: " + String.valueOf(status));
            } else
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                mynetstatus.setText("NET status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(final Location location) {
        Log.i(TAG, "start to showing location");

        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            mylocation.setText(formatLocation(location));
            Log.i(TAG, "4 step");

        } else
        if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            mylocation.setText(formatLocation(location));
        }
        Log.i(TAG, "5 step" + location.getLatitude() + location.getLongitude());
//        Log.i(TAG, "location1" + location.getLatitude() + location.getLongitude());


        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final Location locationVar = location;

        final Context self = this;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Log.i(TAG, "6 step idToken that we send with update location");

                            double lat = locationVar.getLatitude();
                            double lon = locationVar.getLongitude();
                            Log.i(TAG, lat + " " + lon);

                            String urlPath = "update-courier-location";
                            @SuppressLint("DefaultLocale")
                            String params = String.format("token=%s&latitude=%f&longitude=%f", idToken, lat, lon);
                            String updateCourierLocation = String.format("%s/%s?%s", API_HOST, urlPath, params);

                            RequestQueue queue = Volley.newRequestQueue(self);
                            StringRequest postRequest = new StringRequest(Request.Method.POST, updateCourierLocation,
                                    new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.i(TAG, "7 step" + response);
                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.i(TAG, "Error of sending location");
                                        }
                                    }
                            );
                            queue.add(postRequest);

                        } else {
                            Log.i(TAG, "Error of sending URL location");

                        }
                    }
                });



    }

    @SuppressLint("DefaultLocale")
    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format("Coordinates: lat = %1$.4f, lon = %2$.4f", location.getLatitude(), location.getLongitude());
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,1000 * 5, 2,
                locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 5, 2,
                locationListener);
        checkEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private void checkEnabled() {
        gpsstatus.setText("GPS enabled: "
                + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        mynetstatus.setText("NET enabled: "
                + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }



}


