package com.company.tokoonline.admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.AkunActivity;
import com.company.tokoonline.CartActivity;
import com.company.tokoonline.MainActivity;
import com.company.tokoonline.PencarianActivity;
import com.company.tokoonline.PesananActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.Splash;
import com.company.tokoonline.adapters.KategoriAdapter;
import com.company.tokoonline.adapters.RekomendasiAdapter;
import com.company.tokoonline.adapters.SliderImageAdapter;
import com.company.tokoonline.adapters.TerkiniAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.config.PicassoImageLoadingService;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.KategoriItem;
import com.company.tokoonline.models.SlideItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;

public class AdminDashboard extends AppCompatActivity {


    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);


        context = AdminDashboard.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        findViewById(R.id.actionBack).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), MainActivity.class);
            v.getContext().startActivity(myIntent);

        });


        findViewById(R.id.actionBank).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), AdminBank.class);
            v.getContext().startActivity(myIntent);

        });

        findViewById(R.id.actionKategori).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), AdminKategori.class);
            v.getContext().startActivity(myIntent);


        });

        findViewById(R.id.actionBarang).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), AdminBarang.class);
            v.getContext().startActivity(myIntent);

        });

        findViewById(R.id.actionTransaksi).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), AdminPesanan.class);
            v.getContext().startActivity(myIntent);

        });



        findViewById(R.id.akun).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), AkunActivity.class);
            v.getContext().startActivity(myIntent);

        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}