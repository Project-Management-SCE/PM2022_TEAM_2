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
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.Utility.Validation;
import com.team2.finance.Utility.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CryptoExchange extends BaseActivity {


    EditText fromCurrency, toCurrency;
    Spinner fromDropdown, toDropdown , fromDropdown_graph;
    Button convert_bt;
    RequestQueue requestQueue;
    ImageButton menu;

    //graph

    private LineChart mChart;

    private SimpleDateFormat simpleDateFormat;
    private String dateTime;
    private static final long ONE_DAY_MILLI_SECONDS = 24 * 60 * 60 * 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_crypto_exchange, frameLayout);

        convert_bt = (Button) findViewById(R.id.convert_bt);
        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        fromDropdown = findViewById(R.id.fromDropdown);
        toDropdown = findViewById(R.id.toDropdown);
        fromDropdown_graph = findViewById(R.id.fromDropdown_graph);

        menu = findViewById(R.id.menu);

        mChart = findViewById(R.id.linechart);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //on Create first of all to init the Spinners and graph
        try {
            initSpinners();
            CryptoGraphDefualt();
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


        // on tne click of the button run the Convert Function
        convert_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.isEmpty(fromCurrency.getText().toString())) {
                    Convert();
                } else {
                    fromCurrency.setError("Empty value");
                }
            }
        });
    }

    /// function that will convert the chosen crypto coin
    private void Convert() {

        String url = "https://api.coinstats.app/public/v1/coins?skip=0&limit=20&currency=USD";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("coins");

                            float frompirce = 1;
                            float toprice = 1;
                            float result = 1;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject coin = jsonArray.getJSONObject(i);
                                String type = coin.getString("symbol");
                                if (fromDropdown.getSelectedItem().toString().equals(type)) {
                                    //check that the field isnt empty ( if it does put 1)
                                    if (fromCurrency.getText().toString().equals("")) {
                                        frompirce = Float.parseFloat(coin.getString("price"));
                                        fromCurrency.setText("1");
                                    } else {
                                        frompirce = Float.parseFloat(coin.getString("price")) * Float.parseFloat(fromCurrency.getText().toString());
                                    }

                                }

                                if (toDropdown.getSelectedItem().toString().equals(type)) {
                                    toprice = Float.parseFloat(coin.getString("price"));
                                }

                                //Show the exchange price on the field
                                result = frompirce / toprice;
                                toCurrency.setText(String.valueOf(result));

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("Conver Eroor", "Somthing went Wrong on the onResponse Function");
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
    private void initSpinners() throws JSONException {
        //array of all the Crypto symbols to display at the spinners
        ArrayList<String> coins_symbol = new ArrayList<>();
        ArrayList<String> coins_name = new ArrayList<>();


        String url = "https://api.coinstats.app/public/v1/coins?skip=0&limit=20&currency=USD";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //create array of all the objects innside coins Jsonfile
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = response.getJSONArray("coins");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject coin = jsonArray.getJSONObject(i);
                                String symbol = coin.getString("symbol");
                                String name = coin.getString("name");
                                coins_symbol.add(symbol);
                                coins_name.add(name);


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("initSpinners", "catch inside onResponse function");
                        }

                        //add to the spinners
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CryptoExchange.this,
                                android.R.layout.simple_spinner_item,
                                (ArrayList<String>) coins_symbol);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        fromDropdown.setAdapter(adapter);
                        toDropdown.setAdapter(adapter);

                        ArrayAdapter<String> adapter_graph = new ArrayAdapter<String>(CryptoExchange.this,
                                android.R.layout.simple_spinner_item,
                                (ArrayList<String>) coins_name);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        fromDropdown_graph.setAdapter(adapter_graph);
//


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        requestQueue.add(jsonObjectRequest);

    }

    public void CryptoGraphDefualt()
    {
        ArrayList<Float> History_Values = new ArrayList<>();

        String url_1w = "https://api.coinstats.app/public/v1/charts?period=1w&coinId=bitcoin";




        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url_1w, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //create array of all the objects innside coins Jsonfile
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = response.getJSONArray("chart");




                            for (int i = 0; i < jsonArray.length(); i++) {

                                if(i%24 == 0)
                                {

                                    JSONArray obj = jsonArray.getJSONArray(i);
                                    double value = obj.getDouble(1);
                                    History_Values.add( (float) value);


                                }

                                if(i == jsonArray.length()-1)
                                {
                                    JSONArray obj = jsonArray.getJSONArray(i);
                                    double value = obj.getDouble(1);
                                    History_Values.add(6,(float) value);
                                }

                            }



                            //graph build

                            ArrayList<Entry> y = new ArrayList<>();

                            for(int day = 0 ; day < 7; day++)
                            {

                                y.add(new Entry(day,History_Values.get(day)));
                                Log.e("dayValue" ,String.valueOf(History_Values.get(day)));
                            }

                            //Line
                            LineDataSet set1 = new LineDataSet(y,"Bitcoin to USD");
                            set1.setFillAlpha(110);
                            set1.setLineWidth(2f);
                            set1.setCircleColor(Color.rgb(255,165,0));
                            set1.setColor(Color.rgb(218,165,32));
                            set1.setValueTextSize(10f);

                            // x - days

                            simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
                            //get today
                            dateTime = java.time.LocalDate.now().toString();
                            Date date = simpleDateFormat.parse(dateTime);

                            //array of days as string
                            ArrayList<String> x = new ArrayList<>();

                            //add today to the array
                            x.add(dateTime);

                            //add the others days to array
                            for(int i =1 ; i < 7 ; i++)
                            {
                                long previousDayMilliSeconds = date.getTime() - (ONE_DAY_MILLI_SECONDS * i);
                                Date previousDate = new Date(previousDayMilliSeconds);
                                String previousDateStr = simpleDateFormat.format(previousDate);
                                x.add(previousDateStr);
                            }
                            //reverse the array
                            Collections.reverse(x);




                            //y - prise
//                            YAxis leftYAxis = mChart.getAxisLeft();
//                            YAxis rightYAxis = mChart.getAxisRight();
//                            leftYAxis.setEnabled(false);
//                            rightYAxis.setEnabled(false);


                            //init
                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(set1);
                            LineData data = new LineData(dataSets);

                            mChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(x));
                            mChart.setData(data);
                            mChart.notifyDataSetChanged();
                            mChart.invalidate();
                            mChart.getDescription().setText("One-week");






                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Log.e("GraphError", "catch inside onResponse function");
                        }


//


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        requestQueue.add(jsonObjectRequest);












    }


}