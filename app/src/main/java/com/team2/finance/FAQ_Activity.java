package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.skydoves.expandablelayout.ExpandableLayout;

public class FAQ_Activity extends AppCompatActivity {
    int g = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);


        ExpandableLayout ex
                = (ExpandableLayout) findViewById(R.id.expandable);
        ex.setOnClickListener(new ExpandableLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (g == 0) {
                    g = 1;
                    ex.expand();
                } else {
                    g = 0;
                    ex.collapse();
                }
            }
        });
    }
}