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
import android.widget.Toast;


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

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

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

public class Exchange extends BaseActivity {

    EditText fromCurrency, toCurrency;
    Spinner fromDropdown, toDropdown,fromDropdown_h,toDropdown_h;
    Button convert_bt;
    Button history_bt;
    //export
    Button export_graph_btn , export_convert_btn;
    private static final long ONE_DAY_MILLI_SECONDS = 24 * 60 * 60 * 1000;
    ArrayList<Float> Y_graph;
    ArrayList<String> x_graph = new ArrayList<>();

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

        //export
        export_convert_btn = (Button) findViewById(R.id.ConvertExport_btn);
        export_graph_btn = (Button) findViewById(R.id.graphExport_btn);



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

        export_convert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toCurrency.getText().toString().equals(""))
                {
                    Toast.makeText(Exchange.this, "Please Convert before Export", Toast.LENGTH_SHORT).show();
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

        history_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showHistory();
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


            //for export
            ArrayList<Float> History_Values = new ArrayList<>();

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
                                x_graph.add(date);
                                try {
                                    Double currency_rate = jsonObject.getJSONObject(date).getDouble(toDropdown_h.getSelectedItem().toString());
                                    rateList.add(new Entry(x,currency_rate.floatValue()));
                                    //for export
                                    History_Values.add(currency_rate.floatValue());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                x++;
                            }

                            //for export
                            Y_graph = History_Values;

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


    private void Convert_export() throws IOException {

        boolean file_exist = false;

        try {
            File file = new File(getExternalFilesDir(null), "Converts.xls");
            FileInputStream myInput = new FileInputStream(file);
            file_exist = true;

        } catch (IOException e) {
            e.printStackTrace();
        }


        String fromCoin = fromDropdown.getSelectedItem().toString();
        ;
        String toCoin = toDropdown.getSelectedItem().toString();

        String fromValue = fromCurrency.getText().toString();
        String toValue = toCurrency.getText().toString();

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ");
        String strDate = dateFormat.format(date);


        // WRITE TO EXEL ------

        //check if the file is exist
        if (file_exist) {

            // Creating Input Stream
            File file = new File(getExternalFilesDir(null), "Converts.xls");
            FileInputStream myInput = new FileInputStream(file);
            FileOutputStream outputStream = null;

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);


            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            int lastRow = mySheet.getLastRowNum();


            Cell cell = null;
            CellStyle cellStyle = myWorkBook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);


            Row row_conver = mySheet.createRow(lastRow + 1);

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


            try {

                outputStream = new FileOutputStream(file);
                myWorkBook.write(outputStream);
                Toast.makeText(getApplicationContext(), "Saved on the Converts File", Toast.LENGTH_LONG).show();
            } catch (java.io.IOException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "NO OK", Toast.LENGTH_LONG).show();
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }


            //if end
        } else {
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

                outputStream = new FileOutputStream(file);
                myWorkBook.write(outputStream);

                Toast.makeText(getApplicationContext(), "Saved on the Converts File", Toast.LENGTH_LONG).show();

            } catch (java.io.IOException e) {
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
    }//convert export end

    private void Graph_export() throws IOException {

        if (fromDropdown_h.getSelectedItem().toString().equals(toDropdown_h.getSelectedItem().toString())) {
            Toast.makeText(getApplicationContext(),"Export failed - Please select two different coins",Toast.LENGTH_LONG).show();

        }
        else
        {
            boolean file_exist = false;

            try {
                File file = new File(getExternalFilesDir(null), "Graph History.xls");
                FileInputStream myInput = new FileInputStream(file);
                file_exist = true;

            }catch (IOException e)
            {
                e.printStackTrace();
            }




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
                cell.setCellValue(fromDropdown_h.getSelectedItem().toString());
                cell.setCellStyle(cellStyle);

                cell = row_header.createCell(4);
                cell.setCellValue("To");
                cell.setCellStyle(cellStyle);

                cell = row_header.createCell(5);
                cell.setCellValue(toDropdown_h.getSelectedItem().toString());
                cell.setCellStyle(cellStyle);


                for ( int i = 0 ; i < Y_graph.size() ; i++)
                {
                    cell = row_x.createCell(i+1);
                    cell.setCellValue(x_graph.get(i));
                    cell.setCellStyle(cellStyle);
                }

                //Collections.reverse(Y_graph);
                for(int i = 0 ; i < Y_graph.size() ; i++)
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
                cell.setCellValue(fromDropdown_h.getSelectedItem().toString());
                cell.setCellStyle(cellStyle);

                cell = row_header.createCell(4);
                cell.setCellValue("To");
                cell.setCellStyle(cellStyle);

                cell = row_header.createCell(5);
                cell.setCellValue(toDropdown_h.getSelectedItem().toString());
                cell.setCellStyle(cellStyle);


                for ( int i = 0 ; i < Y_graph.size() ; i++)
                {
                    cell = row_x.createCell(i+1);
                    cell.setCellValue(x_graph.get(i));
                    cell.setCellStyle(cellStyle);
                }


                for(int i = 0 ; i < Y_graph.size() ; i++)
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





    }




}
