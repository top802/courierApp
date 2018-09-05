package wisehands.me.deliverty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import static wisehands.me.deliverty.ProfileActivity.API_HOST;
import static wisehands.me.deliverty.ProfileActivity.jwttoken;

public class MessageOrder extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Messaging";
    private TextView order, address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massege_order);

        order = findViewById(R.id.order);
        address = findViewById(R.id.address);

        findViewById(R.id.bn_accept).setOnClickListener(this);
        findViewById(R.id.bn_cancel).setOnClickListener(this);
        findViewById(R.id.bn_finish).setOnClickListener(this);

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        String orderr = intent.getStringExtra("order");
        String addres = intent.getStringExtra("address");

        Log.d(TAG, "MessageOrder: data: " + orderr + "  " + addres);
        if(addres != null && orderr != null) {
            order.setText(orderr);
            address.setText(addres);
        } else {
            order.setText("Error");
            address.setText("Error");
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bn_accept:
                startService(new Intent(this, ServiceGPS.class));
                acceptOrder();
                isactivecourier(false);
                break;
            case R.id.bn_cancel:
                order.setText("Please wait for next order!");
                address.setText("Please wait!");
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.bn_finish:
                order.setText("Please wait for next order!");
                address.setText("Please wait for next order!");
                finishOrder();
                isactivecourier(true);
                stopService(new Intent(this, ServiceGPS.class));
                break;
        }

    }

    private void isactivecourier(boolean isactive) {
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

    private void acceptOrder() {
        boolean inProgress = true;
        String urlPath = "readytowork";
        String params = String.format("jwttoken=%s&inProgress=%s", jwttoken, inProgress);
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

    private void finishOrder() {
        stopService(new Intent(this, ServiceGPS.class));
        boolean inProgress = false;
        String urlPath = "readytowork";
        String params = String.format("jwttoken=%s&inProgress=%s", jwttoken, inProgress);
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
                        Log.i(TAG, "FAILRequest: you aren't ready to cancel order");
                    }
                }
        );
        queue.add(postRequest);
    }



}
