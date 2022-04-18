package com.company.tokoonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {

    private String uid;
    private int key;
    private int beli;
    private int ongkir;
    private int harga;
    private int harga_total;

    private Context context;
    private SharedPreferences sharedpreferences;

    TextView alamat_pengiriman;

    ImageView imageview;
    TextView tv_txtview;
    TextView tv_harga;
    TextView tv_beli;


    TextView tv_ongkir;
    TextView tv_harga_subtotal_produk;
    TextView tv_harga_subtotal_pengiriman;
    TextView tv_harga_total_pembayaran;
    TextView tv_harga_total_pembayaran2;
    EditText et_catatan;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        context = CheckOutActivity.this;
        key = getIntent().getIntExtra("key",0);
        beli = getIntent().getIntExtra("beli",0);

        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        uid = sharedpreferences.getString("uid","");


        dialog = new ProgressDialog(context);
        dialog.setMessage("Loading. Please wait...");
        dialog.setCancelable(false);

        if( TextUtils.isEmpty(uid) ){

            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }


        alamat_pengiriman = findViewById(R.id.alamat_pengiriman);


        imageview = findViewById(R.id.imageview);
        tv_txtview = findViewById(R.id.txtview);
        tv_harga = findViewById(R.id.harga);
        tv_beli = findViewById(R.id.beli);


        tv_ongkir = findViewById(R.id.ongkir);
        tv_harga_subtotal_produk = findViewById(R.id.harga_subtotal_produk);
        tv_harga_subtotal_pengiriman = findViewById(R.id.harga_subtotal_pengiriman);
        tv_harga_total_pembayaran = findViewById(R.id.harga_total_pembayaran);
        tv_harga_total_pembayaran2 = findViewById(R.id.harga_total_pembayaran2);
        et_catatan = findViewById(R.id.catatan);

        String alamat_pengiriman_text = "";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_penerima","") +" | ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_notelp","") +"\n";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_jalan","") +" ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_provinsi","") +" ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_kabupaten","") +" ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_kecamatan","") +" ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_desa","") +" RT ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_rt","") +"/RW ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_rw","") +" ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_kodepos","");

        alamat_pengiriman.setText( alamat_pengiriman_text );


        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });

        findViewById(R.id.actionCheckout).setOnClickListener(v -> {

            new sendDataCheckout().execute();

        });


        new getDataProdukDetail().execute();
    }



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
                            myIntent.putExtra("key", req.getInt("key"));
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
                        params.put("catatan", et_catatan.getText().toString());

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}