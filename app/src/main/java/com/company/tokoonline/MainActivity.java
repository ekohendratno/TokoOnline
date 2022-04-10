package com.company.tokoonline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.company.tokoonline.adapters.SliderImageAdapter;
import com.company.tokoonline.adapters.TerkiniAdapter;
import com.company.tokoonline.adapters.KategoriAdapter;
import com.company.tokoonline.adapters.RekomendasiAdapter;
import com.company.tokoonline.config.AppController;
import com.company.tokoonline.config.Config;
import com.company.tokoonline.config.PicassoImageLoadingService;
import com.company.tokoonline.models.BarangItem;
import com.company.tokoonline.models.KategoriItem;
import com.company.tokoonline.models.SlideItem;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.ImageLoadingService;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainActivity extends AppCompatActivity {



    private Slider slider;
    private SliderAdapter adapter;
    private RecyclerView recyleviewTerkini;
    private RecyclerView recyleviewKategori;
    private RecyclerView recyleviewRekomendasi;
    private TerkiniAdapter terkiniAdapter;
    private KategoriAdapter kategoriAdapter;
    private RekomendasiAdapter rekomendasiAdapter;

    private Context context;
    private SharedPreferences sharedpreferences;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = MainActivity.this;
        sharedpreferences = getSharedPreferences(Splash.MyPREFERENCES, Context.MODE_PRIVATE);


        findViewById(R.id.pesanan).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), PesananActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);
        });

        findViewById(R.id.akun).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), AkunActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);

        });

        findViewById(R.id.cart).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), CartActivity.class);
            myIntent.putExtra("key", "1"); //Optional parameters
            v.getContext().startActivity(myIntent);
        });

        findViewById(R.id.cari).setOnClickListener(v -> {

            Intent myIntent = new Intent(v.getContext(), PencarianActivity.class);
            v.getContext().startActivity(myIntent);

        });

        findViewById(R.id.pesan).setOnClickListener(v -> {

        });


        slider = findViewById(R.id.banner);
        recyleviewTerkini = findViewById(R.id.recyle_view0);
        recyleviewKategori = findViewById(R.id.recyle_view1);
        recyleviewRekomendasi = findViewById(R.id.recyle_view2);



        slider.init(new PicassoImageLoadingService(this));


        new getDataHome().execute();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            new getDataHome().execute();

            swipeRefresh.setRefreshing(false);
        });

    }








    private class getDataHome extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                RequestQueue requestQueue = AppController.getInstance().getRequestQueue();

                String uid = sharedpreferences.getString("uid", "");

                StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.restapi + "/api/home?uid="+uid, response -> {
                    Log.e("VOLLEY", response);
                    try {

                        final JSONObject req = new JSONObject(response);

                        if( req.getBoolean("success") ) {

                            /**
                             * SET DATA PROMO
                             */

                            final List<SlideItem> sliders = new ArrayList();
                            JSONArray promoJsonArray = req.getJSONArray("promo");
                            for(int a=0; a<promoJsonArray.length() ;a++){
                                JSONObject promo = promoJsonArray.getJSONObject(a);
                                sliders.add(new SlideItem(
                                        promo.getInt("promo_id"),
                                        promo.getString("promo_gambar"),
                                        promo.getString("promo_link")));
                            }

                            slider.setAdapter(new SliderImageAdapter(context,sliders));
                            if(slider.getAdapter() != null && slider.getAdapter().getItemCount() > 1){
                                slider.setInterval(10000);
                            }
                            slider.setOnSlideClickListener(position -> {

                                String link = sliders.get(position).slider_link;
                                if(!TextUtils.isEmpty(link) ){
                                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse( link )));
                                }
                            });




                            /**
                             * SET DATA TERKINI
                             */
                            final List<BarangItem> barangItemList = new ArrayList();
                            JSONArray barangItemArray= req.getJSONArray("terkini");
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
                                        terkini.getString("barang_tanggal_diubah")
                                ));
                            }

                            recyleviewTerkini.setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) );
                            terkiniAdapter = new TerkiniAdapter(context, barangItemList);
                            recyleviewTerkini.setAdapter(terkiniAdapter);

                            /**
                             * SET DATA KETEGORI
                             */
                            final List<KategoriItem> kategoriItemList = new ArrayList();
                            JSONArray kategoriItemArray= req.getJSONArray("kategori");
                            for(int a=0; a<kategoriItemArray.length() ;a++){
                                JSONObject promo = kategoriItemArray.getJSONObject(a);
                                kategoriItemList.add(new KategoriItem(
                                        promo.getInt("kategori_id"),
                                        promo.getString("kategori_judul"),
                                        promo.getString("kategori_parent"),
                                        promo.getString("kategori_gambar")
                                ));
                            }

                            recyleviewKategori.setLayoutManager( new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) );
                            kategoriAdapter = new KategoriAdapter(context, kategoriItemList);
                            recyleviewKategori.setAdapter(kategoriAdapter);

                            /**
                             * SET DATA REKOMENDASI
                             */
                            final List<BarangItem> barangRekomendasiItemList = new ArrayList();
                            JSONArray barangRekomendasiItemArray = req.getJSONArray("rekomendasi");
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
                                        rekomendasi.getString("barang_tanggal_diubah")
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