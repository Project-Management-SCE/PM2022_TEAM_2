package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private static final String TAG = "ForgotPassword";

    private FirebaseAuth mAuth;

    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        email = (TextView) findViewById(R.id.emailAddress);

        Button reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    Log.d(TAG, "Valid");
                    sendReset();
                } else {
                    Log.d(TAG, "Invalid");
                }
            }
        });
    }

    private void sendReset() {
        mAuth.sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Email sent.");
                        Toast.makeText(this.getApplicationContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this.getApplicationContext(), "Email not sent.", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private boolean checkValidation() {
        boolean validator = true;

        if (!Validation.isEmailValid(email.getText().toString())) {
            email.setError("Invalid Email Address");
            validator = false;
        }
        return validator;
    }
}
