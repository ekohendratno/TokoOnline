package com.company.tokoonline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.SliderImageAdapter;
import com.company.tokoonline.adapters.TerkaitAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.config.PicassoImageLoadingService;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.SlideItem;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ss.com.bannerslider.Slider;

public class DetailActivity extends AppCompatActivity {

    private int key;

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;


    AppCompatTextView judul;
    AppCompatTextView kategori;
    AppCompatTextView stok;
    AppCompatTextView harga;
    AppCompatTextView keterangan;

    AppCompatTextView harga_total;
    AppCompatImageView action_jumlah_beli_min;
    AppCompatImageView action_jumlah_beli_plus;
    AppCompatEditText edit_jumlah_beli;

    MaterialButton actionCart;
    MaterialButton actionBuy;

    private SweetAlertDialog dialog;


    private RecyclerView recyleviewTerkait;
    private TerkaitAdapter terkaitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        context = DetailActivity.this;
        key = getIntent().getIntExtra("key",0);
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText( "Loading. Please wait..." );

        judul = findViewById(R.id.judul);
        kategori = findViewById(R.id.kategori);
        stok = findViewById(R.id.stok);
        harga = findViewById(R.id.harga);
        keterangan = findViewById(R.id.keterangan);

        harga_total = findViewById(R.id.harga_total);
        action_jumlah_beli_min = findViewById(R.id.action_jumlah_beli_min);
        action_jumlah_beli_plus = findViewById(R.id.action_jumlah_beli_plus);
        edit_jumlah_beli = findViewById(R.id.edit_jumlah_beli);

        actionCart = findViewById(R.id.actionCart);
        actionBuy = findViewById(R.id.actionBuy);


