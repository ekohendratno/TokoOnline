package com.company.tokoonline.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.company.tokoonline.LoginActivity;
import com.company.tokoonline.R;
import com.company.tokoonline.Splash;
import com.company.tokoonline.admin.adapters.PesananAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.TransaksiItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminPesanan extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView recyclerView;
    private PesananAdapter cartAdapter;

    private ProgressBar progressBar;
    private ProgressDialog progressBarDialog;
    private LinearLayout empty_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_pesanan);


        context = AdminPesanan.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        progressBar = findViewById(R.id.progressBar);
        progressBarDialog= new ProgressDialog(context);
        progressBarDialog.setMessage("Tunggu sebentar...");
        progressBarDialog.setCancelable(false);


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


        new getDataPesanan().execute();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            new getDataPesanan().execute();

            swipeRefresh.setRefreshing(false);
        });
    }


    private class getDataPesanan extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/admin_pesanan?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            final List<TransaksiItem> transaskiItemList = new ArrayList();
                            JSONArray transaskiItemArray= req.getJSONArray("response");
                            for(int a=0; a<transaskiItemArray.length() ;a++){
                                JSONObject transaksi = transaskiItemArray.getJSONObject(a);
                                transaskiItemList.add(new TransaksiItem(
                                        transaksi.getInt("barang_id"),
                                        transaksi.getString("barang_judul"),
                                        transaksi.getString("barang_gambar"),

                                        transaksi.getInt("transaksi_id"),
                                        transaksi.getString("transaksi_nota"),
                                        transaksi.getInt("transaksi_jumlah"),
                                        transaksi.getInt("transaksi_harga"),
                                        transaksi.getInt("transaksi_harga_total"),
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
                            }

                            cartAdapter = new PesananAdapter(context, transaskiItemList);
                            recyclerView.setAdapter(cartAdapter);

                        }else{
                        }
                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                });



                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                requestQueue.add(stringRequest);



            }catch (Exception e){
                e.printStackTrace();
            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.GONE);

            if (progressBarDialog.isShowing()) {
                progressBarDialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            empty_view.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressBarDialog.show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}