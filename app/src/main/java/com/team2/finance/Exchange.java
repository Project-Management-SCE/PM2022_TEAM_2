package com.team2.finance;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Exchange extends BaseActivity {

    EditText fromCurrency, toCurrency;
    Spinner fromDropdown, toDropdown;
    Button convert_bt;
    ImageButton menu;
    private RequestQueue requestQueue;
    private int user_type = 1;
    private static final String TAG = "Exchange";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_exchange, frameLayout);

        menu = findViewById(R.id.menu);
        convert_bt = (Button) findViewById(R.id.convert_bt);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        fromDropdown = findViewById(R.id.fromDropdown);
        toDropdown = findViewById(R.id.toDropdown);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        try {
            initSpinners();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        convert_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Validation.isEmpty(fromCurrency.getText().toString())) {
                    Convert();
                } else {
                    fromCurrency.setError("Empty value");
                }
            }
        });
    }

    private void initSpinners() throws JSONException {
        if (user_type == 1) {
            getCurrency();
        } else {
            ArrayList<String> foreign_currency = new ArrayList<>(Arrays.asList("USD", "ILS", "EUR"));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, foreign_currency);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fromDropdown.setAdapter(adapter);
            toDropdown.setAdapter(adapter);
        }
    }

    private void Convert() {

        if (fromDropdown.getSelectedItem().toString().equals(toDropdown.getSelectedItem().toString())) {
            toCurrency.setText(fromCurrency.getText().toString());
        } else {
            String url = "https://frankfurter.app/latest?amount=" + fromCurrency.getText().toString() + "&from=" + fromDropdown.getSelectedItem().toString() + "&to=" + toDropdown.getSelectedItem().toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = response.getJSONObject("rates");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String rate = String.valueOf(jsonObject.getDouble(toDropdown.getSelectedItem().toString()));
                                toCurrency.setText(rate);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, String.valueOf(error));
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }
    }

    private void getCurrency() throws JSONException {
        String url = "https://frankfurter.app/latest";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = response.getJSONObject("rates");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Iterator keysToCopyIterator = jsonObject.keys();
                        ArrayList<String> keysList = new ArrayList<String>();
                        while (keysToCopyIterator.hasNext()) {
                            String key = (String) keysToCopyIterator.next();
                            keysList.add(key);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Exchange.this, android.R.layout.simple_spinner_item, (ArrayList<String>) keysList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        fromDropdown.setAdapter(adapter);
                        toDropdown.setAdapter(adapter);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, String.valueOf(error));
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}
