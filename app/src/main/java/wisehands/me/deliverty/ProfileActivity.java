package wisehands.me.deliverty;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import org.json.JSONObject;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtName, txtEmail, txtToken;
    private Button bn_loguot, bn_token;
    private FirebaseAuth mAuth;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtToken = findViewById(R.id.txtToken);

        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());

        bn_loguot = findViewById(R.id.bn_logout);
        bn_loguot.setOnClickListener(this);
        bn_token = findViewById(R.id.bn_token);
        bn_token.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bn_logout:
                signOut();

                break;
            case R.id.bn_token:
                deviseToken();
                break;
        }
    }

    private void deviseToken() {

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        final ProfileActivity  self =this;
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            txtToken.setText(idToken);
                            // Send token to your backend via HTTPS

                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(self);
                            String url ="http://192.168.1.88:8080/authenticate?token=" + idToken;

                            // Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Display the first 500 characters of the response string.
                                            txtToken.setText("Response is: "+ response.substring(0,500));
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    txtToken.setText("That didn't work!");
                                }
                            });

                            // Add the request to the RequestQueue.
                            queue.add(stringRequest);

                        } else {
                            txtToken.setText("Token not generated");
                            // Handle error -> task.getException();
                        }
                    }
                });

    }

    private void signOut() {

        mAuth.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                });

        finish();
        startActivity(new Intent(this, MainActivity.class));

    }

    private void updateUI(final FirebaseUser user) {
//        if (user == null){
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//        }
//        else {
//         txtToken.setText("fuck");
//        }
    }

}


