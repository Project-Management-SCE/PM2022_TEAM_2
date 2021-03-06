package com.team2.finance.Utility.Payment;

import com.squareup.moshi.Moshi;


import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ConfigHelper {

    public static final String GOOGLE_PAY_MERCHANT_ID = "REPLACE_ME";
    private static final String CHARGE_SERVER_HOST = "team2-finance.herokuapp.com/"; // team2-finance.herokuapp.com/
    private static final String CHARGE_SERVER_URL = "https://" + CHARGE_SERVER_HOST + "/";

    public static boolean serverHostSet() {
        return !CHARGE_SERVER_HOST.equals("REPLACE_ME");
    }

    public static boolean merchantIdSet() {
        return !GOOGLE_PAY_MERCHANT_ID.equals("REPLACE_ME");
    }

    public static Retrofit createRetrofitInstance() {
        Moshi moshi = new Moshi.Builder().build();
        return new Retrofit
                .Builder()
                .baseUrl(ConfigHelper.CHARGE_SERVER_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build();
    }
}
