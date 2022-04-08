package com.company.tokoonline;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CheckOutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);



        findViewById(R.id.actionCheckout).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), PaymentActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);

        });
    }
}