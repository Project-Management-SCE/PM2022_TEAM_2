package com.team2.finance.Pages;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.team2.finance.R;
import com.team2.finance.Utility.Payment.ConfigHelper;
import com.team2.finance.Utility.Payment.ExampleApplication;
import com.team2.finance.Utility.Payment.GooglePayChargeClient;
import com.team2.finance.Utility.Payment.OrderSheet;

import sqip.CardEntry;
import sqip.GooglePay;
import sqip.InAppPaymentsSdk;


public class CheckoutActivity extends AppCompatActivity {

    private static String TAG = "CheckoutActivity";
    CardView card1;
    CardView card2;
    CardView card3;

    LottieAnimationView confetti1;
    LottieAnimationView confetti2;
    LottieAnimationView confetti3;

    ImageButton payButton;
    ImageButton backButton;


    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 1;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private GooglePayChargeClient googlePayChargeClient;
    private PaymentsClient paymentsClient;
    private OrderSheet orderSheet;

    SharedPreferences preferences;

    public static final String Name = "priceKey";

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

        payButton = findViewById(R.id.pay_button);
        backButton = findViewById(R.id.back);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Name, "");
        editor.apply();

        googlePayChargeClient = (GooglePayChargeClient) getLastCustomNonConfigurationInstance();
        if (googlePayChargeClient == null) {
            googlePayChargeClient = ExampleApplication.createGooglePayChargeClient(this);
        }
        googlePayChargeClient.onActivityCreated(this);

        paymentsClient = Wallet.getPaymentsClient(this,
                new Wallet.WalletOptions.Builder()
                        .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                        .build());

        orderSheet = new OrderSheet();

        enableGooglePayButton(orderSheet);
        orderSheet.setOnPayWithCardClickListener(this::startCardEntryActivity);
        orderSheet.setOnPayWithGoogleClickListener(this::startGooglePayActivity);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });


        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = preferences.getString(Name, "");
                if (value.equals("")) {
                    showToast();
                } else {
                    if (InAppPaymentsSdk.INSTANCE.getSquareApplicationId().equals("REPLACE_ME")) {
                        showMissingSquareApplicationIdDialog();
                    } else {
                        showOrderSheet();
                    }
                }
            }
        });


        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confetti1.setVisibility(View.VISIBLE);
                confetti2.setVisibility(View.INVISIBLE);
                confetti3.setVisibility(View.INVISIBLE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Name, "12.99");
                editor.apply();

            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confetti1.setVisibility(View.INVISIBLE);
                confetti2.setVisibility(View.VISIBLE);
                confetti3.setVisibility(View.INVISIBLE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Name, "59.99");
                editor.apply();

            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confetti1.setVisibility(View.INVISIBLE);
                confetti2.setVisibility(View.INVISIBLE);
                confetti3.setVisibility(View.VISIBLE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(Name, "99.99");
                editor.apply();
            }
        });
    }

    public void showToast() {

        View toastLayout = getLayoutInflater().inflate(R.layout.toast_layout, null);
        Toast toast = new Toast(getApplicationContext());
        toast.setView(toastLayout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public GooglePayChargeClient onRetainCustomNonConfigurationInstance() {
        return googlePayChargeClient;
    }

    private void enableGooglePayButton(OrderSheet orderSheet) {
        Task<Boolean> readyToPayTask =
                paymentsClient.isReadyToPay(GooglePay.createIsReadyToPayRequest());
        readyToPayTask.addOnCompleteListener(this,
                (task) -> orderSheet.setPayWithGoogleEnabled(task.isSuccessful()));
    }

    private void startCardEntryActivity() {
        CardEntry.startCardEntryActivity(this);
    }

    private void startGooglePayActivity() {
        TransactionInfo transactionInfo = TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice("1.00")
                .setCurrencyCode("USD")
                .build();

        PaymentDataRequest paymentDataRequest =
                GooglePay.createPaymentDataRequest(ConfigHelper.GOOGLE_PAY_MERCHANT_ID,
                        transactionInfo);

        Task<PaymentData> googlePayActivityTask = paymentsClient.loadPaymentData(paymentDataRequest);

        AutoResolveHelper.resolveTask(googlePayActivityTask, this, LOAD_PAYMENT_DATA_REQUEST_CODE);
    }

    private void showMissingSquareApplicationIdDialog() {
        new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(R.string.missing_application_id_title)
                .setMessage(Html.fromHtml(getString(R.string.missing_application_id_message)))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> showOrderSheet())
                .show();
    }

    private void showOrderSheet() {
        orderSheet.show(CheckoutActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CardEntry.handleActivityResult(data, cardEntryActivityResult -> {
            if (cardEntryActivityResult.isSuccess()) {
                if (!ConfigHelper.serverHostSet()) {
                    showServerHostNotSet();
                } else {
                    showSuccessCharge();
                }
            } else if (cardEntryActivityResult.isCanceled()) {
                Resources res = getResources();
                int delayMs = res.getInteger(R.integer.card_entry_activity_animation_duration_ms);
                handler.postDelayed(this::showOrderSheet, delayMs);
            }
        });

        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            handleGooglePayActivityResult(resultCode, data);
        }
    }

    private void handleGooglePayActivityResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (!ConfigHelper.merchantIdSet()) {
                showMerchantIdNotSet();
                return;
            }
            PaymentData paymentData = PaymentData.getFromIntent(data);
            if (paymentData != null && paymentData.getPaymentMethodToken() != null) {
                String googlePayToken = paymentData.getPaymentMethodToken().getToken();
                googlePayChargeClient.charge(googlePayToken);
            }
        } else {
            // The customer canceled Google Pay or an error happened, show the order sheet again.
            showOrderSheet();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            googlePayChargeClient.cancel();
        }
        googlePayChargeClient.onActivityDestroyed();
    }

    public void showError(String message) {
        showOkDialog(R.string.unsuccessful_order, message);
    }

    public void showSuccessCharge() {
        showOkDialog(R.string.successful_order_title, getString(R.string.successful_order_message));
        // TODO update DB
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void showServerHostNotSet() {
        showOkDialog(R.string.server_host_not_set_title, Html.fromHtml(getString(R.string.server_host_not_set_message)));
    }

    private void showMerchantIdNotSet() {
        showOkDialog(R.string.merchant_id_not_set_title, Html.fromHtml(getString(R.string.merchant_id_not_set_message)));
    }

    private void showOkDialog(int titleResId, CharSequence message) {
        new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(titleResId)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void showNetworkErrorRetryPayment(Runnable retry) {
        new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setTitle(R.string.network_failure_title)
                .setMessage(getString(R.string.network_failure))
                .setPositiveButton(R.string.retry, (dialog, which) -> retry.run())
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        orderSheet.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        orderSheet.onRestoreInstanceState(this, savedInstanceState);
    }

}