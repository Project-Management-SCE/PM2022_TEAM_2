package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.skydoves.expandablelayout.ExpandableLayout;

public class FAQ_Activity extends AppCompatActivity {
    int g = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);


        ExpandableLayout Question1
                = (ExpandableLayout) findViewById(R.id.expandable_a);
        Question1.setOnClickListener(new ExpandableLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (g == 0) {
                    g = 1;
                    Question1.expand();
                } else {
                    g = 0;
                    Question1.collapse();
                }
            }
        });

        ExpandableLayout Question2
                = (ExpandableLayout) findViewById(R.id.expandable_a1);
        Question2.setOnClickListener(new ExpandableLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (g == 0) {
                    g = 1;
                    Question2.expand();
                } else {
                    g = 0;
                    Question2.collapse();
                }
            }
        });

        ExpandableLayout Question3
                = (ExpandableLayout) findViewById(R.id.expandable_a2);
        Question3.setOnClickListener(new ExpandableLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (g == 0) {
                    g = 1;
                    Question3.expand();
                } else {
                    g = 0;
                    Question3.collapse();
                }
            }
        });
        ExpandableLayout Question4
                = (ExpandableLayout) findViewById(R.id.expandable_a3);
        Question4.setOnClickListener(new ExpandableLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (g == 0) {
                    g = 1;
                    Question4.expand();
                } else {
                    g = 0;
                    Question4.collapse();
                }
            }
        });
        ExpandableLayout Question5
                = (ExpandableLayout) findViewById(R.id.expandable_a4);
        Question5.setOnClickListener(new ExpandableLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (g == 0) {
                    g = 1;
                    Question5.expand();
                } else {
                    g = 0;
                    Question5.collapse();
                }
            }
        });



    }
}