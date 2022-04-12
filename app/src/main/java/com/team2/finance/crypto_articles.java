package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class crypto_articles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto_articles);

        TextView Article1_link = (TextView) findViewById(R.id.link_1);
        Article1_link.setMovementMethod(LinkMovementMethod.getInstance());

        TextView Article2_link = (TextView) findViewById(R.id.link_2);
        Article2_link.setMovementMethod(LinkMovementMethod.getInstance());

        TextView Article3_link = (TextView) findViewById(R.id.link_3);
        Article3_link.setMovementMethod(LinkMovementMethod.getInstance());

        TextView Article4_link = (TextView) findViewById(R.id.link_4);
        Article4_link.setMovementMethod(LinkMovementMethod.getInstance());
    }
}