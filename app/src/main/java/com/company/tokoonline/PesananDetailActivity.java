package com.company.tokoonline;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.CheckoutAdapter;
import com.company.tokoonline.adapters.PesananAdapter;
import com.company.tokoonline.adapters.PesananDetailAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.TransaksiItem;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PesananDetailActivity extends AppCompatActivity {

    private int key;

    private Context context;
    private SharedPreferences sharedpreferences;

    TextView alamat_pengiriman;
    TextView nota;


    private RecyclerView recyclerView;
    private PesananDetailAdapter pesananDetailAdapter;

    TextView detail_message;
    ImageView detail_icon;

    TextView waktu_pemesanan;
    TextView waktu_pembayaran;
    TextView waktu_pengiriman;
    TextView waktu_pesanan_selesai;
    TextView waktu_pesanan_batal;

    TextView catatan;
    TextView noresi;
    TextView noresi_copy;


    MaterialButton actionBatal;
    MaterialButton actionCheckout;

    RelativeLayout waktu_pembayaran_lyt;
    RelativeLayout waktu_pengiriman_lyt;
    RelativeLayout waktu_pesanan_selesai_lyt;
    RelativeLayout waktu_pesanan_batal_lyt;

    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_detail);


        context = PesananDetailActivity.this;
        key = getIntent().getIntExtra("key",0);
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText( "Loading. Please wait..." );


        alamat_pengiriman = findViewById(R.id.alamat_pengiriman);
        nota = findViewById(R.id.nota);


        recyclerView = findViewById(R.id.recycle_view0);
        recyclerView.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) );

        detail_message = findViewById(R.id.detail_message);
        detail_icon = findViewById(R.id.detail_icon);

        waktu_pembayaran_lyt = findViewById(R.id.waktu_pembayaran_lyt);
        waktu_pengiriman_lyt = findViewById(R.id.waktu_pengiriman_lyt);
        waktu_pesanan_selesai_lyt = findViewById(R.id.waktu_pesanan_selesai_lyt);
        waktu_pesanan_batal_lyt = findViewById(R.id.waktu_pesanan_batal_lyt);

        waktu_pemesanan = findViewById(R.id.waktu_pemesanan);
        waktu_pembayaran = findViewById(R.id.waktu_pembayaran);
        waktu_pengiriman = findViewById(R.id.waktu_pengiriman);
        waktu_pesanan_selesai = findViewById(R.id.waktu_pesanan_selesai);
        waktu_pesanan_batal = findViewById(R.id.waktu_pesanan_batal);

        catatan = findViewById(R.id.catatan);
        noresi = findViewById(R.id.noresi);
        noresi_copy = findViewById(R.id.noresi_copy);

        actionBatal = findViewById(R.id.actionBatal);
        actionCheckout = findViewById(R.id.actionCheckout);

        noresi_copy.setOnClickListener(v->{
            String text = noresi.getText().toString();
            if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
                clipboard.setPrimaryClip(clip);
            }

            Toast.makeText(v.getContext(),"Resi disalin",Toast.LENGTH_SHORT).show();
        });



        waktu_pembayaran_lyt.setVisibility(View.GONE);
        waktu_pengiriman_lyt.setVisibility(View.GONE);
        waktu_pesanan_selesai_lyt.setVisibility(View.GONE);
        waktu_pesanan_batal_lyt.setVisibility(View.GONE);

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


        getDataPesananDetail();
    }


    private void getDataPesananDetail() {

        dialog.show();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/pesanan_detail?uid="+uid+"&key="+key, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {
                    JSONObject jsonObject = req.getJSONObject("response");

                    String transaksi_status = jsonObject.getString("transaksi_status");


                    final List<BarangItem> barangItemList = new ArrayList();
                    JSONArray transaksi_barang_data = jsonObject.getJSONArray("transaksi_barang_data");
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


                    pesananDetailAdapter = new PesananDetailAdapter(context, barangItemList);
                    recyclerView.setAdapter(pesananDetailAdapter);


                    actionBatal.setVisibility(View.GONE);
                    if( transaksi_status.equalsIgnoreCase("Pending") ){


                        detail_message.setText("Pesanan ini menunggu dibayar oleh Pembeli");
                        detail_icon.setImageDrawable(getDrawable(R.drawable.ic_baseline_payment_24));

                        waktu_pembayaran_lyt.setVisibility(View.GONE);
                        waktu_pengiriman_lyt.setVisibility(View.GONE);
                        waktu_pesanan_selesai_lyt.setVisibility(View.GONE);
                        waktu_pesanan_batal_lyt.setVisibility(View.GONE);

                        actionCheckout.setText("Bayar");
                        actionCheckout.setOnClickListener(v -> {

                            Intent myIntent = new Intent(context, PaymentActivity.class);
                            myIntent.putExtra("key", key);
                            startActivity(myIntent);
                            finish();

                        });

                        actionBatal.setVisibility(View.VISIBLE);
                        actionBatal.setOnClickListener(v -> {

                            sendDataCheckoutCencel();

                        });
                    }else if( transaksi_status.equalsIgnoreCase("Diperiksa") ){


                        detail_message.setText("Pembayaran sedang diperiksa oleh Penjual");
                        detail_icon.setImageDrawable(getDrawable(R.drawable.ic_baseline_payment_24));

                        waktu_pembayaran_lyt.setVisibility(View.VISIBLE);
                        waktu_pengiriman_lyt.setVisibility(View.GONE);
                        waktu_pesanan_selesai_lyt.setVisibility(View.GONE);
                        waktu_pesanan_batal_lyt.setVisibility(View.GONE);

                        actionCheckout.setText("Sedang Diperiksa");
                        actionCheckout.setEnabled(false);

                    }else if( transaksi_status.equalsIgnoreCase("Bayar") ){

                        detail_message.setText("Pesananmu sedang dikemas oleh Penjual");
                        detail_icon.setImageDrawable(getDrawable(R.drawable.ic_baseline_delivery_dining_24));

                        waktu_pembayaran_lyt.setVisibility(View.VISIBLE);
                        waktu_pengiriman_lyt.setVisibility(View.GONE);
                        waktu_pesanan_selesai_lyt.setVisibility(View.GONE);

                        actionCheckout.setText("Disiapkan");
                        actionCheckout.setEnabled(false);

                    }else if( transaksi_status.equalsIgnoreCase("Dikirim") ){

                        detail_message.setText("Pesananmu sedang dikirim");
                        detail_icon.setImageDrawable(getDrawable(R.drawable.ic_baseline_delivery_dining_24));

                        waktu_pembayaran_lyt.setVisibility(View.VISIBLE);
                        waktu_pengiriman_lyt.setVisibility(View.VISIBLE);
                        waktu_pesanan_selesai_lyt.setVisibility(View.GONE);
                        waktu_pesanan_batal_lyt.setVisibility(View.GONE);

                        actionCheckout.setText("Terima Pesanan");
                        actionCheckout.setOnClickListener(v -> {
                            sendDataTerima();
                        });
                    }else if( transaksi_status.equalsIgnoreCase("Diterima") ){

                        detail_message.setText("Pesanan selesai");
                        detail_icon.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_circle_outline_24));

                        waktu_pembayaran_lyt.setVisibility(View.VISIBLE);
                        waktu_pengiriman_lyt.setVisibility(View.VISIBLE);
                        waktu_pesanan_selesai_lyt.setVisibility(View.VISIBLE);
                        waktu_pesanan_batal_lyt.setVisibility(View.GONE);

                        actionCheckout.setText("Beli Lagi");
                        actionCheckout.setOnClickListener(v -> {

                            Intent myIntent = new Intent(v.getContext(), DetailActivity.class);
                            myIntent.putExtra("key", key); //Optional parameters
                            v.getContext().startActivity(myIntent);

                        });
                    }else if( transaksi_status.equalsIgnoreCase("Dibatalkan") ){

                        detail_message.setText("Pesanan Dibatalkan");
                        detail_icon.setImageDrawable(getDrawable(R.drawable.ic_baseline_highlight_off_24));

                        waktu_pembayaran_lyt.setVisibility(View.GONE);
                        waktu_pengiriman_lyt.setVisibility(View.GONE);
                        waktu_pesanan_selesai_lyt.setVisibility(View.GONE);
                        waktu_pesanan_batal_lyt.setVisibility(View.VISIBLE);

                        actionCheckout.setText("Dibatalkan");
                        actionCheckout.setEnabled(false);
                    }





                    //Picasso.with(context).load( jsonObject.getString("barang_gambar") ).into( imageview );
                    //txtview.setText( jsonObject.getString("barang_judul") );
                    //harga.setText( Config.formatRupiah( jsonObject.getInt("transaksi_total_bayar") ) );
                    //beli.setText( "x"+jsonObject.getString("transaksi_jumlah") );

                    nota.setText( "#"+jsonObject.getString("transaksi_nota") );

                    waktu_pemesanan.setText( jsonObject.getString("transaksi_tanggal_pesanan") );
                    waktu_pembayaran.setText( jsonObject.getString("transaksi_tanggal_bayar") );
                    waktu_pengiriman.setText( jsonObject.getString("transaksi_tanggal_dikirim") );
                    waktu_pesanan_selesai.setText( jsonObject.getString("transaksi_tanggal_pesanan_selesai") );
                    waktu_pesanan_batal.setText( jsonObject.getString("transaksi_tanggal_batal") );

                    catatan.setText( jsonObject.getString("transaksi_catatan") );

                    String txt_noresi = jsonObject.getString("transaksi_noresi");

                    noresi.setText( txt_noresi );
                    if( TextUtils.isEmpty(txt_noresi) ){
                        noresi_copy.setVisibility(View.GONE);
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



    private void sendDataCheckoutCencel(){

        dialog.show();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/checkout_cencel?uid="+uid+"&key="+key, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();
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



    private void sendDataTerima() {
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.restapi + "/api/pesanan_terima?uid="+uid+"&key="+key, response -> {
            Log.e("VOLLEY", response);
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {


                    getDataPesananDetail();

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