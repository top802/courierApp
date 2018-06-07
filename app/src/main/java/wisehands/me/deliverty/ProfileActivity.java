package wisehands.me.deliverty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.messaging.FirebaseMessaging;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String API_HOST = "http://192.168.1.88:8080";
    private static String firebaseToken;

    Button locsetting;
    private TextView urls, mygpgstatus, mynetstatus, mylocation;
    private LocationManager locationManager;
    StringBuilder sbNet = new StringBuilder();
    private ProfileActivity context = this;

    private TextView txtName, txtEmail,vToken;
    private FirebaseAuth mAuth;

    private final String TOPIC = "JavaSampleApproach";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        urls = (TextView) findViewById(R.id.url);
        mylocation = (TextView) findViewById(R.id.mylocation);
        mynetstatus = (TextView) findViewById(R.id.netstatus);
        mygpgstatus = (TextView) findViewById(R.id.gpgstatus);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        findViewById(R.id.locSetting).setOnClickListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        vToken = (TextView) findViewById(R.id.vToken);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);

        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());

        findViewById(R.id.bn_accept).setOnClickListener(this);
        findViewById(R.id.bn_cancel).setOnClickListener(this);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        final ProfileActivity  self = this;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            final String idToken = task.getResult().getToken();
                            self.firebaseToken = idToken;
                            vToken.setText("idToken received");
                            // Send token to your backend via HTTPS
                            // volley POST
                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(self);
                            String url = API_HOST +"/authenticate?token=" + idToken;

                            // Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Display the first 500 characters of the response string.
                                            //txtToken.setText("Response is: "+ response.substring(0,500));

                                            Toast.makeText(ProfileActivity.this,"registration is completed.",
                                                    Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    private void cancelOrder() {
        Toast.makeText(ProfileActivity.this, "You are canceled delivery", Toast.LENGTH_SHORT).show();
    }

    // start get location

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * 10, 10, locationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * 1, 10,
                locationListener);
        checkEnabled();
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                mygpgstatus.setText("GPS status: " + String.valueOf(status));
            } else
            if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                mynetstatus.setText("NET status: " + String.valueOf(status));
            }
        }
    };

    private void showLocation(final Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            mylocation.setText(formatLocation(location));
        } else
        if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            mylocation.setText(formatLocation(location));
        }

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final Location locationVar = location;

        final Context self=this;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();

                            double lat = locationVar.getLatitude();
                            double lon = locationVar.getLongitude();
                            String urlPath = "update-courier-location";
                            String params = String.format("token=%s&latitude=%f&longitude=%f", idToken, lat, lon);
//                            String params = String.format("latitude=%f&longtitude=%f", lat, lon);
                            String updateCourierLocation = String.format("%s/%s?%s", API_HOST, urlPath, params);

                            urls.setText(lat+" "+lon);
                            RequestQueue queue = Volley.newRequestQueue(self);
                            StringRequest postRequest = new StringRequest(Request.Method.POST, updateCourierLocation,
                                    new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.d("Response", response);
                                        }
                                    },
                                    new Response.ErrorListener()
                                    {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.d("Error.Response", error.getMessage());
                                        }
                                    }
                            );
                            queue.add(postRequest);

                        } else {
                            Toast.makeText(ProfileActivity.this, "Token with URL no send",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });



    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f",
                location.getLatitude(), location.getLongitude());
    }

    private void checkEnabled() {
        mygpgstatus.setText("GPS enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        mynetstatus.setText("NET enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }



}


