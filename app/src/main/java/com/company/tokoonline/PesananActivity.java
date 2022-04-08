package com.company.tokoonline;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PesananActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}