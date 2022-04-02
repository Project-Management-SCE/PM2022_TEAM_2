package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView password;
    private TextView email;

    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        password = (TextView) findViewById(R.id.password);
        email = (TextView) findViewById(R.id.emailAddress);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    Log.d(TAG, "Valid");
                } else {
                    Log.d(TAG, "Invalid");
                }
            }
        });

    }

    private boolean checkValidation() {
        boolean validator = true;
        if (!Validation.isEmailValid(email.getText().toString())) {
            email.setError("Invalid Email Address");
            validator = false;
        }
        if (!Validation.isEmpty(password.getText().toString())) {
            password.setError("Invalid");
            validator = false;
        }
        return validator;
    }
}