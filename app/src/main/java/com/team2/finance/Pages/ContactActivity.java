package com.team2.finance.Pages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.team2.finance.R;
import com.team2.finance.Utility.BaseActivity;
import com.team2.finance.Utility.Validation;

public class ContactActivity extends BaseActivity {
    ImageButton menu;
    TextInputLayout name,email,subject,msg;
    Button send_bt;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = getLayoutInflater().inflate(R.layout.activity_contact, frameLayout);
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


        name = findViewById(R.id.contact_name_id);
        email = findViewById(R.id.contact_email_id);
        subject = findViewById(R.id.subject_id);
        msg = findViewById(R.id.msg_id);

        send_bt = (Button) findViewById(R.id.send_bt);
        send_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValidation()) {
                    sendEmail();
                }
            }
        });

    }

    protected void mail(){


    }

    protected void sendEmail() {

        String[] TO = {"artiobo@ac.sce.ac.il"};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getEditText().getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, name.getEditText().getText().toString()+
                " send this massage:\n"+msg.getEditText().getText().toString());
//        emailIntent.setType("message/rfc822");
        emailIntent.setData(Uri.parse("mailto:"));

        try {
            startActivity(emailIntent);
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
        }
    }

    private boolean checkValidation() {
        boolean validator = true;
        if (!Validation.isAlpha(name.getEditText().getText().toString())) {
            name.setError("Invalid Name");
            validator = false;
        }
        if (!Validation.isEmailValid(email.getEditText().getText().toString())) {
            email.setError("Invalid Email Address");
            validator = false;
        }
        if (subject.getEditText().getText().toString().isEmpty()){
            subject.setError("Please enter subject");
            validator = false;
        }
        if (msg.getEditText().getText().toString().isEmpty()){
            subject.setError("Please enter massage");
            validator = false;
        }
        return validator;
    }

}