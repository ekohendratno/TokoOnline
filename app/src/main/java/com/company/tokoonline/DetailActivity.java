package com.company.tokoonline;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int key = getIntent().getIntExtra("key",0);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });

        findViewById(R.id.actionCart).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), CartActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);

        });


        findViewById(R.id.actionBuy).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), CheckOutActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);

        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}