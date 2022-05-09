package com.company.tokoonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.KeranjangAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.KeranjangItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartActivity extends AppCompatActivity {

    private static Context context;
    private static SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private static RecyclerView recyclerView;
    private static KeranjangAdapter cartAdapter;

    static AppCompatTextView harga_total_pembayaran;

    TabLayout tabLayout;

    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        context = CartActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        String uid = sharedpreferences.getString("uid", "");

        if( TextUtils.isEmpty(uid) ){

            startActivity(new Intent(context, LoginActivity.class));
            finish();

        }

        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText( "Loading. Please wait..." );

        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );

        harga_total_pembayaran = findViewById(R.id.harga_total_pembayaran);


        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


        getDataKeranjang("Semua");

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            getDataKeranjang("Semua");

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
                    getDataKeranjang("Diskon");
                }else if (tab.getPosition() == 2) {
                    getDataKeranjang("BeliLagi");
                }else{
                    getDataKeranjang("Semua");
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


    public void getDataKeranjang(String status) {

        harga_total_pembayaran.setText("Rp 0");
        dialog.show();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/keranjang?uid="+uid+"&status="+status, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();

            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {

                    final List<KeranjangItem> keranjangItemList = new ArrayList();
                    JSONArray keranjangItemArray= req.getJSONArray("response");
                    for(int a=0; a<keranjangItemArray.length() ;a++){
                        JSONObject keranjang = keranjangItemArray.getJSONObject(a);
                        keranjangItemList.add(new KeranjangItem(
                                keranjang.getInt("barang_id"),
                                keranjang.getString("barang_judul"),
                                keranjang.getString("barang_gambar"),
                                keranjang.getInt("barang_harga"),
                                keranjang.getInt("barang_stok"),

                                keranjang.getInt("keranjang_id"),
                                keranjang.getInt("keranjang_jumlah")
                        ));
                    }


                    List<KeranjangItem> currentSelectedItems = new ArrayList<>();
                    cartAdapter = new KeranjangAdapter(context, keranjangItemList, new KeranjangAdapter.OnItemCheckListener() {
                        @Override
                        public void onItemCheck(KeranjangItem item, int beli) {

                            item.setJumlah(beli);
                            currentSelectedItems.add(item);

                            int total_bayar = 0;

                            for(int a=0; a<currentSelectedItems.size() ;a++){
                                KeranjangItem keranjangItem = currentSelectedItems.get(a);

                                int harga = keranjangItem.barang_harga;

                                total_bayar += harga*beli;

                            }

                            //harga_total_pembayaran.setText( Config.formatRupiah( total_bayar ) );

                        }

                        @Override
                        public void onItemUncheck(KeranjangItem item, int beli) {

                            item.setJumlah(beli);
                            currentSelectedItems.remove(item);

                            int total_bayar = 0;

                            for(int a=0; a<currentSelectedItems.size() ;a++){
                                KeranjangItem keranjangItem = currentSelectedItems.get(a);

                                int harga = keranjangItem.barang_harga;

                                total_bayar += harga*beli;

                            }

                            //harga_total_pembayaran.setText( Config.formatRupiah( total_bayar ) );
                        }

                        @Override
                        public void onItemJumlah(KeranjangItem item, int beli) {
                            item.setJumlah(beli);

                            int total_bayar = 0;
                            for(int a=0; a<currentSelectedItems.size() ;a++){
                                KeranjangItem keranjangItem = currentSelectedItems.get(a);

                                int harga = keranjangItem.barang_harga;

                                total_bayar += harga*beli;
                            }

                            //harga_total_pembayaran.setText( Config.formatRupiah( total_bayar ) );
                        }

                    });
                    recyclerView.setAdapter(cartAdapter);


                    findViewById(R.id.actionCheckout).setOnClickListener(v -> {


                        Intent intent = new Intent(v.getContext(), CheckOutMultiActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("keranjang", (ArrayList<? extends Parcelable>) currentSelectedItems);
                        intent.putExtras(bundle);

                        startActivity(intent);

                    });


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