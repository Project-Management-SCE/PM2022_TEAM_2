package com.team2.finance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private static final String TAG = "LoginFragment";
    private View binding;

    private TextView first_name;
    private TextView last_name;
    private TextView password;
    private TextView email;
    private TextView phone_number;
    private CheckBox checkBox;
    private TextView use_terms;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflater.inflate(R.layout.fragment_register, container, false);
        View view = binding.getRootView();

        Button register = (Button) view.findViewById(R.id.register);
        register.setOnClickListener(this);

        ImageButton back = (ImageButton) view.findViewById(R.id.back);
        back.setOnClickListener(this);

        use_terms = (TextView) view.findViewById(R.id.useTerms);

        use_terms.setOnClickListener(this);

        first_name = (TextView) view.findViewById(R.id.firstName);
        last_name = (TextView) view.findViewById(R.id.lastName);
        password = (TextView) view.findViewById(R.id.password);
        email = (TextView) view.findViewById(R.id.emailAddress);
        phone_number = (TextView) view.findViewById(R.id.phoneNumber);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);

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
                    checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#F3B533")));
                }
            }
        });

        return view;
    }

    public RegisterFragment() {

        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.useTerms:
                termsPopup();
                break;
            case R.id.back:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.register:
                if (checkValidation()) {
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(getActivity(), task -> {
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
                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                                }
                            }).addOnFailureListener(Throwable::printStackTrace);
                }
                break;
        }
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

    public void termsPopup() {

        // Create the object of
        // AlertDialog Builder class
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(getContext());

        // Set the message show for the Alert time
        builder.setMessage(getString(R.string.terms_and_conditions));

        // Set Alert Title
        builder.setTitle("Terms and conditions");

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.

        builder
                .setPositiveButton(
                        "Done",
                        new DialogInterface
                                .OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                                // When the user click yes button
                                // then app will close
                            }
                        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();

        // Show the Alert Dialog box
        alertDialog.show();
    }
}