        recyleviewTerkait = findViewById(R.id.recyleviewTerkait);

        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });

        actionCart.setOnClickListener(v -> {

            sendDataKeranjang();

        });


        actionBuy.setOnClickListener(v -> {
            String beli = edit_jumlah_beli.getText().toString();

            Log.e("beli",beli);

            Intent myIntent = new Intent(v.getContext(), CheckOutActivity.class);
            myIntent.putExtra("key", key);
            myIntent.putExtra("beli", Integer.parseInt(beli));
            startActivity(myIntent);

        });



        getDataProdukDetail();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            getDataProdukDetail();

            swipeRefresh.setRefreshing(false);
        });



    }

    private void getDataProdukDetail(){
        actionBuy.setEnabled(false);
        actionCart.setEnabled(false);
        dialog.show();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/detail?uid="+uid+"&key="+key, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();
            actionBuy.setEnabled(true);
            actionCart.setEnabled(true);
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {
                    getDataProdukTerkait();


                    JSONObject jsonObject = req.getJSONObject("response");

                    Slider slider = findViewById(R.id.slider);
                    slider.init(new PicassoImageLoadingService(context));

                    int barang_stok = jsonObject.getInt("barang_stok");
                    int barang_harga = jsonObject.getInt("barang_harga");


                    judul.setText( jsonObject.getString("barang_judul") );
                    kategori.setText( jsonObject.getString("barang_kategori") );

                    harga.setText(  Config.formatRupiah( barang_harga ) );
                    keterangan.setText( jsonObject.getString("barang_katerangan") );



                    JSONArray gambars = jsonObject.getJSONArray("barang_gambars");


                    final List<SlideItem> gambarsList = new ArrayList();
                    for(int a=0; a<gambars.length() ;a++){
                        gambarsList.add(new SlideItem(a, gambars.get(a).toString(), ""));
                    }

                    slider.setAdapter(new SliderImageAdapter(context,gambarsList));

                    edit_jumlah_beli.setEnabled(false);



                    int s = barang_stok;
                    int h = barang_harga;

                    if( s >= 1 ){
                        stok.setText( "Tersedia "+barang_stok+" barang" );
                        edit_jumlah_beli.setText( "1" );

                        action_jumlah_beli_min.setEnabled(true);
                        action_jumlah_beli_plus.setEnabled(true);
                        actionBuy.setEnabled(true);
                    }else{
                        stok.setText( "Habis" );
                        actionBuy.setText( "Habis" );

                        edit_jumlah_beli.setText( "0" );

                        action_jumlah_beli_min.setEnabled(false);
                        action_jumlah_beli_plus.setEnabled(false);
                        actionBuy.setEnabled(false);
                    }


                    //set harga setelah ditampilkan
                    int beli_sebelum = Integer.parseInt( edit_jumlah_beli.getText().toString() );
                    harga_total.setText( Config.formatRupiah( beli_sebelum*h) );


                    action_jumlah_beli_min.setOnClickListener(v->{
                        int beli_minus = Integer.parseInt( edit_jumlah_beli.getText().toString() );
                        int beli_minus_setelah = beli_minus-1;

                        if( beli_minus_setelah > 0){
                            edit_jumlah_beli.setText(String.valueOf( beli_minus_setelah ));
                            harga_total.setText( Config.formatRupiah( beli_minus_setelah*h ) );
                        }

                    });
                    action_jumlah_beli_plus.setOnClickListener(v->{
                        int beli_plus = Integer.parseInt( edit_jumlah_beli.getText().toString() );
                        int beli_plus_setelah = beli_plus+1;


                        if( beli_plus_setelah > 0 && beli_plus_setelah <= s){

                            edit_jumlah_beli.setText(String.valueOf( beli_plus_setelah ));
                            harga_total.setText( Config.formatRupiah( beli_plus_setelah*h ) );

                        }

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

    private void getDataProdukTerkait(){
        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();
        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/produk_terkait?uid="+uid+"&key="+key, response -> {
            Log.e("VOLLEY", response);
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {
                    JSONArray barangItemArray = req.getJSONArray("response");

                    final List<BarangItem> barangItemList = new ArrayList();
                    for(int a=0; a<barangItemArray.length() ;a++){
                        JSONObject terkini = barangItemArray.getJSONObject(a);
                        barangItemList.add(new BarangItem(
                                terkini.getInt("barang_id"),
                                terkini.getString("barang_judul"),
                                terkini.getString("barang_katerangan"),
                                terkini.getString("barang_kategori"),
                                terkini.getString("barang_variasi"),
                                terkini.getString("barang_ukuran"),
                                terkini.getInt("barang_berat"),
                                terkini.getInt("barang_stok"),
                                terkini.getInt("barang_harga"),
                                terkini.getInt("barang_diskon"),
                                terkini.getInt("barang_terjual"),
                                terkini.getString("barang_gambar"),
                                terkini.getString("barang_tanggal"),
                                terkini.getString("barang_tanggal_diubah"),
                                terkini.getString("barang_status")
                        ));
                    }

                    recyleviewTerkait.setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) );
                    terkaitAdapter = new TerkaitAdapter(context, barangItemList);
                    recyleviewTerkait.setAdapter(terkaitAdapter);

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

    }


    private void sendDataKeranjang(){
        String jumlah = edit_jumlah_beli.getText().toString();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.restapi + "/api/keranjang_simpan?uid="+uid+"&key="+key, response -> {
            Log.e("VOLLEY", response);
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {

                    Intent myIntent = new Intent(context, CartActivity.class);
                    startActivity(myIntent);
                    finish();

                }else{

                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oops...")
                            .setContentText( req.getString("response") )
                            .show();

                }
            } catch (Exception e) {
                Log.e("VOLLEY","Authentication error: " + e.getMessage());

            }
        }, error -> {

            new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Terjadi gangguan!")
                    .setContentText("Mengalami gangguan ketika mengambil data!")
                    .show();

            Log.e("VOLLEY", error.toString());

        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("jumlah", String.valueOf(jumlah));
                params.put("barang", String.valueOf(key));

                return params;
            }
        };



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