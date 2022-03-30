package com.team2.finance;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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

import java.util.Collections;


public class Exchange extends AppCompatActivity {

    EditText fromCurrency;
    TextView toCurrency;
    Spinner fromDropdown, toDropdown;
    Button convert_bt;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        convert_bt = (Button) findViewById(R.id.convert_bt);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        fromDropdown = findViewById(R.id.fromDropdown);
        toDropdown = findViewById(R.id.toDropdown);
        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        initSpinners();

        convert_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Convert();
            }
        });

    }

    private void initSpinners(){
        String array[] = new String[] {"USD","ILS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromDropdown.setAdapter(adapter);
        toDropdown.setAdapter(adapter);
    }

    private void Convert() {
        String url="https://frankfurter.app/latest?amount=" + fromCurrency.getText().toString() + "&from=" + fromDropdown.getSelectedItem().toString() + "&to=" + toDropdown.getSelectedItem().toString();

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
                        // TODO: Handle error
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }
}
