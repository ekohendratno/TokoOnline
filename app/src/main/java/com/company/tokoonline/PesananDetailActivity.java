package com.company.tokoonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.company.tokoonline.models.TransaksiItem;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PesananDetailActivity extends AppCompatActivity {

    private int key;

    private Context context;
    private SharedPreferences sharedpreferences;

    TextView alamat_pengiriman;

    ImageView imageview;
    TextView txtview;
    TextView harga;
    TextView beli;
    TextView nota;
    TextView waktu_pemesanan;
    TextView waktu_pembayaran;
    TextView waktu_pengiriman;
    TextView waktu_pesanan_selesai;
    TextView catatan;
    MaterialButton actionBatal;
    MaterialButton actionCheckout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_detail);


        context = PesananDetailActivity.this;
        key = getIntent().getIntExtra("key",0);
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);

        alamat_pengiriman = findViewById(R.id.alamat_pengiriman);

        imageview = findViewById(R.id.imageview);
        txtview = findViewById(R.id.txtview);
        harga = findViewById(R.id.harga);
        beli = findViewById(R.id.beli);
        nota = findViewById(R.id.nota);
        waktu_pemesanan = findViewById(R.id.waktu_pemesanan);
        waktu_pembayaran = findViewById(R.id.waktu_pembayaran);
        waktu_pengiriman = findViewById(R.id.waktu_pengiriman);
        waktu_pesanan_selesai = findViewById(R.id.waktu_pesanan_selesai);
        catatan = findViewById(R.id.catatan);
        actionBatal = findViewById(R.id.actionBatal);
        actionCheckout = findViewById(R.id.actionCheckout);


        String alamat_pengiriman_text = "";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_nama","") +" | ";
        alamat_pengiriman_text += sharedpreferences.getString("pengiriman_notelp","") +"\n";
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


        new getDataPesananDetail().execute();
    }


    private class getDataPesananDetail extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/pesanan_detail?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {
                            JSONObject jsonObject = req.getJSONObject("response");

                            String transaksi_status = jsonObject.getString("transaksi_status");


                            actionBatal.setVisibility(View.GONE);
                            if( transaksi_status.equalsIgnoreCase("Pending") ){
                                actionCheckout.setText("Bayar");
                                actionCheckout.setOnClickListener(v -> {

                                    Intent myIntent = new Intent(context, PaymentActivity.class);
                                    myIntent.putExtra("key", key);
                                    startActivity(myIntent);
                                    finish();

                                });

                                actionBatal.setVisibility(View.VISIBLE);
                                actionBatal.setOnClickListener(v -> {

                                    new sendDataCheckoutCencel().execute();

                                });
                            }else if( transaksi_status.equalsIgnoreCase("Diperiksa") ){
                                actionCheckout.setText("Diperiksa");
                                actionCheckout.setEnabled(false);

                            }else if( transaksi_status.equalsIgnoreCase("Bayar") ){
                                actionCheckout.setText("Disiapkan");
                                actionCheckout.setEnabled(false);

                            }else if( transaksi_status.equalsIgnoreCase("Dikirim") ){
                                actionCheckout.setText("Terima Pesanan");
                                actionCheckout.setOnClickListener(v -> {

                                });
                            }else if( transaksi_status.equalsIgnoreCase("Diterima") ){
                                actionCheckout.setText("Beli Lagi");
                                actionCheckout.setOnClickListener(v -> {

                                });
                            }else if( transaksi_status.equalsIgnoreCase("Dibatalkan") ){
                                actionCheckout.setText("Dibatalkan");
                                actionCheckout.setEnabled(false);
                            }




                            Picasso.with(context).load( jsonObject.getString("barang_gambar") ).into( imageview );

                            txtview.setText( jsonObject.getString("barang_judul") );
                            harga.setText( Config.formatRupiah( jsonObject.getInt("transaksi_harga_total") ) );
                            beli.setText( "x"+jsonObject.getString("transaksi_jumlah") );
                            nota.setText( "#"+jsonObject.getString("transaksi_nota") );
                            waktu_pemesanan.setText( jsonObject.getString("transaksi_tanggal_pesanan") );
                            waktu_pembayaran.setText( jsonObject.getString("transaksi_tanggal_bayar") );
                            waktu_pengiriman.setText( jsonObject.getString("transaksi_tanggal_dikirim") );
                            waktu_pesanan_selesai.setText( jsonObject.getString("transaksi_tanggal_pesanan_selesai") );
                            catatan.setText( jsonObject.getString("transaksi_catatan") );


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



    private class sendDataCheckoutCencel extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/checkout_cencel?uid="+uid+"&key="+key, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            Intent myIntent = new Intent(context, PesananActivity.class);
                            startActivity(myIntent);
                            finish();


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}