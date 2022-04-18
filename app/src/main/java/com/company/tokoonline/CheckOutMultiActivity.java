package com.company.tokoonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.CheckoutAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.KeranjangItem;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckOutMultiActivity extends AppCompatActivity {

    private ArrayList<KeranjangItem> keranjang;


    private Context context;
    private SharedPreferences sharedpreferences;

    private RecyclerView recyclerView;
    private CheckoutAdapter checkoutAdapter;


    TextView tv_ongkir;
    TextView tv_harga_subtotal_produk;
    TextView tv_harga_subtotal_pengiriman;
    TextView tv_harga_total_pembayaran;
    TextView tv_harga_total_pembayaran2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_multi);

        context = CheckOutMultiActivity.this;

        Bundle bundle = getIntent().getExtras();
        keranjang = bundle.getParcelableArrayList("keranjang");


        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );


        tv_ongkir = findViewById(R.id.ongkir);
        tv_harga_subtotal_produk = findViewById(R.id.harga_subtotal_produk);
        tv_harga_subtotal_pengiriman = findViewById(R.id.harga_subtotal_pengiriman);
        tv_harga_total_pembayaran = findViewById(R.id.harga_total_pembayaran);
        tv_harga_total_pembayaran2 = findViewById(R.id.harga_total_pembayaran2);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });

        findViewById(R.id.actionCheckout).setOnClickListener(v -> {

            //new sendDataCheckout().execute();

        });

        new getDataOngkir().execute();


        checkoutAdapter = new CheckoutAdapter(context, keranjang);
        recyclerView.setAdapter(checkoutAdapter);

    }



    private class getDataOngkir extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/ongkir?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            int ongkir = 0;
                            int total = 0;
                            int total_ongkir = 0;

                            ongkir = req.getInt("response");

                            for(int a=0; a<keranjang.size() ;a++){
                                KeranjangItem keranjangItem = keranjang.get(a);

                                Log.e("a", keranjangItem.toString());

                                //int harga = keranjangItem.barang_harga;
                                //int jumlah = keranjangItem.keranjang_jumlah;

                                //total+= harga*jumlah;
                            }

                            //total_ongkir = ( total+ongkir );

                            //tv_ongkir.setText( Config.formatRupiah( ongkir) );
                            //tv_harga_subtotal_produk.setText( Config.formatRupiah( total ) );
                            //tv_harga_subtotal_pengiriman.setText( Config.formatRupiah( total_ongkir ) );
                            //tv_harga_total_pembayaran.setText( Config.formatRupiah( total_ongkir ) );
                            //tv_harga_total_pembayaran2.setText( Config.formatRupiah( total_ongkir ) );

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
        }
    }


    /**

    private class getDataProdukDetail extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/detail?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {
                            JSONObject jsonObject = req.getJSONObject("response");



                            String barang_gambar = jsonObject.getString("barang_gambar");
                            harga = jsonObject.getInt("barang_harga");
                            int total = beli*harga;


                            tv_txtview.setText( jsonObject.getString("barang_judul") );
                            tv_beli.setText( "x"+beli );
                            tv_harga.setText( String.valueOf( total ) );
                            Picasso.with(context).load( barang_gambar ).into( imageview );


                            ongkir = jsonObject.getInt("barang_ongkir");
                            harga_total = ongkir+total;

                            tv_ongkir.setText( Config.formatRupiah( ongkir) );
                            tv_harga_subtotal_produk.setText( Config.formatRupiah( total) );
                            tv_harga_subtotal_pengiriman.setText( Config.formatRupiah( harga_total) );
                            tv_harga_total_pembayaran.setText( Config.formatRupiah( harga_total) );
                            tv_harga_total_pembayaran2.setText( Config.formatRupiah( harga_total) );


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
        }
    }


    private class sendDataCheckout extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.restapi + "/api/checkout?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            Intent myIntent = new Intent(context, PaymentActivity.class);
                            myIntent.putExtra("key", req.getString("nota"));
                            myIntent.putExtra("total", req.getString("total"));
                            myIntent.putExtra("unik", req.getString("unik"));
                            startActivity(myIntent);
                            finish();

                        }

                    } catch (Exception e) {
                        Log.e("VOLLEY","Authentication error: " + e.getMessage());

                    }
                }, error -> {
                    Log.e("VOLLEY", error.toString());

                }) {
                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<>();
                        params.put("harga", String.valueOf(harga));
                        params.put("harga_total", String.valueOf(harga_total));
                        params.put("ongkir", String.valueOf(ongkir));
                        params.put("beli", String.valueOf(beli));

                        return params;
                    }
                };




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
        }
    }
     */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}