package com.team2.finance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView first_name;
    private TextView last_name;
    private TextView password;
    private TextView email;
    private TextView phone_number;
    private CheckBox checkBox;

    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        first_name = (TextView) findViewById(R.id.firstName);
        last_name = (TextView) findViewById(R.id.lastName);
        password = (TextView) findViewById(R.id.password);
        email = (TextView) findViewById(R.id.emailAddress);
        phone_number = (TextView) findViewById(R.id.phoneNumber);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidation()) {
                    Log.d(TAG, "Valid");
                    register();
                } else {
                    Log.d(TAG, "Invalid");
                }
            }
        });

        first_name.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!Validation.isAlpha(first_name.getText().toString())) {
                    first_name.setError("Invalid Name");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

        });

        last_name.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!Validation.isAlpha(last_name.getText().toString())) {
                    last_name.setError("Invalid Name");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

        });

        email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!Validation.isEmailValid(email.getText().toString())) {
                    email.setError("Invalid Email Address ");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

        });

        password.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!Validation.isValidPassword(password.getText().toString())) {
                    password.setError("Minimum eight characters, at least one letter and one number");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

        });

        phone_number.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (!Validation.isValidPhoneNumber(phone_number.getText().toString())) {
                    phone_number.setError("Invalid phone number");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#85ff7a")));
                }
            }
        });
    }

    private boolean checkValidation() {
        boolean validator = true;
        if (!Validation.isAlpha(first_name.getText().toString())) {
            first_name.setError("Invalid Name");
            validator = false;
        }
        if (!Validation.isAlpha(last_name.getText().toString())) {
            last_name.setError("Invalid Name");
            validator = false;
        }
        if (!Validation.isEmailValid(email.getText().toString())) {
            email.setError("Invalid Email Address");
            validator = false;
        }
        if (!Validation.isValidPassword(password.getText().toString())) {
            password.setError("Minimum eight characters, at least one letter and one number");
            validator = false;
        }
        if (!Validation.isValidPhoneNumber(phone_number.getText().toString())) {
            phone_number.setError("Invalid phone number");
            validator = false;
        }
        if (!checkBox.isChecked()) {
            checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
            validator = false;
        }
        return validator;
    }

    private void register() {
        String user_email, user_password;
        user_email = email.getText().toString();
        user_password = password.getText().toString();

        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> userData = new HashMap<>();
                        userData.put("Uid", user.getUid());
                        userData.put("email_address", email.getText().toString());
                        userData.put("first_name", first_name.getText().toString());
                        userData.put("last_name", last_name.getText().toString());
                        userData.put("phone_number", phone_number.getText().toString());
                        userData.put("type", "register");
                        userData.put("Vip", false);
                        db.collection("Users") // Add a new document with a generated ID
                                .add(userData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                    }
                }).addOnFailureListener(Throwable::printStackTrace);
    }
}