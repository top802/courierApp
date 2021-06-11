package wisehands.me.deliverty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
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

import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


import javax.net.ssl.TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.ResourceBundle;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "STEPS";
    public static final String API_HOST = "https://192.168.1.78:8902";
    private TextView  gpsstatus, mynetstatus;
    private TextView txtName, txtEmail;
    private Switch switchButton;

    private LocationManager locationManager;

    private ProfileActivity context = this;
    public static String jwttoken;
    public static String firebaseToken;

    private FirebaseAuth mAuth;

    private final String TOPIC = "JavaSampleApproach";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mynetstatus = (TextView) findViewById(R.id.netstatus);
        gpsstatus = (TextView) findViewById(R.id.gpsstatus);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        switchButton = (Switch) findViewById(R.id.switch1);

        findViewById(R.id.locSetting).setOnClickListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());

        switchButton.setChecked(false);




//        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
//            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//            }
//            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//            }
//        } };
//        SSLContext sc = null;
//        try {
//            sc = SSLContext.getInstance("TLSv1.2");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        try {
//            sc.init(null, trustAllCerts, new java.security.SecureRandom());
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//        // Create all-trusting host name verifier
//        HostnameVerifier allHostsValid = new HostnameVerifier() {
//            public boolean verify(String hostname, SSLSession session) {
//                return true;
//            }
//        };
//        // Install the all-trusting host verifier
//        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);






        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG, "1 step - get IdToken");
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {

                            ProfileActivity.firebaseToken = task.getResult().getToken();
                            Log.i(TAG, "1/1 step - send IdToken");
                            authenticateUser();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Token not generated.",
                                    Toast.LENGTH_SHORT).show();

                            // Handle error -> task.getException();
                        }
                    }
                });

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bn_navi_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.item_main:
                                exitApp();
                                break;
                            case R.id.item_profile:
                                break;
                            case R.id.item_message:
                                finish();
                                startActivity(new Intent(context, MessageActivity.class));
                                break;
                        }
                        return true;
                    }
                });

    }

    public void exitApp(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit ??");
        builder.setPositiveButton("Yes. Exit now!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i){

                System.exit(0);

           }
         });
	    builder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void getResponse(int method, String url, JSONObject jsonValue, final VolleyCallback callback) {

        RequestQueue queue = MySingleton.getInstance(this).getRequestQueue();

        StringRequest strreq = new StringRequest(Request.Method.GET, url, new Response.Listener < String > () {

            @Override
            public void onResponse(String Response) {
                callback.onSuccessResponse(Response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(ProfileActivity.this, e + "error", Toast.LENGTH_LONG).show();
            }
        })
        {
            // set headers
            @Override
            public Map< String, String > getHeaders() throws com.android.volley.AuthFailureError {
                Map< String, String > params = new HashMap< String, String >();
//                params.put("Authorization: Basic", TOKEN);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(strreq);
    }

    public void authenticateUser() {
        String urlPath = "authenticate";
        String params = String.format("token=%s", firebaseToken);
        String url = String.format("%s/%s?%s", API_HOST, urlPath, params);
        Log.i(TAG, "1/2 step - get IdToken => " + url);

        getResponse(Request.Method.POST, url, null,
                new VolleyCallback() {
                    @Override
                    public void onSuccessResponse(String result) {
                        jwttoken = result;
                        Toast.makeText(ProfileActivity.this, "authentication is complete.",
                                Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "2/2 step " + "registration is complete.");
                        context.onJWTTokenReceived();
                    }
                });

    }

    public void onJWTTokenReceived(){

//        testtest2.setText("send deviceId and check json web token");
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
            case R.id.locSetting:
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                break;
        }
    }

    public void onSwitchClick(View view){
        if(switchButton.isChecked()){
            Log.i(TAG, "you are ready to work " + true);
            boolean isactive = true;
            String urlPath = "isactivecourier";
            String params = String.format("jwttoken=%s&isactive=%s", jwttoken, isactive);
            String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.i(TAG, "RESPONSE: you are ready to work " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.i(TAG, "FAILRequest: you aren't ready to work");
                        }
                    }
            );
            queue.add(postRequest);
            switchButton.setChecked(true);
        }
        else {
            boolean isactive = false;
            Log.i(TAG, "RESPONSE: you are not ready to work " + false);
            String urlPath = "isactivecourier";
            String params = String.format("jwttoken=%s&isactive=%s", jwttoken, isactive);
            String strURL = String.format("%s/%s?%s", API_HOST, urlPath, params);

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, strURL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Log.i(TAG, "RESPONSE: you are not ready to work " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.i(TAG, "FAILRequest: you aren't ready to work");
                        }
                    }
            );
            queue.add(postRequest);
        }
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
//            mylocation.setText(formatLocation(location));
            Log.i(TAG, "4 step");
            Log.i(TAG, "4 step " + formatLocation(location));

        } else
        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            Log.i(TAG, "4 step " + formatLocation(location));

//            mylocation.setText(formatLocation(location));
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


