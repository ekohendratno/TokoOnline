package com.company.tokoonline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
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

import cn.pedant.SweetAlert.SweetAlertDialog;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;

public class KategoriActivity extends AppCompatActivity {


    private String key;

    private RecyclerView recyleviewRekomendasi;
    private RekomendasiAdapter rekomendasiAdapter;

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    private TextView kategori;

    private SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori);


        context = KategoriActivity.this;
        key = getIntent().getStringExtra("key");
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        dialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText( "Loading. Please wait..." );

        kategori = findViewById(R.id.kategori);
        recyleviewRekomendasi = findViewById(R.id.recycle_view0);

        kategori.setText(key.toUpperCase());



        getDataKategori();


        findViewById(R.id.refresh).setOnClickListener(v->{
            getDataKategori();
        });

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            getDataKategori();

            swipeRefresh.setRefreshing(false);
        });


        findViewById(R.id.actionBack).setOnClickListener(v -> {

            onBackPressed();

        });


    }








    private void getDataKategori() {

        dialog.show();

        RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

        String uid = sharedpreferences.getString("uid", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/produk_bykategori?uid="+uid+"&key="+key, response -> {
            Log.e("VOLLEY", response);
            dialog.dismiss();
            try {

                final JSONObject req = new JSONObject(response);

                if( req.getBoolean("success") ) {

                    /**
                     * SET DATA REKOMENDASI
                     */
                    final List<BarangItem> barangRekomendasiItemList = new ArrayList();
                    JSONArray barangRekomendasiItemArray = req.getJSONArray("response");
                    for(int a=0; a<barangRekomendasiItemArray.length() ;a++){
                        JSONObject rekomendasi = barangRekomendasiItemArray.getJSONObject(a);
                        barangRekomendasiItemList.add(new BarangItem(
                                rekomendasi.getInt("barang_id"),
                                rekomendasi.getString("barang_judul"),
                                rekomendasi.getString("barang_katerangan"),
                                rekomendasi.getString("barang_kategori"),
                                rekomendasi.getString("barang_variasi"),
                                rekomendasi.getString("barang_ukuran"),
                                rekomendasi.getInt("barang_berat"),
                                rekomendasi.getInt("barang_stok"),
                                rekomendasi.getInt("barang_harga"),
                                rekomendasi.getInt("barang_diskon"),
                                rekomendasi.getInt("barang_terjual"),
                                rekomendasi.getString("barang_gambar"),
                                rekomendasi.getString("barang_tanggal"),
                                rekomendasi.getString("barang_tanggal_diubah"),
                                rekomendasi.getString("barang_status")
                        ));
                    }
                    recyleviewRekomendasi.setLayoutManager( new GridLayoutManager(context, 2) );
                    rekomendasiAdapter = new RekomendasiAdapter(context, barangRekomendasiItemList);
                    recyleviewRekomendasi.setAdapter(rekomendasiAdapter);





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