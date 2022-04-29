package com.team2.finance.exchnage;

import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.Utility.Validation;
import com.team2.finance.Utility.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;

public class Exchange extends BaseActivity {

    EditText fromCurrency, toCurrency;
    Spinner fromDropdown, toDropdown,fromDropdown_h,toDropdown_h;
    Button convert_bt;
    Button history_bt;
    ImageButton menu;
    private LineChart mChart;
    private RequestQueue requestQueue;
    private static final String TAG = "Exchange";

    private String dateTime;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_exchange, frameLayout);
        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();
        //Currency convert
        convert_bt = (Button) findViewById(R.id.convert_bt);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        fromDropdown = (Spinner)findViewById(R.id.fromDropdown);
        toDropdown = (Spinner)findViewById(R.id.toDropdown);

        //Historical rate
        history_bt = (Button) findViewById(R.id.history_bt);
        fromDropdown_h = (Spinner)findViewById(R.id.fromDropdown_h);
        toDropdown_h = (Spinner)findViewById(R.id.toDropdown_h);
        mChart = (LineChart)findViewById(R.id.historical_chart);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);
        mChart.setTouchEnabled(true);
        //to hide right Y and top X border
        YAxis rightYAxis = mChart.getAxisRight();
        rightYAxis.setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //Default values chart
        defaultChart();
        try {
            initSpinners(mAuth);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        convert_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Validation.isEmpty(fromCurrency.getText().toString())) {
                    Convert();
                } else {
                    fromCurrency.setError("Empty value");
                }
            }
        });

        history_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showHistory();
            }
        });
    }

    private void initSpinners(FirebaseAuth mAuth) throws JSONException {
        if (mAuth != null) {
            getCurrency();
        } else {
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Exchange.this,
                    R.array.currency_array, android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            toDropdown.setAdapter(adapter);
            fromDropdown.setAdapter(adapter);

            toDropdown_h.setAdapter(adapter);
            fromDropdown_h.setAdapter(adapter);
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

                        fromDropdown_h.setAdapter(adapter);
                        toDropdown_h.setAdapter(adapter);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, String.valueOf(error));
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }


    private void showHistory() {

        if (fromDropdown_h.getSelectedItem().toString().equals(toDropdown_h.getSelectedItem().toString())) {
            //TODO
        } else {
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateTime = simpleDateFormat.format(calendar.getTime()).toString();
            String url = "https://api.frankfurter.app/2022-04-01"+".."+ dateTime + "?&from=" + fromDropdown_h.getSelectedItem().toString() + "&to=" + toDropdown_h.getSelectedItem().toString();

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
                            int x = 0;
                            Iterator keysToCopyIterator = jsonObject.keys();
                            ArrayList<Entry> rateList = new ArrayList<>();
                            ArrayList<String> dateList = new ArrayList<String>();
                            while (keysToCopyIterator.hasNext()) {
                                String date = (String) keysToCopyIterator.next();
                                dateList.add(date);
                                try {
                                    Double currency_rate = jsonObject.getJSONObject(date).getDouble(toDropdown_h.getSelectedItem().toString());
                                    rateList.add(new Entry(x,currency_rate.floatValue()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                x++;
                            }

                            LineDataSet lineDataSet = new LineDataSet(rateList,"Rates");
                            //customize line
                            lineDataSet.setLineWidth(2f);
                            lineDataSet.setValueTextSize(10f);
                            lineDataSet.setCircleColor(Color.rgb(255,165,0));
                            lineDataSet.setColor(Color.rgb(218,165,32));
                            ArrayList<ILineDataSet> iLineDataSets= new ArrayList<>();
                            iLineDataSets.add(lineDataSet);
                            LineData lineData = new LineData(iLineDataSets);
                            //String setter in x-Axis
                            mChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(dateList));
                            mChart.getXAxis().setLabelCount(4);
                            mChart.setData(lineData);
                            mChart.notifyDataSetChanged();
                            mChart.invalidate();

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

    void defaultChart(){
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateTime = simpleDateFormat.format(calendar.getTime()).toString();
        ArrayList<String> x = new ArrayList<>(Arrays.asList(dateTime,dateTime,dateTime,dateTime));
        ArrayList<Entry> y = new ArrayList<>();
        y.add(new Entry(0,1.0f));
        y.add(new Entry(1,1.0f));
        y.add(new Entry(2,1.0f));
        y.add(new Entry(3,1.0f));


        LineDataSet lineDataSet = new LineDataSet(y,"Rates");
        //customize line
        lineDataSet.setLineWidth(2f);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setCircleColor(Color.rgb(255,165,0));
        lineDataSet.setColor(Color.rgb(218,165,32));
        ArrayList<ILineDataSet> iLineDataSets= new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        //String setter in x-Axis
        mChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(x));
        mChart.setData(lineData);
        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }
}
