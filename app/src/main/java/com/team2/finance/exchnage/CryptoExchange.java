package com.team2.finance.exchnage;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.mikephil.charting.charts.LineChart;


import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.Utility.Validation;
import com.team2.finance.Utility.VolleySingleton;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;



//export exel
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

//facebook
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;




public class CryptoExchange extends BaseActivity {


    EditText fromCurrency, toCurrency;
    Spinner fromDropdown, toDropdown , fromDropdown_graph;
    Button convert_bt , history_bt , export_graph_btn , export_convert_btn;
    RequestQueue requestQueue;
    ImageButton menu;

    //graph

    private LineChart mChart;

    private SimpleDateFormat simpleDateFormat;
    private String dateTime;
    private static final long ONE_DAY_MILLI_SECONDS = 24 * 60 * 60 * 1000;

    //export
    ArrayList<Float> Y_graph;
    ArrayList<String> x_graph = new ArrayList<>();

    //facebook
    private CallbackManager callbackManager;
    private LoginButton loginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = getLayoutInflater().inflate(R.layout.activity_crypto_exchange, frameLayout);

        //facebook
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);




        convert_bt = (Button) findViewById(R.id.convert_bt);
        history_bt = (Button) findViewById(R.id.history_bt);
        export_graph_btn = (Button) findViewById(R.id.graphExport_btn);
        export_convert_btn = (Button) findViewById(R.id.ConvertExport_btn);

        fromCurrency = findViewById(R.id.fromCurrency);
        toCurrency = findViewById(R.id.toCurrency);
        fromDropdown = findViewById(R.id.fromDropdown);
        toDropdown = findViewById(R.id.toDropdown);
        fromDropdown_graph = findViewById(R.id.fromDropdown_graph);

        menu = findViewById(R.id.menu);

        //for the graph
        mChart = findViewById(R.id.linechart);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(false);

        requestQueue = VolleySingleton.getInstance(this).getRequestQueue();

        //on Create first of all to init the Spinners and graph
        try {
            initSpinners();
            CryptoGraphBuild();
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
        // on tne click of the button update the graph to the chosen coin
        history_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CryptoGraphBuild();
            }
        });

        export_convert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toCurrency.getText().toString().equals(""))
                {
                    Toast.makeText(CryptoExchange.this, "Please Convert before Export", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try {

                        Convert_export();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        export_graph_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Graph_export();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        loginButton.setPermissions(Arrays.asList("user_gender , user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(),"GOOD",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Toast.makeText(getApplicationContext(),"EROOR",Toast.LENGTH_LONG).show();

            }
        });


    }


    private void Graph_export() throws IOException {

        boolean file_exist = false;

        try {
            File file = new File(getExternalFilesDir(null), "Graph History.xls");
            FileInputStream myInput = new FileInputStream(file);
            file_exist = true;

        }catch (IOException e)
        {
            e.printStackTrace();
        }


        String fromCoin = fromDropdown.getSelectedItem().toString();;
        String toCoin = toDropdown.getSelectedItem().toString();

        String fromValue = fromCurrency.getText().toString();
        String toValue = toCurrency.getText().toString();

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ");
        String strDate = dateFormat.format(date);


        // WRITE TO EXEL ------

        //check if the file is exist
        if(file_exist)
        {

            // Creating Input Stream
            File file = new File(getExternalFilesDir(null), "Graph History.xls");
            FileInputStream myInput = new FileInputStream(file);
            FileOutputStream outputStream=null;

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);


            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            int lastRow = mySheet.getLastRowNum();


            Cell cell=null;
            CellStyle cellStyle=myWorkBook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);


            Row row_header = mySheet.createRow(lastRow+2);
            Row row_x = mySheet.createRow(lastRow+3);
            Row row_y = mySheet.createRow(lastRow +4);


            cell = row_header.createCell(0);
            cell.setCellValue("Date & Time : ");
            cell.setCellStyle(cellStyle);

            cell = row_header.createCell(1);
            cell.setCellValue(strDate);
            cell.setCellStyle(cellStyle);

            cell = row_header.createCell(2);
            cell.setCellValue("Graph of :");
            cell.setCellStyle(cellStyle);

            cell = row_header.createCell(3);
            cell.setCellValue(fromDropdown_graph.getSelectedItem().toString());
            cell.setCellStyle(cellStyle);


            for ( int i = 0 ; i < 7 ; i++)
            {
                cell = row_x.createCell(i+1);
                cell.setCellValue(x_graph.get(i));
                cell.setCellStyle(cellStyle);
            }

            Collections.reverse(Y_graph);
            for(int i = 0 ; i < 7 ; i++)
            {
                cell = row_y.createCell(i+1);
                cell.setCellValue(String.valueOf(Y_graph.get(i)));
                cell.setCellStyle(cellStyle);
            }

            mySheet.setColumnWidth(0, (10 * 300));
            mySheet.setColumnWidth(1, (10 * 600));
            mySheet.setColumnWidth(2, (10 * 300));
            mySheet.setColumnWidth(3, (10 * 300));
            mySheet.setColumnWidth(4, (10 * 300));
            mySheet.setColumnWidth(5, (10 * 300));



            try {

                outputStream=new FileOutputStream(file);
                myWorkBook.write(outputStream);
                Toast.makeText(getApplicationContext(),"Saved on the Graph History File",Toast.LENGTH_LONG).show();
            } catch (java.io.IOException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),"NO OK",Toast.LENGTH_LONG).show();
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }




            //if end
        }
        else {
            //workbook
            Workbook myWorkBook = new HSSFWorkbook();
            Cell cell = null;

            //cell

            CellStyle cellStyle = myWorkBook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

//          Now we are creating sheet

            HSSFSheet mySheet = null;
            mySheet = (HSSFSheet) myWorkBook.createSheet("My Graph History");

            Row row_header = mySheet.createRow(0);
            Row row_x = mySheet.createRow(1);
            Row row_y = mySheet.createRow(2);


            cell = row_header.createCell(0);
            cell.setCellValue("Date & Time : ");
            cell.setCellStyle(cellStyle);

            cell = row_header.createCell(1);
            cell.setCellValue(strDate);
            cell.setCellStyle(cellStyle);

            cell = row_header.createCell(2);
            cell.setCellValue("Graph of :");
            cell.setCellStyle(cellStyle);

            cell = row_header.createCell(3);
            cell.setCellValue(fromDropdown_graph.getSelectedItem().toString());
            cell.setCellStyle(cellStyle);


            for ( int i = 0 ; i < 7 ; i++)
            {
                cell = row_x.createCell(i+1);
                cell.setCellValue(x_graph.get(i));
                cell.setCellStyle(cellStyle);
            }

            Collections.reverse(Y_graph);
            for(int i = 0 ; i < 7 ; i++)
            {
                cell = row_y.createCell(i+1);
                cell.setCellValue(String.valueOf(Y_graph.get(i)));
                cell.setCellStyle(cellStyle);
            }

            mySheet.setColumnWidth(0, (10 * 300));
            mySheet.setColumnWidth(1, (10 * 600));
            mySheet.setColumnWidth(2, (10 * 300));
            mySheet.setColumnWidth(3, (10 * 300));
            mySheet.setColumnWidth(4, (10 * 300));
            mySheet.setColumnWidth(5, (10 * 300));

            File file = new File(getExternalFilesDir(null), "Graph History.xls");
            FileOutputStream outputStream = null;

            try {

                outputStream=new FileOutputStream(file);
                myWorkBook.write(outputStream);

                Toast.makeText(getApplicationContext(), "Saved on the Graph History File", Toast.LENGTH_LONG).show();

            } catch (java.io.IOException e)
            {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "NO OK", Toast.LENGTH_LONG).show();
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


            //else finish
        }



    }

    private void Convert_export() throws IOException {

        boolean file_exist = false;

        try {
            File file = new File(getExternalFilesDir(null), "Converts.xls");
            FileInputStream myInput = new FileInputStream(file);
            file_exist = true;

        }catch (IOException e)
        {
            e.printStackTrace();
        }


        String fromCoin = fromDropdown.getSelectedItem().toString();;
        String toCoin = toDropdown.getSelectedItem().toString();

        String fromValue = fromCurrency.getText().toString();
        String toValue = toCurrency.getText().toString();

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ");
        String strDate = dateFormat.format(date);


        // WRITE TO EXEL ------

        //check if the file is exist
        if(file_exist)
        {

                // Creating Input Stream
            File file = new File(getExternalFilesDir(null), "Converts.xls");
            FileInputStream myInput = new FileInputStream(file);
            FileOutputStream outputStream=null;

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);


            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            int lastRow = mySheet.getLastRowNum();


            Cell cell=null;
            CellStyle cellStyle=myWorkBook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);


            Row row_conver =mySheet.createRow(lastRow+1);

            cell=row_conver.createCell(0);
            cell.setCellValue(strDate);
            cell.setCellStyle(cellStyle);

            cell=row_conver.createCell(1);
            cell.setCellValue(fromValue);
            cell.setCellStyle(cellStyle);

            cell=row_conver.createCell(2);
            cell.setCellValue(fromCoin);
            cell.setCellStyle(cellStyle);

            cell=row_conver.createCell(3);
            cell.setCellValue("=");
            cell.setCellStyle(cellStyle);

            cell=row_conver.createCell(4);
            cell.setCellValue(toValue);
            cell.setCellStyle(cellStyle);

            cell=row_conver.createCell(5);
            cell.setCellValue(toCoin);
            cell.setCellStyle(cellStyle);


            mySheet.setColumnWidth(0,(10*600));
            mySheet.setColumnWidth(1,(10*300));
            mySheet.setColumnWidth(2,(10*150));
            mySheet.setColumnWidth(3,(10*50));
            mySheet.setColumnWidth(4,(10*300));
            mySheet.setColumnWidth(5,(10*150));



            try {

               outputStream=new FileOutputStream(file);
               myWorkBook.write(outputStream);
                Toast.makeText(getApplicationContext(),"Saved on the Converts File",Toast.LENGTH_LONG).show();
            } catch (java.io.IOException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),"NO OK",Toast.LENGTH_LONG).show();
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }




            //if end
        }
        else {
            //workbook
            Workbook myWorkBook = new HSSFWorkbook();
            Cell cell = null;

            //cell

            CellStyle cellStyle = myWorkBook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

//          Now we are creating sheet

            HSSFSheet mySheet = null;
            mySheet = (HSSFSheet) myWorkBook.createSheet("My Convert History");

            Row row_date = mySheet.createRow(0);
            Row row_conver = mySheet.createRow(1);


            cell = row_date.createCell(0);
            cell.setCellValue("Date & Time");
            cell.setCellStyle(cellStyle);


            cell = row_conver.createCell(0);
            cell.setCellValue(strDate);
            cell.setCellStyle(cellStyle);

            cell = row_conver.createCell(1);
            cell.setCellValue(fromValue);
            cell.setCellStyle(cellStyle);

            cell = row_conver.createCell(2);
            cell.setCellValue(fromCoin);
            cell.setCellStyle(cellStyle);

            cell = row_conver.createCell(3);
            cell.setCellValue("=");
            cell.setCellStyle(cellStyle);

            cell = row_conver.createCell(4);
            cell.setCellValue(toValue);
            cell.setCellStyle(cellStyle);

            cell = row_conver.createCell(5);
            cell.setCellValue(toCoin);
            cell.setCellStyle(cellStyle);


            mySheet.setColumnWidth(0, (10 * 600));
            mySheet.setColumnWidth(1, (10 * 300));
            mySheet.setColumnWidth(2, (10 * 150));
            mySheet.setColumnWidth(3, (10 * 50));
            mySheet.setColumnWidth(4, (10 * 300));
            mySheet.setColumnWidth(5, (10 * 150));

            File file = new File(getExternalFilesDir(null), "Converts.xls");
            FileOutputStream outputStream = null;

            try {

            outputStream=new FileOutputStream(file);
            myWorkBook.write(outputStream);

            Toast.makeText(getApplicationContext(), "Saved on the Converts File", Toast.LENGTH_LONG).show();

            } catch (java.io.IOException e)
            {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "NO OK", Toast.LENGTH_LONG).show();
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


        //else finish
        }



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

                            //find the selected item and take the price
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject coin = jsonArray.getJSONObject(i);
                                String type = coin.getString("symbol");
                                if (fromDropdown.getSelectedItem().toString().equals(type)) {
                                    frompirce = Float.parseFloat(coin.getString("price")) * Float.parseFloat(fromCurrency.getText().toString());
                                }

                                if (toDropdown.getSelectedItem().toString().equals(type)) {
                                    toprice = Float.parseFloat(coin.getString("price"));
                                }

                                //Show the exchange price on the field
                                result = frompirce / toprice;
                                toCurrency.setText(String.valueOf(result));

                            }

                            Toast.makeText(CryptoExchange.this,"Converting "
                                            +fromDropdown.getSelectedItem().toString()+" to "
                                            +toDropdown.getSelectedItem().toString()
                                            ,Toast.LENGTH_SHORT).show();


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
                                String name = coin.getString("id").toUpperCase();
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

    public void CryptoGraphBuild()
    {
        ArrayList<Float> History_Values = new ArrayList<>();
        //defualt url
        String url_1w = "https://api.coinstats.app/public/v1/charts?period=1w&coinId=bitcoin";

        String url_1w2 = "https://api.coinstats.app/public/v1/charts?period=1w&coinId=";
        String CoinName = "bitcoin";

        try {
            CoinName = fromDropdown_graph.getSelectedItem().toString().toLowerCase();
            url_1w = url_1w2+CoinName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        String finalCoinName = CoinName;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url_1w, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //create array of all the objects innside coins Jsonfile
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = response.getJSONArray("chart");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                //get last 7 days values
                                if(i%24 == 0)
                                {
                                    JSONArray obj = jsonArray.getJSONArray(i);
                                    double value = obj.getDouble(1);
                                    History_Values.add( (float) value);
                                }

                                //update the last one to be the most updated no matter what
                                if(i == jsonArray.length()-1)
                                {
                                    JSONArray obj = jsonArray.getJSONArray(i);
                                    double value = obj.getDouble(1);
                                    History_Values.add(6,(float) value);
                                }

                            }

                            //for the export
                            Y_graph = History_Values;


                            //graph build
                            ArrayList<Entry> y = new ArrayList<>();

                            for(int day = 0 ; day < 7; day++)
                            {
                                y.add(new Entry(day,History_Values.get(day)));
                            }



                            //Line
                            String label = finalCoinName.toUpperCase() +" to UDS";
                            LineDataSet set1 = new LineDataSet(y,label);
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
                            //reverse the array to fit the graph

                            for(int i = 0 ; i < 7 ; i++)
                            {
                                x_graph.add(i,x.get(i));
                            }

                            x.set(0,"Today");
                            Collections.reverse(x);

                            //for the export




                            //y - prise (visability)
//                            YAxis leftYAxis = mChart.getAxisLeft();
                           YAxis rightYAxis = mChart.getAxisRight();
//                            leftYAxis.setEnabled(false);
                            rightYAxis.setEnabled(false);


                            //init
                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(set1);
                            LineData data = new LineData(dataSets);

                            mChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(x));
                            mChart.setData(data);
                            mChart.notifyDataSetChanged();
                            mChart.invalidate();
                            mChart.getDescription().setText("One-week");

                            Toast.makeText(CryptoExchange.this,"Graph updated.. "
                                    ,Toast.LENGTH_SHORT).show();



                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                            Log.e("GraphError", "catch inside onResponse function");
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}