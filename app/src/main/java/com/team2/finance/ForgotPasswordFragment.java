package com.team2.finance;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private View binding;
    private String TAG = "ForgotPassword";
    private FragmentManager fragmentManager;
    private TextView email;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        View view = binding.getRootView();

        email = (TextView) view.findViewById(R.id.emailAddress);

        Button reset = (Button) view.findViewById(R.id.reset);
        reset.setOnClickListener(this);

        return view;
    }

    public ForgotPasswordFragment(FragmentManager fragmentManager) {

        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reset:
                if (checkValidation()) {
                    mAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                    Toast.makeText(this.getContext(), "Email sent.", Toast.LENGTH_SHORT).show();
                                    fragmentManager.popBackStack();
                                } else {
                                    Toast.makeText(this.getContext(), "Email not sent.", Toast.LENGTH_SHORT).show();

                                }
                            });
                    break;
                }
        }
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