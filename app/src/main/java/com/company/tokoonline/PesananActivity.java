package com.company.tokoonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.PesananAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.TransaksiItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PesananActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView;
    private PesananAdapter cartAdapter;

    private LinearLayout empty_view;

    TabLayout tabLayout;
    
    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);


        context = PesananActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText( "Loading. Please wait..." );


        String uid = sharedpreferences.getString("uid", "");

        if( TextUtils.isEmpty(uid) ){

            startActivity(new Intent(context, LoginActivity.class));
            finish();

        }

        empty_view = findViewById(R.id.empty_view);
        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


        getDataPesanan("Pending");

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            getDataPesanan("Pending");

            swipeRefresh.setRefreshing(false);
        });



        tabLayout = findViewById(R.id.tabs);

        ViewGroup.LayoutParams params = tabLayout.getLayoutParams();
        params.height = 80;
        tabLayout.setLayoutParams(params);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.e("p", String.valueOf(tab.getPosition()));

                if (tab.getPosition() == 1) {
                    getDataPesanan("Diperiksa");
                }else if (tab.getPosition() == 2) {
                    getDataPesanan("Bayar");
                }else if (tab.getPosition() == 3) {
                    getDataPesanan("Dikirim");
                }else if (tab.getPosition() == 4) {
                    getDataPesanan("Diterima");
                }else if (tab.getPosition() == 5) {
                    getDataPesanan("Dibatalkan");
                }else{
                    getDataPesanan("Pending");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    private void getDataPesanan(String status){

        empty_view.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        dialog.show();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/pesanan?uid="+uid+"&status="+status, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {

                    final List<TransaksiItem> transaskiItemList = new ArrayList();
                    JSONArray transaskiItemArray= req.getJSONArray("response");
                    for(int a=0; a<transaskiItemArray.length() ;a++){
                        JSONObject transaksi = transaskiItemArray.getJSONObject(a);


                        final List<BarangItem> barangItemList = new ArrayList();
                        JSONArray transaksi_barang_data = transaksi.getJSONArray("transaksi_barang_data");
                        for(int b=0; b<transaksi_barang_data.length() ;b++) {
                            JSONObject barang = transaksi_barang_data.getJSONObject(b);
                            barangItemList.add(new BarangItem(
                                    barang.getInt("barang_id"),
                                    barang.getString("barang_judul"),
                                    barang.getString("barang_gambar"),
                                    barang.getInt("barang_harga"),
                                    barang.getInt("barang_stok"),
                                    barang.getInt("barang_beli")
                            ));
                        }


                        transaskiItemList.add(new TransaksiItem(
                                barangItemList,
                                transaksi.getInt("transaksi_id"),
                                transaksi.getString("transaksi_nota"),
                                transaksi.getString("transaksi_jumlah"),
                                transaksi.getString("transaksi_harga"),
                                transaksi.getInt("transaksi_total_bayar"),
                                transaksi.getInt("transaksi_ongkir"),
                                transaksi.getString("transaksi_status"),
                                transaksi.getString("transaksi_tanggal"),
                                transaksi.getString("transaksi_barang"),
                                transaksi.getString("transaksi_catatan"),
                                transaksi.getString("transaksi_user")
                        ));
                    }

                    if(transaskiItemList.size() > 0){
                        empty_view.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        cartAdapter = new PesananAdapter(context, transaskiItemList);
                        recyclerView.setAdapter(cartAdapter);
                    }

                }else{
                }
            } catch (Exception e) {
                Log.e("VOLLEY","Authentication error: " + e.getMessage());

            }
        }, error -> {
            dialog.dismiss();

            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Terjadi gangguan!")
                    .setContentText("Mengalami gangguan ketika mengambil data!")
                    .show();

            Log.e("VOLLEY", error.toString());

        });



        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}