package com.team2.finance.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.os.Bundle;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.team2.finance.R;

public class PaymentActivity extends AppCompatActivity {

    private static String TAG = "PaymentActivity";
    CardView card1;
    CardView card2;
    CardView card3;
    private int priceOption = 0;
    LottieAnimationView confetti1;
    LottieAnimationView confetti2;
    LottieAnimationView confetti3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);

        confetti1 = findViewById(R.id.confetti1);
        confetti2 = findViewById(R.id.confetti2);
        confetti3 = findViewById(R.id.confetti3);

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confetti1.setVisibility(View.VISIBLE);
                confetti2.setVisibility(View.INVISIBLE);
                confetti3.setVisibility(View.INVISIBLE);
                priceOption = 1;
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confetti1.setVisibility(View.INVISIBLE);
                confetti2.setVisibility(View.VISIBLE);
                confetti3.setVisibility(View.INVISIBLE);
                priceOption = 2;
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confetti1.setVisibility(View.INVISIBLE);
                confetti2.setVisibility(View.INVISIBLE);
                confetti3.setVisibility(View.VISIBLE);
                priceOption = 3;
            }
        });
    }
}