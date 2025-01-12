package com.example.assignment3_relationshipcounter.main_screen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment3_relationshipcounter.R;
import com.example.assignment3_relationshipcounter.service.firestore.DataUtils;
import com.example.assignment3_relationshipcounter.service.models.User;
import com.example.assignment3_relationshipcounter.service.models.UserType;
import com.example.assignment3_relationshipcounter.utils.UserSession;
import com.google.android.gms.common.internal.AccountType;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity {
    private CardInputWidget cardInputWidget;
    private Button payButton;
    private Stripe stripe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        cardInputWidget = findViewById(R.id.card_input_widget);
        payButton = findViewById(R.id.pay_button);

        stripe = new Stripe(
                getApplicationContext(),
                PaymentConfiguration.getInstance(this).getPublishableKey()
        );

        payButton.setOnClickListener(v -> startPayment());
    }

    private void startPayment() {
        PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

        if (params == null) {
            Toast.makeText(this, "Invalid card details", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call your Firebase server function to create a PaymentIntent
        createPaymentIntent(params);
    }

    private void createPaymentIntent(PaymentMethodCreateParams params) {
        String url = "https://createpaymentintent-ffllg57gyq-uc.a.run.app";

        JSONObject payload = new JSONObject();
        try {
            payload.put("amount", 35000);
            payload.put("currency", "vnd");
            payload.put("payment_method_id", params.toParamMap().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                payload,
                response -> {
                    try {
                        String clientSecret = response.getString("clientSecret");
                        confirmPayment(clientSecret, params);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error creating PaymentIntent", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void confirmPayment(String clientSecret, PaymentMethodCreateParams params) {
        ConfirmPaymentIntentParams confirmParams =
                ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(params, clientSecret);

        stripe.confirmPayment(this, confirmParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        stripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
            @Override
            public void onSuccess(@NonNull PaymentIntentResult result) {
                PaymentIntent.Status status = result.getIntent().getStatus();
                if (status == PaymentIntent.Status.Succeeded) {
                    Toast.makeText(PaymentActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();
                    updateToPremium();
                } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                    Toast.makeText(PaymentActivity.this, "Payment failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                Toast.makeText(PaymentActivity.this, "Payment error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateToPremium() {
        User user = UserSession.getInstance().getCurrentUser();
        user.setAccountType(UserType.PREMIUM);
        new DataUtils().updateById("users", user.getId(), user, new DataUtils.NormalCallback<User>() {
            @Override
            public void onSuccess() {
                Toast.makeText(PaymentActivity.this, "Upgraded to Premium", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("paymentSuccess", true);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(PaymentActivity.this, "Error updating user: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}