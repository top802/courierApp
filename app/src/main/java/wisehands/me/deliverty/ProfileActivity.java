package wisehands.me.deliverty;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtName, txtEmail;
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
                            String idToken = task.getResult().getToken();
//                            txtToken.setText(idToken);
                            // Send token to your backend via HTTPS

                            // Instantiate the RequestQueue.
                            RequestQueue queue = Volley.newRequestQueue(self);
                            String url ="http://192.168.1.3:8080/authenticate?token=" + idToken;

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


        }
    }

    private void acceptOrder() {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    private void cancelOrder() {
        Toast.makeText(ProfileActivity.this, "You are canceled delivery", Toast.LENGTH_SHORT).show();
    }


}


