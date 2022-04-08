package com.company.tokoonline;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);




        findViewById(R.id.actionBack).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), MainActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);

        });


        findViewById(R.id.actionKonfirmasi).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), KonfirmasiActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);

        });
    }
}