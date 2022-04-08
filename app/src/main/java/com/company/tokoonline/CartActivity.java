package com.company.tokoonline;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.tokoonline.adapters.CartAdapter;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });

        findViewById(R.id.actionBuy).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), CheckOutActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);

        });




        recyclerView = findViewById(R.id.recyle_view0);

        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );
        cartAdapter = new CartAdapter(this);
        recyclerView.setAdapter(cartAdapter);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}