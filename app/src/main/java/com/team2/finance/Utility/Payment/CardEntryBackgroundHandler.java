package com.team2.finance.Utility.Payment;

import android.content.res.Resources;

import com.team2.finance.R;

import sqip.Call;
import sqip.CardDetails;
import sqip.CardEntryActivityCommand;
import sqip.CardNonceBackgroundHandler;

public class CardEntryBackgroundHandler implements CardNonceBackgroundHandler {

    private final ChargeCall.Factory chargeCallFactory;
    private final Resources resources;

    public CardEntryBackgroundHandler(ChargeCall.Factory chargeCallFactory,
                                      Resources resources) {
        this.chargeCallFactory = chargeCallFactory;
        this.resources = resources;
    }

    @Override
    public CardEntryActivityCommand handleEnteredCardInBackground(CardDetails cardDetails) {

        Call<ChargeResult> chargeCall = chargeCallFactory.create(cardDetails.getNonce());
        ChargeResult chargeResult = chargeCall.execute();

        if (chargeResult.success) {
            return new CardEntryActivityCommand.Finish();
        } else if (chargeResult.networkError) {
            return new CardEntryActivityCommand.ShowError(resources.getString(R.string.network_failure));
        } else {
            return new CardEntryActivityCommand.ShowError(chargeResult.errorMessage);
        }
    }
}
