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

        Bundle bundle = getIntent().getExtras();
        String addres = bundle.getString("address");
        String orderr = bundle.getString("order");

        Log.d(TAG, "onCreate: data: " + orderr + addres);

        order.setText(orderr);
        address.setText(addres);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bn_accept:
                acceptOrder();
                break;
            case R.id.bn_cancel:
                order.setText("Please wait for next order!");
                address.setText("Please wait!");
                cancelOrder();
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }

    }


    private void acceptOrder() {
        startService(new Intent(this, ServiceGPS.class));
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
        stopService(new Intent(this, ServiceGPS.class));
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
                        Log.i(TAG, "FAILRequest: you aren't ready to cancel order");
                    }
                }
        );
        queue.add(postRequest);
    }



}
