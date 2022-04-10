package com.company.tokoonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private static Context context;
    private static SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private static RecyclerView recyclerView;
    private static KeranjangAdapter cartAdapter;

    static AppCompatTextView harga_total_pembayaran;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        context = CartActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );

        harga_total_pembayaran = findViewById(R.id.harga_total_pembayaran);


        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


        new getDataKeranjang().execute();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            new getDataKeranjang().execute();

            swipeRefresh.setRefreshing(false);
        });


    }


    public class getDataKeranjang extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/keranjang?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
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

                                    harga_total_pembayaran.setText( Config.formatRupiah( total_bayar ) );

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

                                    harga_total_pembayaran.setText( Config.formatRupiah( total_bayar ) );
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

                                    harga_total_pembayaran.setText( Config.formatRupiah( total_bayar ) );
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
        }

        @Override
        protected void onPreExecute() {
            harga_total_pembayaran.setText("Rp 0");
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}