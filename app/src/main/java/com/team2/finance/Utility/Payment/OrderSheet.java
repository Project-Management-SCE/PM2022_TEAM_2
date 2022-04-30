package com.team2.finance.Utility.Payment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.team2.finance.R;

public class OrderSheet {

    private static final String SHOWING_KEY = "showing";
    private Runnable onPayWithCardClickListener;
    private Runnable onPayWithGoogleClickListener;
    private View payWithGoogleButton;
    private boolean payWithGoogleButtonEnabled;
    private boolean showing;
    private TextView price;

    public static final String Name = "priceKey";

    public void setOnPayWithCardClickListener(Runnable listener) {
        onPayWithCardClickListener = listener;
    }

    public void setOnPayWithGoogleClickListener(Runnable listener) {
        onPayWithGoogleClickListener = listener;
    }

    public void setPayWithGoogleEnabled(boolean enabled) {
        payWithGoogleButtonEnabled = enabled;
        if (payWithGoogleButton != null) {
            payWithGoogleButton.setEnabled(enabled);
        }
    }

    public void show(Activity activity) {
        showing = true;
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        View sheetView = LayoutInflater.from(activity).inflate(R.layout.sheet_order, null);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        String value = preferences.getString(Name, "");
        price = sheetView.findViewById(R.id.price);
        price.setText(value);


        View closeButton = sheetView.findViewById(R.id.close_sheet_button);
        View payWithCardButton = sheetView.findViewById(R.id.pay_with_card_button);
        payWithCardButton.setOnClickListener(v -> {
            dialog.dismiss();
            showing = false;
            onPayWithCardClickListener.run();
        });

        payWithGoogleButton = sheetView.findViewById(R.id.pay_with_google_button);
        payWithGoogleButton.setEnabled(payWithGoogleButtonEnabled);
        payWithGoogleButton.setOnClickListener(v -> {
            dialog.dismiss();
            showing = false;
            onPayWithGoogleClickListener.run();
        });

        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(sheetView);
        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) sheetView.getParent());
        dialog.setOnShowListener(dialogInterface -> behavior.setPeekHeight(sheetView.getHeight()));
        dialog.setOnCancelListener(dialog1 -> showing = false);
        dialog.setCancelable(true);

        dialog.show();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SHOWING_KEY, showing);
    }

    public void onRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        boolean showing = savedInstanceState.getBoolean(SHOWING_KEY);
        if (showing) {
            show(activity);
        }
    }
}
