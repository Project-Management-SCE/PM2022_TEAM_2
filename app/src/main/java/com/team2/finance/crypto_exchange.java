package com.team2.finance;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class crypto_exchange extends AppCompatActivity {


    EditText fromCurrency,toCurrency;
    Spinner fromDropdown, toDropdown;
    Button convert_bt;

    TextView result;
    RequestQueue requestQueue;

    String API_key = "cdc03d89-a441-4710-9e72-5362b76f28b3";
    String string_response = "result";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_exchange);

        convert_bt = (Button) findViewById(R.id.convert_bt);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        fromDropdown = findViewById(R.id.fromDropdown);
        toDropdown = findViewById(R.id.toDropdown);
        result = findViewById(R.id.result);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        try {
            initSpinners();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        convert_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Convert();

            }
        });




    }

    /// function that will convert the chosen crypto coin
    private void Convert()
    {
        String url = "https://api.coinstats.app/public/v1/coins?skip=0&limit=10&currency=USD";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("coins");

                            for(int i = 0 ; i < jsonArray.length() ; i++)
                            {
                                JSONObject coin = jsonArray.getJSONObject(i);
                                String type = coin.getString("name");
                                String price = coin.getString("price");
                                result.append(type + price + "\n");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        requestQueue.add(jsonObjectRequest);


    }

    //function that will initiate the spinners
    private void initSpinners() throws JSONException
    {
        //array of all the Crypto symbols to display at the spinners
        ArrayList<String> coins_symbol = new ArrayList<String>();

        String url = "https://api.coinstats.app/public/v1/coins?skip=0&limit=15&currency=USD";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("coins");


                            for(int i = 0 ; i < jsonArray.length() ; i++)
                            {
                                JSONObject coin = jsonArray.getJSONObject(i);
                                String symbol = coin.getString("symbol");
                                coins_symbol.add(symbol);


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        requestQueue.add(jsonObjectRequest);


        //add to the spinners

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, coins_symbol);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromDropdown.setAdapter(adapter);
        toDropdown.setAdapter(adapter);

    }



}